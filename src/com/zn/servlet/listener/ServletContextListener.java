package com.zn.servlet.listener;

public interface ServletContextListener extends Listener{
    default void init(ServletContextEvent servletContextEvent) {

    }

    default void destroyed(ServletContextEvent servletContextEvent) {

    }
}
