package com.zn.servlet.listener;

public interface ServletRequestListener extends Listener {

    default void initRequest(ServletRequestEvent servletRequestEvent) {

    }

    default void destroyed(ServletRequestEvent servletRequestEvent) {

    }
}
