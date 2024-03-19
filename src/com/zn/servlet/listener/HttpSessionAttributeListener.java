package com.zn.servlet.listener;

/**
 * @author 张男
 * @date: 2024/3/12---17:29
 */
public interface HttpSessionAttributeListener extends Listener{
    default void addAttribute(HttpSessionAttributeEvent httpSessionAttributeEvent) {

    }

    default void updateAttribute(HttpSessionAttributeEvent httpSessionAttributeEvent) {

    }


    default void removeAttribute(HttpSessionAttributeEvent httpSessionAttributeEvent) {

    }
}
