package com.zn.servlet.listener;


import com.zn.servlet.ServletContext;

public class ServletContextEvent {
    private ServletContext servletContext;

    public ServletContextEvent(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }
}
