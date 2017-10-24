package com.clevoice.asr;

import com.clevoice.asr.models.*;
import javax.servlet.annotation.WebServlet;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.annotations.GraphQLAnnotations;
import graphql.servlet.SimpleGraphQLServlet;

@WebServlet(urlPatterns = "/graphql", loadOnStartup = 1)
public class GraphQLServlet extends SimpleGraphQLServlet {

  static final long serialVersionUID = 1;

  public GraphQLServlet() {
      super(buildSchema());
  }

  private static GraphQLSchema buildSchema() {
    GraphQLObjectType query = GraphQLAnnotations.object(Query.class);
    GraphQLObjectType mutation = GraphQLAnnotations.object(Mutation.class);
    return GraphQLSchema.newSchema().query(query).mutation(mutation).build();
  }
}