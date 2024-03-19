package com.zn.core;

import com.zn.servlet.HttpServlet;
import com.zn.servlet.HttpServletRequest;
import com.zn.servlet.HttpServletResponse;
import com.zn.servlet.annotation.WebServlet;

import java.io.IOException;

@WebServlet("/aaa")
public class Main3 extends HttpServlet {
    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.err.println(req.getServletContext().getAttribute("a"));
    }
}
