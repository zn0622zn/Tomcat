package com.zn.tomcat.exception;

/**
 * @author 张男
 * @date: 2024/3/12---17:37
 */
public class NoListenerException extends Exception {
    private static final long serialVersionUID = 6333216729171347282L;

    public NoListenerException(String message) {
        super(message);
    }
}
