package com.zn.servlet.listener;

import com.zn.servlet.HttpSession;

/**
 * @author 张男
 * @date: 2024/3/12---17:29
 */
public class HttpSessionAttributeEvent extends HttpSessionEvent{
    private String key;
    private Object value;

    public HttpSessionAttributeEvent(HttpSession httpSession, String key, Object value) {
        super(httpSession);
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
