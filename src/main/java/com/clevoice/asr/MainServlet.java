package com.clevoice.asr;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "MainServlet", urlPatterns = { "hello" }, loadOnStartup = 1)
public class MainServlet extends HttpServlet {
  static final long serialVersionUID = 0;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String name = request.getParameter("name");
    response.getWriter().print("Hello, World! " + name);
  }

  // protected void doPost(HttpServletRequest request, HttpServletResponse response)
  //     throws ServletException, IOException {
  //     String name = request.getParameter("name");
  //     if (name == null) name = "World";
  //     request.setAttribute("user", name);
  //     request.getRequestDispatcher("response.jsp").forward(request, response); 
  // }
}