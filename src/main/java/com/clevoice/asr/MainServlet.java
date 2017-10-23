package com.clevoice.asr;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.clevoice.asr.models.ASR;
import com.clevoice.asr.models.ASRException;
import com.clevoice.asr.models.ASRProvider;
import java.io.File;
import java.io.IOException;

@WebServlet(name = "MainServlet", urlPatterns = { "hello" })
public class MainServlet extends HttpServlet {
  static final long serialVersionUID = 0;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String file = request.getParameter("file");
    if (file == null) {
      file = "test.wav";
    }
    String result = "";

    try {
      ASR record = ASRProvider.getInstance().create((new File(file)).getAbsolutePath());
      result += record.getId();
      result += record.getFile();
      result += record.getStatus();
      result += record.getResult().toString();
    } catch (ASRException e) {
      e.printStackTrace();
      result += e.getMessage();
    }
    response.getWriter().print("Hello, World! " + result);
  }

  // protected void doPost(HttpServletRequest request, HttpServletResponse response)
  //     throws ServletException, IOException {
  //     String name = request.getParameter("name");
  //     if (name == null) name = "World";
  //     request.setAttribute("user", name);
  //     request.getRequestDispatcher("response.jsp").forward(request, response); 
  // }
}