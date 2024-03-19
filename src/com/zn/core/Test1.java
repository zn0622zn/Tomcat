package com.zn.core;

import com.zn.servlet.*;
import com.zn.servlet.annotation.WebServlet;

import java.io.IOException;

@WebServlet("/good")
public class Test1 extends HttpServlet {
    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        System.out.println(session.getId());
    }
}
