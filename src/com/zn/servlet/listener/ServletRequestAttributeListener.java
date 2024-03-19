package com.zn.servlet.listener;

public interface ServletRequestAttributeListener extends Listener{
    default void addAttribute(ServletRequestAttributeEvent servletRequestAttributeEvent) {

    }

    default void updateAttribute(ServletRequestAttributeEvent servletRequestAttributeEvent) {

    }


    default void removeAttribute(ServletRequestAttributeEvent servletRequestAttributeEvent) {

    }
}
