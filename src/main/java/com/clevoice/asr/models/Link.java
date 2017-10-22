package com.clevoice.asr.models;

import graphql.annotations.GraphQLName;
import graphql.annotations.GraphQLField;

@GraphQLName("Link")
public class Link {

  @GraphQLField
  private final String url;
  @GraphQLField
  private final String description;

  public Link(String url, String description) {
      this.url = url;
      this.description = description;
  }

  public String getUrl() {
      return url;
  }

  public String getDescription() {
      return description;
  }
}