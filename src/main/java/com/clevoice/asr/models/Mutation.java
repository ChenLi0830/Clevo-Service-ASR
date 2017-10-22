package com.clevoice.asr.models;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

@GraphQLName("mutation")
public class Mutation {

  @GraphQLField
  public static Link createLink(final DataFetchingEnvironment env, @GraphQLName("url") String url, @GraphQLName("description") String description) {
      Link newLink = new Link(url, description);
      LinkProvider.getInstance().saveLink(newLink);
      return newLink;
  }
}