package com.bronya;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class BlockedTest {
  public static void main(String[] args) {
    Thread blockedServer =
        new Thread(
            () -> {
              ByteBuffer buf = ByteBuffer.allocate(16);
              try (ServerSocketChannel listener = ServerSocketChannel.open()) {
                listener.bind(new InetSocketAddress("0.0.0.0", 3261));
                var sockets = new ArrayList<SocketChannel>();
                while (true) {
                  System.out.println("Blocked for new connection...");
                  // 没有新的连接时，线程阻塞，放弃 cpu
                  SocketChannel socket = listener.accept();
                  sockets.add(socket);
                  for (var socket_ : sockets) {
                    // 写 buf 前调用 clear 方法：清空脏数据
                    buf.clear();
                    System.out.println("Blocked for new packet...");
                    // 没有新的数据时，线程阻塞，放弃 cpu
                    socket_.read(buf); // 从 socketChan 中读，向 buf 中写
                    // 读 buf 前调用 flip 方法
                    buf.flip();
                    System.out.println(StandardCharsets.UTF_8.decode(buf).toString());
                  }
                }
              } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
              }
            });

    Thread client =
        new Thread(
            () -> {
              try (SocketChannel socket = SocketChannel.open()) {
                Thread.sleep(3000);
                socket.connect(new InetSocketAddress("localhost", 3261));
              } catch (IOException | InterruptedException e) {
                System.out.println("Error: " + e.getMessage());
              }
            });

    // 更优雅的实现：CountDownLatch（类似 golang 的 sync.WaitGroup）
    blockedServer.setDaemon(true);
    client.setDaemon(true);
    blockedServer.start();
    client.start();

    try {
      Thread.sleep(10_000);
    } catch (InterruptedException ignore) {
    }
  }
}
