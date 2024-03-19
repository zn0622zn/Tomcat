package com.zn.tomcat.impl;

import com.zn.servlet.*;
import com.zn.tomcat.TomcatStart;
import com.zn.tomcat.prepare.PrepareHandler;

import java.io.IOException;
import java.io.PrintWriter;

public class TomcatRequestDispatcher implements RequestDispatcher {

    /**
     * 重定向的uri
     */
    private String uri;

    public TomcatRequestDispatcher(String uri) {
        this.uri = uri;
    }

    private HttpServlet getHttpServlet(HttpServletRequest request, HttpServletResponse response) {
        HttpServlet httpServlet = null;
        try {
            //拿到PrepareHandler中储存的uri和全限定类名的映射，对其遍历
            for (String uri : PrepareHandler.URIMapping.keySet()) {
                //如果里面存在我们要重定向的uri
                if (uri.equals(this.uri)) {
                    //拿到要重定向的uri类的全限定类名
                    String currentClassName = PrepareHandler.URIMapping.get(uri);
                    //根据全限定类名拿到去 全限定类名和servlet类的映射中拿到 servlet
                    httpServlet = TomcatStart.servletMap.get(currentClassName);
                    //如果httpServlet为空，说明这个uri对应的实例是第一次创建
                    if (httpServlet == null) {
                        //根据全限定类名反射创建对应的httpServlet
                        Class<?> aClass = Class.forName(currentClassName);
                        httpServlet = (HttpServlet) aClass.newInstance();
                        httpServlet.init();
                        //将uri和httpServlet类存入 这个映射中
                        TomcatStart.servletMap.put(currentClassName, httpServlet);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpServlet;
    }

    @Override
    public void include(HttpServletRequest request, HttpServletResponse response) {
        try {
            this.getHttpServlet(request,response).service(request, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void forward(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter writer = response.getWriter();
            writer.flush();
            response.reset();
            HttpServlet httpServlet = this.getHttpServlet(request, response);
            httpServlet.service(request,response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
