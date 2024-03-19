package com.zn.servlet;

import java.util.Date;

/**
 * Cookie类
 */
public class Cookie {
    private String key;
    private String value;
    private int maxAge;
    private String path;
    private String expires;
    private boolean httpOnly;

    private void setExpires() {
        long time = System.currentTimeMillis();
        long realTime = time + this.maxAge* 1000L;
        Date date = new Date(realTime);
        String timeString = date.toString();
        String[] s = timeString.split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(s[0])
                .append(", ")
                .append(s[2])
                .append("-")
                .append(s[1])
                .append("-")
                .append(s[s.length - 1])
                .append(" ")
                .append(s[3])
                .append(" ")
                .append("GMT");
        this.expires = stringBuilder.toString();
    }

    public Cookie(String key, String value) {
        this.key = key;
        this.value = value;
        this.maxAge = -1;
        this.path = "/";
    }

    public boolean getHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public String getExpires() {
        return expires;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.setExpires();
        this.maxAge = maxAge;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
