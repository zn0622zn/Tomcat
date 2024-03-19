package com.zn.servlet.listener;

import com.zn.servlet.HttpSession;

/**
 * @author 张男
 * @date: 2024/3/12---17:28
 */
public class HttpSessionEvent {
    private HttpSession httpSession;

    public HttpSessionEvent(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public HttpSession getHttpSession() {
        return httpSession;
    }
}
