package com.clevoice.asr.models;

import com.alibaba.fastjson.JSON;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClient;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.model.LfasrType;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import com.iflytek.msp.cpdb.lfasr.model.ProgressStatus;
import java.util.HashMap;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class TranscriptionProvider {

  private final static TranscriptionProvider instance = new TranscriptionProvider();

  private LfasrClient client;
  private LfasrType type = LfasrType.LFASR_TELEPHONY_RECORDED_AUDIO;
  private HashMap<String, String> params = new HashMap<>();

  private TranscriptionProvider() {
    // 用户自定义参数，可传递suid，has_participle（是否分词），max_alternatives（多候选词），not_wait（是否同步等待）
    // suid为用户自定义标识字符串，
    // has_participle为true或false字符串，
    // max_alternatives 为1-10整数，
    // not_wait为true或false字符串。
    // params.put("suid", UUID.randomUUID().toString());
    // params.put("has_participle", "true");
    // params.put("max_alternatives", "3");
    // params.put("no_wait", "false");
    // System.out.println("params: " + params.toString());

    // 初始化LFASR实例 
    try {
      this.client = LfasrClientImp.initLfasrClient();
    } catch (LfasrException e) {
      e.printStackTrace();
      Message initMsg = JSON.parseObject(e.getMessage(), Message.class);
      System.err.println("init failed");
      System.err.println("ecode=" + initMsg.getErr_no());
      System.err.println("failed=" + initMsg.getFailed());
    }
  }

  public static TranscriptionProvider getInstance() {
    return instance;
  }

  public Transcription create(String file) throws TranscriptionException {
    Transcription record = new Transcription();
    try {
      URL url = new URL(file);
      String[] parts = url.getFile().split("\\.");
      File temp = new File(UUID.randomUUID().toString() + "." + parts[parts.length-1]);
      FileUtils.copyURLToFile(url, temp);
      System.out.println("download succeeded: " + temp.getAbsolutePath());
      
      // 上传音频文件
      Message uploadMsg = this.client.lfasrUpload(temp.getAbsolutePath(), type, params);
      temp.delete();

      // 判断返回值
      int ok = uploadMsg.getOk();
      if (ok == 0) {
        // 创建任务成功
        String task_id = uploadMsg.getData();
        System.out.println("upload succeeded: " + uploadMsg.toString());
        record.setId(task_id);
        record.setStatus(TranscriptionStatus.started);

        while (true) {
          record = this.get(task_id);
          if (record.getStatus() != null && record.getStatus() != TranscriptionStatus.completed && record.getResult() == null) {
            try {
              Thread.sleep(10000);
            } catch (InterruptedException e) {
              e.printStackTrace();
              throw new TranscriptionException(e.getMessage());
            }              
          } else {
            break;
          }
        }
      } else {
        // 创建任务失败-服务端异常
        System.err.println("upload failed: " + file);
        System.err.println("ecode=" + uploadMsg.getErr_no());
        System.err.println("failed=" + uploadMsg.getFailed());
        record.setStatus(TranscriptionStatus.failed);
      }
    } catch (LfasrException e) {
      // 上传异常，解析异常描述信息
      Message uploadMsg = JSON.parseObject(e.getMessage(), Message.class);
      System.err.println("upload failed: " + file);
      System.out.println("ecode=" + uploadMsg.getErr_no());
      System.out.println("failed=" + uploadMsg.getFailed());
      record.setStatus(TranscriptionStatus.failed);
    } catch (IOException e) {
      System.err.println("download failed: " + file);
      e.printStackTrace();
      record.setStatus(TranscriptionStatus.failed);
    }
    return record;
  }

  public Transcription get(String id) throws TranscriptionException {
    Transcription record = new Transcription();
    record.setId(id);
    try {
      Message progressMsg = this.client.lfasrGetProgress(id);

      if (progressMsg.getOk() == 0) {
        System.out.println(id + " progress: " + progressMsg.toString());
        ProgressStatus progressStatus = JSON.parseObject(progressMsg.getData(), ProgressStatus.class);
        if (progressStatus.getStatus() == 9) {
          record.setStatus(TranscriptionStatus.completed);
          try {
            Message resultMsg = this.client.lfasrGetResult(id);
            System.out.println(id + " result: " + resultMsg.toString());
            if (resultMsg.getOk() == 0) {
              // 打印转写结果
              record.setResult(resultMsg.getData());
            } else {
              // 转写失败，根据失败信息进行处理
              System.err.println("result failed: " + id);
              System.err.println("ecode=" + resultMsg.getErr_no());
              System.err.println("failed=" + resultMsg.getFailed());
              record.setStatus(TranscriptionStatus.failed);
            }
          } catch (LfasrException e) {
            // 获取结果异常处理，解析异常描述信息  
            Message resultMsg = JSON.parseObject(e.getMessage(), Message.class);
            System.err.println("result failed: " + id);
            System.out.println("ecode=" + resultMsg.getErr_no());
            System.out.println("failed=" + resultMsg.getFailed());
            record.setStatus(TranscriptionStatus.failed);
          }
        } else {
          record.setStatus(TranscriptionStatus.processing);
        }
      } else {
        System.err.println("progress failed: " + id);
        System.err.println("ecode=" + progressMsg.getErr_no());
        System.err.println("failed=" + progressMsg.getFailed());
        record.setStatus(TranscriptionStatus.failed);
      }
    } catch (LfasrException e) {
      // 获取进度异常处理，根据返回信息排查问题后，再次进行获取
      Message progressMsg = JSON.parseObject(e.getMessage(), Message.class);
      System.err.println("progress failed: " + id);
      System.err.println("ecode=" + progressMsg.getErr_no());
      System.err.println("failed=" + progressMsg.getFailed());
      record.setStatus(TranscriptionStatus.failed);
    }
    return record;
  }
}