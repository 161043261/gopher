package com.bronya;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.net.NetServerOptions;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var options = new NetServerOptions().setPort(3261).setLogActivity(true);
    var server = vertx.createNetServer(options);

    server.connectHandler(
        socket -> {
          socket.handler(
              buffer -> {
                System.out.println("Server receives: " + buffer.length());
              });

          socket.closeHandler(
              v -> {
                System.out.println("Socket has been closed");
              });
        });

    server.exceptionHandler(
        e -> {
          System.out.println(e.getMessage());
        });

    server
        .listen(3261, "0.0.0.0")
        .onComplete(
            res -> {
              if (res.succeeded()) {
                System.out.println("Server is listening on port: " + server.actualPort());
              } else {
                System.out.println("Failed to listen: " + res.cause().getMessage());
              }
            });

    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  System.out.println("Gracefully shutting down...");
                  server
                      .close()
                      .onComplete(
                          res -> {
                            if (res.succeeded()) {
                              System.out.println("Server has been closed");
                            } else {
                              System.out.println("Failed to shutdown: " + res.cause().getMessage());
                            }
                          });
                }));
  }
}
