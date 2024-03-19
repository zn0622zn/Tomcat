package com.zn.servlet.listener;

/**
 * @author 张男
 * @date: 2024/3/12---17:26
 */
public interface HttpSessionListener extends Listener{
    default void initSession(HttpSessionEvent httpSessionEvent) {

    }

    default void destroyed(HttpSessionEvent httpSessionEvent) {

    }
}
