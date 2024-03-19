package com.zn.servlet;

import java.io.BufferedReader;
import java.io.InputStream;


public interface HttpServletRequest {

    /**
     * 返回所有的cookie
     *
     * @return
     */
    Cookie[] getCookies();

    /**
     * 向域对象中传入kv
     *
     * @param key
     * @param value
     */
    void setAttribute(String key, Object value);

    /**
     * 根据key从域对象中拿值
     *
     * @param key
     * @return
     */
    Object getAttribute(String key);

    void removeAttribute(String key);

    /**
     * 根据请求头获取对应的value
     *
     * @param key 请求头的key
     * @return 请求头key对应的value
     */
    String getHeader(String key);

    /**
     * 获取请求头中所有的key
     *
     * @return 请求头key的数组
     */
    String[] getHeaderNames();

    /**
     * 获取本次请求的请求方法
     *
     * @return
     */
    String getMethod();

    /**
     * 获取本次请求的URL
     *
     * @return
     */
    String getRemoteURL();

    /**
     * 获取本次请求的URI
     *
     * @return
     */
    String getRemoteURI();

//    /**
//     * 获取客户端IP地址
//     *
//     * @return 本次请求的客户端IP地址
//     */
//    InetAddress getRemoteAddr();

    /**
     * 获取查询字符串参数
     *
     * @param key 字符串参数的key
     * @return 字符串参数对应key的value
     */
    String getParameter(String key);

//    /**
//     * 获取请求体数据
//     * @return 字符串形式的请求体
//     */
//    String getRequestBody();

    /**
     * 获取本次请求的字符输入流
     * 用于用户解析请求体
     */
    BufferedReader getReader();

    InputStream getInputStream();

    RequestDispatcher getRequestDispatcher(String uri);

    HttpSession getSession();

    ServletContext getServletContext();
}
