package com.clevoice.asr.models;

import graphql.annotations.GraphQLName;
import graphql.annotations.GraphQLField;

@GraphQLName("Transcription")
public class Transcription {

  @GraphQLField
  private String id;
  @GraphQLField
  private TranscriptionStatus status;
  @GraphQLField
  private String result;

  public Transcription() {
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public TranscriptionStatus getStatus() {
    return status;
  }

  public void setStatus(TranscriptionStatus status) {
    this.status = status;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public String toString() {
    return "id:[" + id + "] status:[" + status + "] result:[" + result +"]";
  }
}