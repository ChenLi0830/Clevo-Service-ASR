package com.clevoice.asr.models;

import graphql.annotations.GraphQLName;

@GraphQLName("TranscriptionStatus")
public enum TranscriptionStatus {
  started, processing, completed, failed
}