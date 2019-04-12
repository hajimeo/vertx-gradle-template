package com.example.vertxhivejdbc;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.apex.Router;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.handler.BodyHandler;
import io.vertx.ext.jdbc.JdbcService;
import io.vertx.ext.sql.SqlConnection;

import java.util.List;

public class ServerVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    JsonObject config = new JsonObject().put("url", "jdbc:hive2://atscale731:11111/");
    config.put("user", "admin");
    config.put("password", "admin");
    DeploymentOptions options = new DeploymentOptions().setConfig(config);

    vertx.deployVerticle("service:io.vertx.jdbc-service", options, res -> {
      if (res.succeeded()) {
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.route().handler(routingContext -> {
          routingContext.response().putHeader("Content-Type", "application/json");
          routingContext.next();
        });

        router.get("/atscale").handler(this::handleListResult);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
      } else {
        throw new RuntimeException(res.cause());
      }
    });
  }

  private void handleListResult(RoutingContext routingContext) {
    JdbcService proxy = JdbcService.createEventBusProxy(vertx, "vertx.jdbc");

    proxy.getConnection(res -> {
      if (res.succeeded()) {
        SqlConnection connection = res.result();
        connection.query("SELECT Gender, SUM(orderquantity1) AS q FROM `Sales Insights`.`Internet Sales Cube` GROUP BY Gender", res2 -> {
          if (res2.succeeded()) {
            JsonArray arr = new JsonArray();
            List<JsonObject> result = res2.result().getRows();
            result.forEach(arr::add);
            connection.close(res3 -> {
              if (res3.succeeded()) {
                routingContext.response().end(arr.encode());
              } else {
                routingContext.fail(res3.cause());
              }
            });
          } else {
            connection.close(res3 -> {
              if (res3.succeeded()) {
                routingContext.fail(res2.cause());
              } else {
                routingContext.fail(res2.cause());
              }
            });
          }
        });
      } else {
        routingContext.fail(res.cause());
      }
    });
  }
}