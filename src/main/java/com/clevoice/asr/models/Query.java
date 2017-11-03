package com.clevoice.asr.models;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import javax.validation.constraints.NotNull;

@GraphQLName("query")
public class Query {

  @GraphQLField
  public static List<Transcription> transcriptions(final DataFetchingEnvironment env) throws TranscriptionException {
    return TranscriptionProvider.getInstance().all();
  }

  @GraphQLField
  public static Transcription transcriptionById(final DataFetchingEnvironment env, @NotNull @GraphQLName("id") final String id)
      throws TranscriptionException {
    return TranscriptionProvider.getInstance().get(id);
  }
}