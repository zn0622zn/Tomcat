package com.zn.core;

import com.zn.servlet.*;
import com.zn.servlet.annotation.WebServlet;

import java.util.Arrays;

@WebServlet("/test")
public class Main2 extends HttpServlet{
    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) {
        ServletContext servletContext = req.getServletContext();
        servletContext.setAttribute("a", "b");
    }
}
