package com.bronya;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class NonBlockedTest {
  static {
    ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
  }

  public static void main(String[] args) {

    var waitGroup /* golang */ = new CountDownLatch(2);

    var nonBlockedServer =
        new Thread(
            () -> {
              ByteBuffer buf = ByteBuffer.allocate(16);
              try (ServerSocketChannel listener = ServerSocketChannel.open()) {
                listener.bind(new InetSocketAddress("0.0.0.0", 3261));
                // 非阻塞 listener
                listener.configureBlocking(false);
                var sockets = new ArrayList<SocketChannel>();
                while (true) {
                  Thread.sleep(1000);
                  System.out.println("Non-blocked, listening for new connection...");
                  // 没有新的连接时，accept 方法返回 null
                  SocketChannel socket = listener.accept();
                  if (socket != null) {
                    // 非阻塞 socket
                    socket.configureBlocking(false);
                    sockets.add(socket);
                  }
                  for (var socket_ : sockets) {
                    // 写 buf 前调用 clear 方法：清空脏数据
                    buf.clear();
                    System.out.println("Non-blocked, reading for new packet...");
                    // 没有新的数据时，read 方法返回 0
                    int nBytes = socket_.read(buf);
                    if (nBytes > 0) {
                      // 读 buf 前调用 flip 方法
                      buf.flip();
                      System.out.println(StandardCharsets.UTF_8.decode(buf).toString());
                    }
                  }
                }
              } catch (IOException | InterruptedException e) {
                System.err.println("[Server] Error: " + e.getMessage());
                Thread.currentThread().interrupt();
              } finally {
                waitGroup.countDown();
                System.out.println("[Server] Waiting for: " + waitGroup.getCount() + " threads");
              }
            });

    var client =
        new Thread(
            () -> {
              try (SocketChannel socket = SocketChannel.open()) {
                socket.connect(new InetSocketAddress("localhost", 3261));
                // 调用 park 方法，主动等待
                // LockSupport.park(); // fixme
              } catch (IOException e) {
                System.out.println("[Client] Error: " + e.getMessage());
                Thread.currentThread().interrupt();
              } finally {
                waitGroup.countDown();
                System.out.println("[Client] Waiting for: " + waitGroup.getCount() + " threads");
              }
            });

    nonBlockedServer.start();
    client.start();

    try {
      boolean countIs0 = waitGroup.await(3, TimeUnit.SECONDS); // Object.wait();
      System.out.println("countIs0: " + countIs0);
    } catch (InterruptedException ignored) {
    } finally {
      nonBlockedServer.interrupt();
      client.interrupt();
    }
  }
}
