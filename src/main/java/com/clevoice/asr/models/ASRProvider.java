package com.clevoice.asr.models;

import com.alibaba.fastjson.JSON;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClient;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.model.LfasrType;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import com.iflytek.msp.cpdb.lfasr.model.ProgressStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ASRProvider {

  private final static ASRProvider instance = new ASRProvider();

  private Map<String, ASR> records;
  private LfasrClient client;
  private LfasrType type = LfasrType.LFASR_TELEPHONY_RECORDED_AUDIO;
  private HashMap<String, String> params = new HashMap<>();

  private ASRProvider() {
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

    this.records = new HashMap<>();
  }

  public static ASRProvider getInstance() {
    return instance;
  }

  public ASR create(String file) throws ASRException {
    ASR record = new ASR();
    try {
      URL url = new URL(file);
      String[] parts = url.getFile().split("\\.");
      File temp = new File(UUID.randomUUID().toString() + "." + parts[parts.length-1]);
      FileUtils.copyURLToFile(url, temp);
      System.out.println("download succeeded: " + temp.getAbsolutePath());
      
      // 上传音频文件
      Message uploadMsg = this.client.lfasrUpload(temp.getAbsolutePath(), type, params);
      temp.delete();
      record.setFile(file);

      // 判断返回值
      int ok = uploadMsg.getOk();
      if (ok == 0) {
        // 创建任务成功
        String task_id = uploadMsg.getData();
        System.out.println("upload succeeded: " + uploadMsg.toString());
        record.setId(task_id);
        this.records.put(task_id, record);

        while (true) {
          ASR one = this.get(task_id);
          if (one.getStatus() != null && one.getStatus() != "9" && one.getResult() == null) {
            try {
              Thread.sleep(10000);
            } catch (InterruptedException e) {
              e.printStackTrace();
              throw new ASRException(e.getMessage());
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
      }
    } catch (LfasrException e) {
      // 上传异常，解析异常描述信息
      Message uploadMsg = JSON.parseObject(e.getMessage(), Message.class);
      System.err.println("upload failed: " + file);
      System.out.println("ecode=" + uploadMsg.getErr_no());
      System.out.println("failed=" + uploadMsg.getFailed());
    } catch (IOException e) {
      System.err.println("download failed: " + file);
      e.printStackTrace();
    }
    return record;
  }

  public ASR get(String id) throws ASRException {
    ASR record = this.records.get(id);
    if (record == null || record.getResult() != null ) {
      System.out.println("memory record: " + (record != null ? record.toString() : "null"));
    } else {
      try {
        Message progressMsg = this.client.lfasrGetProgress(id);

        if (progressMsg.getOk() == 0) {
          System.out.println(id + " progress: " + progressMsg.toString());
          ProgressStatus progressStatus = JSON.parseObject(progressMsg.getData(), ProgressStatus.class);
          record.setStatus(Integer.toString(progressStatus.getStatus()));
          if (progressStatus.getStatus() == 9) {
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
              }
            } catch (LfasrException e) {
              // 获取结果异常处理，解析异常描述信息  
              Message resultMsg = JSON.parseObject(e.getMessage(), Message.class);
              System.err.println("result failed: " + id);
              System.out.println("ecode=" + resultMsg.getErr_no());
              System.out.println("failed=" + resultMsg.getFailed());
            }
          }
        } else {
          System.err.println("progress failed: " + id);
          System.err.println("ecode=" + progressMsg.getErr_no());
          System.err.println("failed=" + progressMsg.getFailed());
        }
      } catch (LfasrException e) {
        // 获取进度异常处理，根据返回信息排查问题后，再次进行获取
        Message progressMsg = JSON.parseObject(e.getMessage(), Message.class);
        System.err.println("progress failed: " + id);
        System.err.println("ecode=" + progressMsg.getErr_no());
        System.err.println("failed=" + progressMsg.getFailed());
      }
    }
    return record;
  }

  public ASR save(ASR record) throws ASRException {
    this.records.put(record.getId(), record);
    return record;
  }

  public ASR delete(String id) throws ASRException {
    return this.records.remove(id);
  }

  public List<ASR> all() throws ASRException {
    Set<String> keys = this.records.keySet();
    ArrayList<ASR> result = new ArrayList<>();
    keys.forEach(key -> {
      try {
        ASR record = this.get(key);
        result.add(record);
      } catch (ASRException e) {
        e.printStackTrace();
      }
    });
    return result;
  }
}