package com.zn.core;

import com.zn.servlet.HttpServlet;
import com.zn.servlet.HttpServletRequest;
import com.zn.servlet.HttpServletResponse;
import com.zn.servlet.annotation.WebServlet;
import com.zn.tomcat.prepare.PrepareHandler;

import java.io.File;
import java.util.List;

@WebServlet("/myTest")
public class Client extends HttpServlet {
    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) {
        String header = req.getHeader("aaa");
        System.err.println(header);
        System.out.println(req.getParameter("username"));
        System.out.println(req.getParameter("password"));
        System.out.println(req.getParameter("passwordsss"));
    }
}
