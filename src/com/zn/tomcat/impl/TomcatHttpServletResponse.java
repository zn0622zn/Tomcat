package com.zn.tomcat.impl;

import com.zn.servlet.Cookie;
import com.zn.servlet.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TomcatHttpServletResponse implements HttpServletResponse {
    /**
     * 初始化构想响应报文的封装对象
     * 不能一开始就向流中写入数据，如果这样，那么后续用户对数据的修改就是在流中追加
     * 而不是真实的在流中修改了
     * 所以应该是在构建的时候就把行，头，体的载体准备好
     * 在关流之前统一把各个信息写入流
     *
     * @param outputStream
     */
    public TomcatHttpServletResponse(OutputStream outputStream) {
        this.cookieBuilderList = new LinkedList<>();
        this.outputStream = outputStream;
        this.headerMap = new HashMap<>();
        //tomcat默认会给很多头，为了解决中文乱码，暂且默认给一个
        headerMap.put("Content-Type", "text/plain;charset=utf8");
        //默认响应状态为200
        this.status = 200;
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        //将用户写的内容转换成字节数组，最后可以用outputSteam写入
        this.printWriter = new PrintWriter(byteArrayOutputStream);
        //给状态码和对应信息初始化
        this.statusMapping();
    }

    private List<StringBuilder> cookieBuilderList;

    /**
     * 用于记录响应状态码
     */
    private Integer status;

    /**
     * 用于存储响应状态码和对应信息
     */
    public static Map<Integer, String> statusMessageMapping;

    /**
     * 记录响应头
     */
    private Map<String, String> headerMap;

    /**
     * 这是用户要往响应体里写的内容的流
     */
    private PrintWriter printWriter;

    /**
     * 我们最终要写进响应体，要依靠与字节数组，但是用户写的是PrintWriter\
     * 我们要用字节数组输出流将内容存下来，然后传进OutputStream
     */
    private ByteArrayOutputStream byteArrayOutputStream;

    /**
     * 这是真正要写响应体里的所依靠的字节输出流
     */
    private OutputStream outputStream;

    /**
     * 响应行和响应头
     */
    private StringBuffer stringBuffer;

    @Override
    public PrintWriter getWriter() {
        return this.printWriter;
    }

    private void statusMapping() {
        statusMessageMapping = new HashMap<>();
        statusMessageMapping.put(200, "OK");
        statusMessageMapping.put(302, "Moved Temporarily");
        statusMessageMapping.put(404, "Not Found");
        statusMessageMapping.put(405, "Method Not Allowed");
        statusMessageMapping.put(500, "Internal Server Error");
        //去HttpStatus.txt中寻找
    }

    @Override
    public void setHeader(String key, String value) {
        this.headerMap.put(key, value);
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public void sendRedirect(String uri) {
        this.setStatus(302);
        this.setHeader("Location", uri);
    }

    @Override
    public void reset() {
        this.byteArrayOutputStream.reset();
    }

    @Override
    public void addCookie(Cookie cookie) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(cookie.getKey())
                .append("=")
                .append(cookie.getValue())
                .append("; ")
                .append("Path=")
                .append(cookie.getPath())
                .append("; ");
        if (cookie.getMaxAge() > 0) {
            stringBuilder
                    .append("Max-Age=")
                    .append(cookie.getMaxAge())
                    .append("; ")
                    .append("Expires=")
                    .append(cookie.getExpires())
                    .append("; ");
        }
        if (cookie.getHttpOnly()) {
            stringBuilder.append("HttpOnly");
        }
        this.cookieBuilderList.add(stringBuilder);
    }

    /**
     * 得到相应行和响应头
     */
    private void encapsulation() {
        this.stringBuffer = new StringBuffer();
        //拼接响应行
        this.stringBuffer.append("HTTP/1.1 ").append(this.status).append(" ").append(statusMessageMapping.get(this.status)).append("\r\n");

        //遍历用于拼接响应头
        for (String headerKey : headerMap.keySet()) {
            this.stringBuffer.append(headerKey).append(": ").append(headerMap.get(headerKey)).append("\r\n");
        }
    }

    /**
     * 用于关流前响应报文内容的提交
     */
    public void finished() throws IOException {
        //为了防止PrintWriter还在缓冲区为刷新，手动刷一下
        this.printWriter.flush();
        //拿到响应体的字节数组
        byte[] bodyArray = this.byteArrayOutputStream.toByteArray();
        //写入响应行和响应头，无响应体
        this.encapsulation();
        this.outputStream.write(this.stringBuffer.toString().getBytes(StandardCharsets.UTF_8));
        //将cookie的list集合中的每一个元素(cookie信息)写入
        if (cookieBuilderList.size() != 0) {
            for (StringBuilder stringBuilder : this.cookieBuilderList) {
                this.outputStream.write(("Set-Cookie: " + stringBuilder + "\r\n").getBytes(StandardCharsets.UTF_8));
            }
        }
        //拼接响应空行\
        this.outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
        //写入响应体
        this.outputStream.write(bodyArray);

        this.outputStream.close();
        this.byteArrayOutputStream.close();

    }
}
