package com.ukonnra.wonderland.whiterabbit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.hibernate.MainVerticle;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle( new MainVerticle() )
      .onSuccess( event -> testContext.completeNow() )
      .onFailure( testContext::failNow );
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) {
    testContext.completeNow();
  }
}
