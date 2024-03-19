package com.zn.tomcat.impl;

import com.zn.servlet.*;
import com.zn.servlet.listener.*;
import com.zn.tomcat.listener.ListenerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TomcatHttpServletRequest implements HttpServletRequest {

    public boolean initSessionMark;

    /**
     * 暂存的Session
     */
    public HttpSession currentSession;

    /**
     * 储存所有的cookie
     */
    private Cookie[] cookies;
    /**
     * 本次请求报文
     */
    private String requestContent;
    /**
     * 请求行
     */
    private final String requestLine;

    /**
     * 请求头数组
     */
    private final String[] headers;

    /**
     * 请求体
     */
    private final String requestBody;

    /**
     * 记录请求的URI
     */
    private String URI;

    /**
     * 记录请求的URL
     */
    private String URL;

    /**
     * 储存查询字符串参数的key-value
     */
    private final Map<String, String> paramsMap = new HashMap<>();
    /**
     * 请求体字节数组
     */
    private byte[] requestBodyBytes;

    private Map<String, Object> attributeMap;

    private ServletRequestListener listener;
    private ServletRequestAttributeListener attributeListener;
    private ServletRequestEvent servletRequestEvent;

    /**
     * 构造的时候直接解析请求报文，获取到报文的内容
     *
     * @param inputStream 传进来的流
     */
    public TomcatHttpServletRequest(InputStream inputStream, String serverIp, int localPort) throws InstantiationException, IllegalAccessException {
        try {
            attributeMap = new HashMap<>();
            //读取完整的报文信息
            byte[] bytes = new byte[8 * 1024];
            int read = inputStream.read(bytes);
            //得到请求报文
            requestContent = new String(bytes, 0, read, StandardCharsets.ISO_8859_1);
            /*
            请求报文以换行为分割，第一行一定是请求行，换行后就是请求头了
            所以第一个换行的位置开始就是请求头开始出现的位置
             */
            int headerBegin = requestContent.indexOf("\n");
            /*
            请求头结束之后是空行,最后一个请求头结束会有一个\n，紧接着是一个\n空行，那么\n\n将是请求头结束的标志
            \r是回到当前行的首部，其实每一次换行都要\r回到首部，所以应该是\r\n\r\n为标志
             */
            int headerEnd = requestContent.indexOf("\r\n\r\n");
            String headersString = requestContent.substring(headerBegin + 1, headerEnd);
            //每一组头也都是换行分割的，也就是\r\n,这样得到每一组的请求头
            this.headers = headersString.split("\r\n");
            //拆分出请求头部分的Cookie
            this.parseCookie();

            //获取到请求行
            this.requestLine = requestContent.substring(0, headerBegin);
            //拿到请求行之后直接将其拆分,拿到URI,URL(ip,端口号)
            this.parseRequestLine(serverIp, localPort);

            /*
            对于非二进制类型的请求(文件),8*1024几乎已经足够一次性的读到全部的报文
            然而对于二进制类型的动辄几个GB显然是不够的
            对于后者，此时的requestBody中应该放着的是，行，头，空行，体(一部分，可能有可能没有)
             */
            this.requestBody = requestContent.substring(headerEnd + 4);
            //解析请求体
            this.parseRequestBody(headerEnd + 4, inputStream);

            this.listener = (ServletRequestListener) ListenerFactory.getListener(this);
            this.attributeListener = (ServletRequestAttributeListener) ListenerFactory.getAttributeListener(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseCookie() {
        String allCookie = this.getHeader("cookie");
        if (allCookie != null) {
            String[] cookiesKV = allCookie.split(";");
            this.cookies = new Cookie[cookiesKV.length];
            for (int i = 0; i < cookiesKV.length; i++) {
                String[] keyAndValue = cookiesKV[i].trim().split("=");
                TomcatCookie tomcatCookie = new TomcatCookie(keyAndValue[0], keyAndValue[1]);
                this.cookies[i] = tomcatCookie;
            }
        }
    }

    @Override
    public Cookie[] getCookies() {
        return this.cookies;
    }

    @Override
    public void setAttribute(String key, Object value) {
        ServletRequestAttributeEvent servletRequestAttributeEvent;
        for (String temp : attributeMap.keySet()) {
            if (temp.equals(key)) {
                servletRequestAttributeEvent = new ServletRequestAttributeEvent(this, temp, this.attributeMap.get(temp));
                this.attributeListener.updateAttribute(servletRequestAttributeEvent);
                this.attributeMap.put(key, value);
                return;
            }
        }
        servletRequestAttributeEvent = new ServletRequestAttributeEvent(this, key, value);
        this.attributeListener.addAttribute(servletRequestAttributeEvent);
        this.attributeMap.put(key, value);
    }

    @Override
    public Object getAttribute(String key) {
        return this.attributeMap.get(key);
    }

    @Override
    public void removeAttribute(String key) {
        ServletRequestAttributeEvent servletRequestAttributeEvent = new ServletRequestAttributeEvent(this, key, this.attributeMap.get(key));
        this.attributeListener.removeAttribute(servletRequestAttributeEvent);
        this.attributeMap.remove(key);
    }

    @Override
    public String getHeader(String key) {
        for (String headers : this.headers) {
            int symbol = headers.indexOf(":");
            String headerKey = headers.substring(0, symbol);
            //对key进行比较(忽略大小写)
            if (headerKey.equalsIgnoreCase(key))
                return headers.substring(symbol + 2);
        }
        return null;
    }

    @Override
    public String[] getHeaderNames() {
        String[] headerName = new String[headers.length];
        int j = 0;
        for (String headers : this.headers) {
            int symbol = headers.indexOf(":");
            String key = headers.substring(0, symbol);
            headerName[j++] = key;
        }
        return headerName;
    }

    /**
     * 对requestBody进行拆解，如果请求体中存在表单数据，那么也应该放在paramsMap中
     * 调用getParameter时也能拿到K对应的V
     */
    private void parseRequestBody(int requestBodyBean, InputStream inputStream) throws IOException {
        String header = this.getHeader("Content-Type");
        //如果没有Content-Type这个头，直接return
        if (header == null)
            return;
        //如果请求体中是表单数据
        if (header.contains("application/x-www-form-urlencoded")) {
            String[] params = requestBody.split("&");
            for (String param : params) {
                String[] paramKV = param.split("=");
                this.paramsMap.put(paramKV[0], paramKV[1]);
            }
            return;
        }
        //如果请求体中是二进制(文件)
        if (header.trim().contains("multipart/form-data")) {
            this.parseMultipartDate(requestBodyBean, inputStream);
        }
    }

    private void parseMultipartDate(int requestBodyBeginIndex, InputStream inputStream) throws IOException {
        //拿到8*1024大小的请求报文中的一部分请求体内容
        String aLittleRequestBody = this.requestBody.substring(requestBodyBeginIndex);
        byte[] aLittleRequestBodyBytes = aLittleRequestBody.getBytes(StandardCharsets.ISO_8859_1);
        //把这一部分先写进 字节数组输出流
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(aLittleRequestBodyBytes);
        //拿到剩余的请求体应该的长度，content-length头的value-一小部分的请求的长度
        int residueBodyBytesLength = Integer.getInteger(this.getHeader("content-length")) - aLittleRequestBodyBytes.length;
        //http请求是分片的，一次最多能读读65536字节
        byte[] maxBytes = new byte[65536];

        //对于超过65536的大小，就要循环的读
        int tempLength = 0;
        while (tempLength < residueBodyBytesLength) {
            int length = inputStream.read(maxBytes);
            tempLength += length;
            byteArrayOutputStream.write(maxBytes, 0, length);
        }
        this.requestBodyBytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
    }

    /**
     * 拿到URI,协议，拼接URL
     */
    private void parseRequestLine(String serverIp, int localPort) {

        //拿到URI和URL，构造的时候就执行了，只执行了一次
        String[] split = this.requestLine.split(" ");
        URI = split[1];
        URL = split[2].split("/")[0].toLowerCase() + "://" + serverIp + ":" + localPort + (URI.equals("/") ? "" : URI);

        //查验URI是否有查询字符串参数或者参数是否合法
        int symbol = URI.indexOf("?");
        //URI为/(没有查询字符串参数),没有"?"，有"?"却没有k=v结构
        if (URI.equals("/") || symbol == -1 || (URI.contains("=")))
            return;

        //将查询字符串参数的KEY和Value存进Map，也只执行了一次
        String paramsString = URI.substring(symbol + 1);
        String[] params = paramsString.split("&");
        for (String param : params) {
            String[] paramKV = param.split("=");
            this.paramsMap.put(paramKV[0], paramKV[1]);
        }
    }


    @Override
    public String getMethod() {
        return this.requestLine.split(" ")[0];
    }

    @Override
    public String getRemoteURL() {
        return this.URL;
    }

    @Override
    public String getRemoteURI() {
        return this.URI;
    }

//    @Override
//    public InetAddress getRemoteAddr() {
//        return socket.getInetAddress();
//    }

    @Override
    public String getParameter(String key) {
        return this.paramsMap.get(key);
    }

    private String getRequestBody() {
        return this.requestBody;
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new StringReader(this.requestBody));
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.requestBodyBytes);
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String uri) {
        return new TomcatRequestDispatcher(uri);
    }

    @Override
    public HttpSession getSession() {
        //判断此次请求是否有Cookie
        if (this.cookies != null) {
            for (Cookie cookie : cookies) {
                //遍历每一个cookie，如果存在JSESSIONID的值
                if ("JSESSIONID".equals(cookie.getKey().trim())) {
                    //尝试根据sessionId值去session容器中获取
                    this.currentSession = SessionManager.getSession(Integer.parseInt(cookie.getValue()));
                    if (this.currentSession == null) {
                        this.initSessionMark = true;
                        this.currentSession = SessionManager.initAndGetSession();
                    }
                }
                return this.currentSession;
            }
        }
        //否则，可能有两种情况：1.客户端虽然发送了cookie但是没有JSESSIONID这个cookie，2.客户端压根没有cookie
        this.initSessionMark = true;
        return this.currentSession = SessionManager.initAndGetSession();
    }

    public ServletContext getServletContext() {
        return TomcatServletContext.getServletContext();
    }

    public ServletRequestListener getListener() {
        return listener;
    }

    public ServletRequestEvent getServletRequestEvent() {
        return this.servletRequestEvent;
    }

    public void setServletRequestEvent(ServletRequestEvent servletRequestEvent) {
        this.servletRequestEvent = servletRequestEvent;
    }

    public ServletRequestAttributeListener getAttributeListener() {
        return attributeListener;
    }
}
