package com.clevoice.asr.models;

import java.util.ArrayList;
import java.util.List;

public class LinkProvider {
  
  private final List<Link> links;
  private final static LinkProvider instance = new LinkProvider();

  private LinkProvider() {
      links = new ArrayList<>();
      //add some links to start off with
      links.add(new Link("http://howtographql.com", "Your favorite GraphQL page"));
      links.add(new Link("http://graphql.org/learn/", "The official docks"));
  }

  public static LinkProvider getInstance() {
    return instance;
  }

  public List<Link> getAllLinks() {
      return links;
  }
  
  public void saveLink(Link link) {
      links.add(link);
  }
}