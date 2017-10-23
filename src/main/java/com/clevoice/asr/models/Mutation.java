package com.clevoice.asr.models;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;
import javax.validation.constraints.NotNull;

@GraphQLName("mutation")
public class Mutation {

  @GraphQLField
  public static ASR createASR(final DataFetchingEnvironment env, @NotNull @GraphQLName("file") final String file)
      throws ASRException {
    return ASRProvider.getInstance().create(file);
  }

  @GraphQLField
  public static ASR deleteASR(final DataFetchingEnvironment env, @NotNull @GraphQLName("id") final String id)
      throws ASRException {
    return ASRProvider.getInstance().delete(id);
  }
}