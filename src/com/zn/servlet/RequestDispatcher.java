package com.zn.servlet;

/**
 * 请求控制器,可以获取并用来转发
 */
public interface RequestDispatcher {
    /**
     * 转发，响应体的流，转发前和转发后是同一个
     */
    void include(HttpServletRequest request, HttpServletResponse response);

    /**
     * 转发，响应体的流，在转发下一个接口的过程中被清空，响应体内容只是转发后的结果
     * @param request
     * @param response
     */
    void forward(HttpServletRequest request, HttpServletResponse response);
}
