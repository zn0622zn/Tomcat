package com.zn.tomcat;

import com.zn.servlet.*;
import com.zn.servlet.listener.*;
import com.zn.tomcat.impl.TomcatHttpServletRequest;
import com.zn.tomcat.impl.TomcatHttpServletResponse;
import com.zn.tomcat.impl.TomcatServletContext;
import com.zn.tomcat.listener.ListenerFactory;
import com.zn.tomcat.prepare.PrepareHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * tomcat启动时候的准备，每次更改代码重启包括重新启动一下这个类
 */
public class TomcatStart {

    private static final String path = "C:\\Users\\张nan\\Desktop\\colin_class\\tomcat\\src";

    /**
     * 用于储存全限定类名和Servlet类的映射关系，解决servlet是单例的问题
     */
    public static final Map<String, HttpServlet> servletMap = new HashMap<>();

    public static void main(String[] args) throws Exception {
        //定义一个ServletSocket对象 向系统注册一个服务 声明当前应用进程可以发送和接收数据
        ServerSocket serverSocket = new ServerSocket();
        //将serverSocket绑定在特定的ip地址和端口号
        serverSocket.bind(new InetSocketAddress("localhost", 8080));
        //拿到本次请求的Ip地址
        String serverIp = serverSocket.getInetAddress().getHostName();
        //拿到套接字正在监听的端口号
        int localPort = serverSocket.getLocalPort();

        //获取到项目路径，用获取这个路径下类和文件的信息
        File file = new File(path);
        PrepareHandler prepareHandler = new PrepareHandler();
        //拿到path路径下所有的类的类名
        List<String> allClasses = prepareHandler.getAllClasses(file);
        //拿到Map集合，存的是WebServlet注解的URI和这个类类名的绑定关系
        Map<String, String> UriMapping = prepareHandler.initURIMapping(allClasses);
        TomcatServletContext servletContext = (TomcatServletContext) TomcatServletContext.getServletContext();
        ServletContextListener applicationListener = (ServletContextListener) ListenerFactory.getListener(servletContext);
        applicationListener.init(servletContext.getServletContextEvent());
        ServletContextAttributeListener attributeListener = (ServletContextAttributeListener) ListenerFactory.getAttributeListener(servletContext);
        servletContext.setServletContextAttributeListener(attributeListener);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            applicationListener.destroyed(servletContext.getServletContextEvent());
        }));

        //----------------------------------------------------------------------------------------------
        //-----------------------------👆           zn___zn             👇------------------------------
        //----------------------------------------------------------------------------------------------
        while (true) {
            //未获取请求时阻塞，直到有请求来到
            Socket socket = serverSocket.accept();
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            TomcatHttpServletRequest request = new TomcatHttpServletRequest(inputStream, serverIp, localPort);
            ServletRequestListener listener = request.getListener();
            //request监听器的生成
            if (listener != null) {
                ServletRequestEvent servletRequestEvent = new ServletRequestEvent(request);
                request.setServletRequestEvent(servletRequestEvent);
                listener.initRequest(servletRequestEvent);
            }
            TomcatHttpServletResponse response = new TomcatHttpServletResponse(outputStream);

            //拿到当前请求的URI-包含查询字符串参数
            String remoteURI = request.getRemoteURI();
            //如果URI是"/"，即没有URI
            if (remoteURI.equals("/")) {
                //返回html页面，设置头(我们定义的默认的头是，纯文本类型)
                response.setHeader("Content-Type", "text/html;charset=utf8");
                response.getWriter().write("<h1>这就是Zn-Tomcat的首页<h1>");
                response.finished();
                continue;
            }
            //拿到去掉了查询字符串参数的URI，也是URI映射中的URI
            int symbol = remoteURI.indexOf("?");
            //如果存在"?"
            if (symbol != -1) {
                remoteURI = remoteURI.substring(0, symbol);
            }
            System.err.println(remoteURI);
            //标志量
            boolean flag = false;
            //拿到映射关系Map中的每一个key
            for (String uri : UriMapping.keySet()) {
                //如果请求的URI是映射中的某一个
                if (uri.equals(remoteURI)) {
                    //从URI和全限定类名映射中拿到全限定类名
                    String className = UriMapping.get(uri);
                    //从全限定类名和Servlet类中拿到对应类名的Servlet类
                    //这里主要解决了单例问题，即整个服务过程中Servlet只有一个
                    HttpServlet currentServlet = servletMap.get(className);
                    //如果从映射Map中没有拿到Servlet，说明这是第一次请求
                    if (currentServlet == null) {
                        //通过类名的反射得到这个Servlet类，并且存在映射Map中，下次不用创建了直接拿出来用
                        Class<?> aClass = Class.forName(className);
                        currentServlet = (HttpServlet) aClass.newInstance();
                        servletMap.put(className, currentServlet);
                    }
                    //如果在类名和Servlet类中拿到了Servlet，说明之前已经创建过了，这不是第一次访问
                    currentServlet.init();
                    currentServlet.service(request, response);
                    response.finished();
                    //标志量设置为true
                    flag = true;
                }
            }
            //如果URI没有匹配上，返回404的页面
            if (!flag) {
                response.setHeader("Content-Type", "text/html;charset=utf8");
                response.getWriter().write("<!doctype html>\n" +
                        "   <html lang=\"zh\">\n" +
                        "      <head>\n" +
                        "         <title>\n" +
                        "            HTTP状态 404 - 未找到\n" +
                        "         </title>\n" +
                        "         <style type=\"text/css\">\n" +
                        "            body {font-family:Tahoma,Arial,sans-serif;} h1, h2, h3, b {color:white;background-color:#525D76;} h1 {font-size:22px;} h2 {font-size:16px;} h3 {font-size:14px;} p {font-size:12px;} a {color:black;} .line {height:1px;background-color:#525D76;border:none;}\n" +
                        "         </style>\n" +
                        "</head>\n" +
                        "      <body>\n" +
                        "         <h1>\n" +
                        "            HTTP状态 404 - 未找到\n" +
                        "         </h1>\n" +
                        "         <hr class=\"line\" />\n" +
                        "         <p>\n" +
                        "            <b>类型</b> 状态报告\n" +
                        "         </p>\n" +
                        "         <p>\n" +
                        "            <b>消息</b> 请求的资源[" + request.getRemoteURI() + "]不可用\n" +
                        "         </p>\n" +
                        "         <p>\n" +
                        "            <b>描述</b> 源服务器未能找到目标资源的表示或者是不愿公开一个已经存在的资源表示。\n" +
                        "         </p>\n" +
                        "         <hr class=\"line\" />\n" +
                        "         <h3>\n" +
                        "            Zn Tomcat/0.0.1\n" +
                        "         </h3>\n" +
                        "</body>\n" +
                        "</html>");
                response.finished();
            }

            //如果此次request涉及到新创建session的问题吗，需要手动添加响应cookie并返回
            if (request.initSessionMark) {
                response.addCookie(new Cookie("JSESSIONID", request.currentSession.getId() + " "));
                response.finished();
            }
            //request监听器的销毁
            if (request.getListener() != null) {
                request.getListener().destroyed(request.getServletRequestEvent());
            }

            inputStream.close();
            outputStream.close();
            socket.close();
        }
    }
}
