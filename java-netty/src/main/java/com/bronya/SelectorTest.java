package com.bronya;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

// mvn dependency:build-classpath
public class SelectorTest {
  public static void main(String[] args) {
    var waitGroup = new CountDownLatch(2);

    var server =
        new Thread(
            () -> {
              try (ServerSocketChannel listener = ServerSocketChannel.open();
                  // 创建 Selector 多路复用器，调度多个 channel
                  Selector selector = Selector.open()) {
                listener.bind(new InetSocketAddress(/* "0.0.0.0" wildcard address */ 3261));
                listener.configureBlocking(false);

                // * SelectionKey.OP_ACCEPT  接收新连接时触发
                // * SelectionKey.OP_CONNECT 建立新连接时触发
                // * SelectionKey.OP_READ    有新的可读数据时触发
                // * SelectionKey.OP_WRITE   有新的可写数据时触发

                // 将 listener 注册到 selector，关注 listener 的 OP_ACCEPT 事件
                SelectionKey listenerKey = listener.register(selector, 0, null);
                listenerKey.interestOps(SelectionKey.OP_ACCEPT);
                // 等价于 listener.register(selector, SelectionKey.OP_ACCEPT);

                while (true) {
                  // selector.select();
                  // 没有事件触发：select 方法阻塞
                  // 有事件触发：选择触发的事件，返回被选择的事件的数量
                  int nEvents = selector.select();
                  System.out.println("Number of selected events: " + nEvents);

                  // selector.selectedKeys();
                  // 返回被选择的事件的键的集合
                  Set<SelectionKey> eventKeys = selector.selectedKeys();

                  Iterator<SelectionKey> iter = eventKeys.iterator();
                  while (iter.hasNext()) {
                    SelectionKey eventKey = iter.next();
                    if (eventKey != listenerKey) {
                      throw new RuntimeException("eventKey != listenerKey");
                    }

                    System.out.println("Selected event key: " + eventKey);
                    // 获取键对应的事件（通道）
                    var chan_ = (ServerSocketChannel) eventKey.channel();
                    if (chan_ != listener) {
                      throw new RuntimeException("chan_ != listener");
                    }
                    SocketChannel socket = listener.accept();
                    socket.configureBlocking(false);
                  }
                }
              } catch (IOException | RuntimeException e) {
                System.out.println(
                    "[Server-stage1] Is interrupted: " + Thread.currentThread().isInterrupted());
                Thread.currentThread().interrupt();
              } finally {
                waitGroup.countDown();
                System.out.println(
                    "[Server-stage2] Is interrupted: " + Thread.currentThread().isInterrupted());
                System.out.println("[Server] Waiting for: " + waitGroup.getCount() + " threads");
              }
            });

    var client =
        new Thread(
            () -> {
              try (SocketChannel socket = SocketChannel.open()) {
                socket.connect(new InetSocketAddress("localhost", 3261));
                // ! 必须检查中断状态 interrupted
                while (!Thread.currentThread().isInterrupted())
                  ;
              } catch (IOException ignored) {
              } finally {
                waitGroup.countDown();
                System.out.println(
                    "[Client] Is interrupted: " + Thread.currentThread().isInterrupted());
                System.out.println("[Client] Waiting for: " + waitGroup.getCount() + " threads");
              }
            });

    server.start();
    client.start();
    try {
      boolean zeroCnt = waitGroup.await(5, TimeUnit.SECONDS);
      assert !zeroCnt : "Unexpect zeroCnt";
      System.err.println(waitGroup.getCount());
      server.interrupt();
      client.interrupt();
      System.out.println("[main] Server is interrupted: " + server.isInterrupted());
      System.out.println("[main] Client is interrupted: " + client.isInterrupted());
    } catch (InterruptedException e) {
      System.err.println(e.getMessage());
    }
  }
}
