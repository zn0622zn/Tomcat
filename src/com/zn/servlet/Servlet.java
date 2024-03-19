package com.zn.servlet;

import java.io.IOException;

public interface Servlet {
    /**
     * service方法执行前执行的逻辑
     */
    void init();

    /**
     * service方法
     */
    void service(HttpServletRequest request,HttpServletResponse response) throws IOException;

    /**
     * 服务器停止且卸装Servlet时执行该方法
     */
    void destroy();
}
