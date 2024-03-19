package com.zn.servlet.listener;

public interface ServletContextAttributeListener extends Listener{
    default void addAttribute(ServletContextAttributeEvent servletContextAttributeEvent) {

    }

    default void updateAttribute(ServletContextAttributeEvent servletContextAttributeEvent) {

    }


    default void removeAttribute(ServletContextAttributeEvent servletContextAttributeEvent) {

    }
}
