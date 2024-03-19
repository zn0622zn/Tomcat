package com.zn.servlet.listener;


import com.zn.servlet.HttpServletRequest;
import com.zn.servlet.ServletContext;

public class ServletRequestEvent {

    private HttpServletRequest httpServletRequest;

    public ServletRequestEvent(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    public HttpServletRequest getServletRequest() {
        return this.httpServletRequest;
    }

    public ServletContext getServletContext() {
        return this.httpServletRequest.getServletContext();
    }
}
