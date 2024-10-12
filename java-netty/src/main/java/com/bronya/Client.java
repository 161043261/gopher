package com.bronya;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClientOptions;

public class Client extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var options =
        new NetClientOptions()
            .setConnectTimeout(10000)
            .setReconnectAttempts(10)
            .setReconnectInterval(500);

    var client = vertx.createNetClient(options);

    client
        .connect(3261, "172.29.45.243")
        .onComplete(
            res -> {
              if (res.succeeded()) {
                System.out.println("Client connect succeeded!");
              } else {
                System.out.println("Failed to connect: " + res.cause().getMessage());
              }
            });
  }

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(Client.class.getName());
  }
}
