package com.zn.servlet.listener;


import com.zn.servlet.HttpServletRequest;

public class ServletRequestAttributeEvent extends ServletRequestEvent{

    private String key;
    private Object value;

    public ServletRequestAttributeEvent(HttpServletRequest httpServletRequest, String key, Object value) {
        super(httpServletRequest);
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
