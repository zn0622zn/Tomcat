package com.zn.core;

import com.zn.servlet.HttpServlet;
import com.zn.servlet.HttpServletRequest;
import com.zn.servlet.HttpServletResponse;
import com.zn.servlet.RequestDispatcher;
import com.zn.servlet.annotation.WebServlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@WebServlet("/ccccc")
public class Server extends HttpServlet {
    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = new User(1,"vvv");
        req.setAttribute("a",user);
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/test");
        requestDispatcher.forward(req, resp);
    }
}
