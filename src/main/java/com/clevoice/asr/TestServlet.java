package com.clevoice.asr;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.clevoice.asr.models.ASR;
import com.clevoice.asr.models.ASRException;
import com.clevoice.asr.models.ASRProvider;
import java.io.IOException;

@WebServlet(urlPatterns = { "test" })
public class TestServlet extends HttpServlet {
  static final long serialVersionUID = 0;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String result = "";

    try {
      ASR record = ASRProvider.getInstance().create(request.getParameter("file"));
      result += record.getId() + "\n";
      result += record.getFile() + "\n";;
      result += record.getStatus() + "\n";;
      result += record.getResult().toString() + "\n";;
    } catch (ASRException e) {
      e.printStackTrace();
      result += e.getMessage();
    }

    response.getWriter().print("Test\n" + result);
  }

  // protected void doPost(HttpServletRequest request, HttpServletResponse response)
  //     throws ServletException, IOException {
  //     String name = request.getParameter("name");
  //     if (name == null) name = "World";
  //     request.setAttribute("user", name);
  //     request.getRequestDispatcher("response.jsp").forward(request, response); 
  // }
}