package com.zn.tomcat.impl;

import com.zn.servlet.ServletContext;
import com.zn.servlet.listener.ServletContextAttributeEvent;
import com.zn.servlet.listener.ServletContextAttributeListener;
import com.zn.servlet.listener.ServletContextEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 张男
 * @date: 2024/3/12---13:51
 */
public class TomcatServletContext implements ServletContext {

    private ServletContextAttributeListener servletContextAttributeListener;

    /**
     * 存储servletContext域对象的Map
     */
    private Map<String, Object> servletContextAttributes;

    private static ServletContext application = new TomcatServletContext();

    private ServletContextEvent servletContextEvent;

    private TomcatServletContext() {
        this.servletContextAttributes = new ConcurrentHashMap<>();
        this.servletContextEvent = new ServletContextEvent(this);
    }

    public static ServletContext getServletContext() {
        return application;
    }

    @Override
    public void setAttribute(String key, Object value) {
        ServletContextAttributeEvent servletContextAttributeEvent;
        for (String temp : this.servletContextAttributes.keySet()) {
            if (temp.equals(key)) {
                servletContextAttributeEvent = new ServletContextAttributeEvent(this, temp, this.servletContextAttributes.get(temp));
                this.servletContextAttributeListener.updateAttribute(servletContextAttributeEvent);
                this.servletContextAttributes.put(key, value);
                return;
            }
        }
        servletContextAttributeEvent = new ServletContextAttributeEvent(this, key, value);
        this.servletContextAttributeListener.addAttribute(servletContextAttributeEvent);
        this.servletContextAttributes.put(key, value);
    }

    public void setServletContextAttributeListener(ServletContextAttributeListener servletContextAttributeListener) {
        this.servletContextAttributeListener = servletContextAttributeListener;
    }

    public ServletContextEvent getServletContextEvent() {
        return servletContextEvent;
    }

    @Override
    public Object getAttribute(String key) {
        return this.servletContextAttributes.get(key);
    }

    @Override
    public void removeAttribute(String key) {
        ServletContextAttributeEvent servletContextAttributeEvent = new ServletContextAttributeEvent(this, key, this.servletContextAttributes.get(key));
        this.servletContextAttributeListener.removeAttribute(servletContextAttributeEvent);
        this.servletContextAttributes.remove(key);
    }
}
