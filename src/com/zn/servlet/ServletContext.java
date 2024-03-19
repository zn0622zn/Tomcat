package com.zn.servlet;

/**
 * @author 张男
 * @date: 2024/3/12---13:51
 */
public interface ServletContext {
    void setAttribute(String key, Object value);

    Object getAttribute(String key);

    void removeAttribute(String key);
}
