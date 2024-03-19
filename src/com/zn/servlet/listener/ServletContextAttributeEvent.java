package com.zn.servlet.listener;


import com.zn.servlet.ServletContext;

public class ServletContextAttributeEvent extends ServletContextEvent {
    private String key;
    private Object value;

    public ServletContextAttributeEvent(ServletContext servletContext, String key, Object value) {
        super(servletContext);
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
