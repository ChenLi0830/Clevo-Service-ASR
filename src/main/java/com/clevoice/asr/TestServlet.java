package com.clevoice.asr;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// import com.clevoice.asr.models.ASR;
// import com.clevoice.asr.models.ASRException;
// import com.clevoice.asr.models.ASRProvider;
import java.io.IOException;
import java.util.Properties;

@WebServlet(urlPatterns = { "/test" })
public class TestServlet extends HttpServlet {
  static final long serialVersionUID = 0;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String result = "Test: " + System.currentTimeMillis();
    result += "\nIFLYTEK_HAS_PARTICIPLE: " + System.getenv("IFLYTEK_HAS_PARTICIPLE");
    result += "\nIFLYTEK_MAX_ALTERNATIVES: " + System.getenv("IFLYTEK_MAX_ALTERNATIVES");
    result += "\nIFLYTEK_NO_WAIT: " + System.getenv("IFLYTEK_NO_WAIT");
    Properties props = new Properties();
    props.load(this.getClass().getResourceAsStream("/config.properties"));
    result += "\nconfig.properties: " + props;

    response.getWriter().print(result);
  }

  // protected void doPost(HttpServletRequest request, HttpServletResponse response)
  //     throws ServletException, IOException {
  //     String name = request.getParameter("name");
  //     if (name == null) name = "World";
  //     request.setAttribute("user", name);
  //     request.getRequestDispatcher("response.jsp").forward(request, response); 
  // }
}