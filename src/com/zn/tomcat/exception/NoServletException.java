package com.zn.tomcat.exception;


public class NoServletException extends Exception{
    private static final long serialVersionUID = -5630553821469741945L;

    public NoServletException(String message) {
        super(message);
    }
}
