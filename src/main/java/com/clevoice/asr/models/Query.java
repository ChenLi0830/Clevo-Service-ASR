package com.clevoice.asr.models;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;

@GraphQLName("query")
public class Query {

  @GraphQLField
  public static List<Link> allLinks(final DataFetchingEnvironment env) {
    System.out.println(env.getFields());
    return LinkProvider.getInstance().getAllLinks();
  }
}