package com.clevoice.asr.models;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;
import javax.validation.constraints.NotNull;

@GraphQLName("mutation")
public class Mutation {

  @GraphQLField
  public static Transcription transcriptionCreate(final DataFetchingEnvironment env, @NotNull @GraphQLName("file") final String file)
      throws TranscriptionException {
    return TranscriptionProvider.getInstance().create(file);
  }
}