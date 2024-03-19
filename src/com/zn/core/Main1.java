package com.zn.core;

import com.zn.servlet.HttpServlet;
import com.zn.servlet.HttpServletRequest;
import com.zn.servlet.HttpServletResponse;
import com.zn.servlet.annotation.WebServlet;
import com.zn.tomcat.impl.TomcatHttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

@WebServlet("/login")
public class Main1 extends HttpServlet {
    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println(req.getHeader("content-length"));
    }
}
