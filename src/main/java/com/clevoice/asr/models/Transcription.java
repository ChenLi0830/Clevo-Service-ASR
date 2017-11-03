package com.clevoice.asr.models;

import graphql.annotations.GraphQLName;
import graphql.annotations.GraphQLField;

@GraphQLName("Transcription")
public class Transcription {

  @GraphQLField
  private String id;
  @GraphQLField
  private String file;
  @GraphQLField
  private String status;
  @GraphQLField
  private String result;

  public Transcription() {
  }

  public String getFile() {
    return file;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setFile(String file) {
    this.file = file;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public String toString() {
    return "id:[" + id + "] file:[" + file + "] status:[" + status + "] result:[" + result +"]";
  }
}