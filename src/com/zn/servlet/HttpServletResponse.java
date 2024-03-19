package com.zn.servlet;

import java.io.PrintWriter;

public interface HttpServletResponse {

    /**
     * 向请求体中写内容
     * @return
     */
    PrintWriter getWriter();

    /**
     * 设置请求头
     * @param key 请求头的key
     * @param value 请求头的value
     */
    void setHeader(String key, String value);

    /**
     * 设置响应状态码
     * @param status 响应状态码
     */
    void setStatus(int status);

    /**
     * 重定向到指定uri
     * @param uri 重定向的uri
     */
    void sendRedirect(String uri);

    /**
     * 用于清空 response中的byteArrayOutputStream,用于实现forward转发
     */
    void reset();

    /**
     * 添加cookie
     * @param cookie
     */
    void addCookie(Cookie cookie);
}
