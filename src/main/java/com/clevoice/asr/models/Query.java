package com.clevoice.asr.models;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import javax.validation.constraints.NotNull;

@GraphQLName("query")
public class Query {

  @GraphQLField
  public static List<ASR> asrs(final DataFetchingEnvironment env) throws ASRException {
    return ASRProvider.getInstance().all();
  }

  @GraphQLField
  public static ASR asr(final DataFetchingEnvironment env, @NotNull @GraphQLName("id") final String id)
      throws ASRException {
    return ASRProvider.getInstance().get(id);
  }
}