package com.clevoice.asr;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSON;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClient;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.model.LfasrType;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@WebServlet(name = "MainServlet", urlPatterns = { "hello" }, loadOnStartup = 1)
public class MainServlet extends HttpServlet {
  static final long serialVersionUID = 0;
  private LfasrClient client;
  private File file = new File("./test.wav");
  private LfasrType type = LfasrType.LFASR_TELEPHONY_RECORDED_AUDIO;
  private HashMap<String, String> params = new HashMap<>();

  public MainServlet() {
    System.out.println(
        "iflytek config: " + System.getProperty("iflytek_app_id") + "/" + System.getProperty("iflytek_secret_key"));
    System.out.println("file: " + file.getAbsolutePath());
    // 初始化LFASR实例 
    try {
      this.client = LfasrClientImp.initLfasrClient(System.getProperty("iflytek_app_id"),
          System.getProperty("iflytek_secret_key"));
    } catch (LfasrException e) {
      Message initMsg = JSON.parseObject(e.getMessage(), Message.class);
      System.err.println("ecode=" + initMsg.getErr_no());
      System.err.println("failed=" + initMsg.getFailed());
    }

    // 用户自定义参数，可传递suid，has_participle（是否分词），max_alternatives（多候选词），not_wait（是否同步等待）
    // suid为用户自定义标识字符串，
    // has_participle为true或false字符串，
    // max_alternatives 为1-10整数，
    // not_wait为true或false字符串。
    params.put("suid", Long.toString(System.currentTimeMillis()));
    params.put("has_participle", "true");
    params.put("max_alternatives", "3");
    params.put("no_wait", "false");
    System.out.println("params: " + params.toString());
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String result = "";
    try {
      @SuppressWarnings("unchecked")
      HashMap<String, String> newParams = (HashMap<String, String>) params.clone();
      String suid = request.getParameter("suid");
      if (suid != null)
        newParams.put("suid", suid);
      String has_participle = request.getParameter("has_participle");
      if (has_participle != null)
        newParams.put("has_participle", has_participle);
      String max_alternatives = request.getParameter("max_alternatives");
      if (max_alternatives != null)
        newParams.put("max_alternatives", max_alternatives);
      String no_wait = request.getParameter("no_wait");
      if (no_wait != null)
        newParams.put("no_wait", no_wait);

      // 上传音频文件
      Message uploadMsg = this.client.lfasrUpload(file.getAbsolutePath(), type, newParams);

      // 判断返回值
      int ok = uploadMsg.getOk();
      if (ok == 0) {
        // 创建任务成功
        String task_id = uploadMsg.getData();
        System.out.println("upload: " + uploadMsg.toString());
        result += uploadMsg.getData();
        // 获取任务结果  
        try {
          Message resultMsg = this.client.lfasrGetResult(task_id);
          System.out.println("result: " + resultMsg.toString());

          while (true) {
            if (resultMsg.getOk() != 0 && resultMsg.getErr_no() == 26605) {
              try {
                Thread.sleep(10000);
                resultMsg = this.client.lfasrGetResult(task_id);
                System.out.println("result: " + resultMsg.toString());
              } catch (InterruptedException e) {
                e.printStackTrace();
              }              
            } else {
              break;
            }
          }

          if (resultMsg.getOk() == 0) {
            // 打印转写结果
            result += resultMsg.getData();
          } else {
            // 转写失败，根据失败信息进行处理  
            System.err.println("ecode=" + resultMsg.getErr_no());
            System.err.println("failed=" + resultMsg.getFailed());
          }
        } catch (LfasrException e) {
          // 获取结果异常处理，解析异常描述信息  
          Message resultMsg = JSON.parseObject(e.getMessage(), Message.class);
          System.out.println("ecode=" + resultMsg.getErr_no());
          System.out.println("failed=" + resultMsg.getFailed());
        }
      } else {
        // 创建任务失败-服务端异常
        System.out.println("ecode=" + uploadMsg.getErr_no());
        System.out.println("failed=" + uploadMsg.getFailed());
      }
    } catch (LfasrException e) {
      // 上传异常，解析异常描述信息
      Message uploadMsg = JSON.parseObject(e.getMessage(), Message.class);
      System.out.println("ecode=" + uploadMsg.getErr_no());
      System.out.println("failed=" + uploadMsg.getFailed());
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