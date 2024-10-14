package com.bronya;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
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
                  // 创建 selector 多路复用器，调度多个 channel
                  Selector selector = Selector.open()) {
                listener.bind(new InetSocketAddress(/* "0.0.0.0" wildcard address */ 3261));
                listener.configureBlocking(false);

                // * SelectionKey.OP_ACCEPT  有新连接可接受时触发
                // * SelectionKey.OP_CONNECT 有新连接可建立时触发
                // * SelectionKey.OP_READ    有新数据可读时触发
                // * SelectionKey.OP_WRITE   有新数据可写时触发

                // ! register 方法：向 selector 中注册 channel
                // selector 关注 listener 的 OP_ACCEPT 事件
                SelectionKey listenerKey = listener.register(selector, 0, null);
                listenerKey.interestOps(SelectionKey.OP_ACCEPT);
                // 等价于 listener.register(selector, SelectionKey.OP_ACCEPT);

                while (!Thread.currentThread().isInterrupted()) {
                  // select()             阻塞直到有事件触发
                  // select(long timeout) 阻塞直到有事件触发，或超时
                  // selectNow()          非阻塞
                  // 选择一组可接收连接、可建立连接、可读、可写的（已注册）通道对应的键
                  // 返回被选择的键的数量
                  int nKeys = selector.select();
                  // int nEventKeys = selector.selectNow();
                  System.out.println("[server] Number of selected keys: " + nKeys);

                  // selector.selectedKeys();
                  // 返回被选择的键的集合 selectedKeys
                  Set<SelectionKey> selectedKeys = selector.selectedKeys();

                  Iterator<SelectionKey> iter = selectedKeys.iterator();
                  while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    if (key.isAcceptable()) {
                      System.out.println("[server] Select an acceptable channel");
                      // 获取键对应的事件（通道）
                      var listener_ = (ServerSocketChannel) key.channel();
                      if (listener != listener_) {
                        throw new RuntimeException("[server] Unexpected acceptable channel");
                      }
                      SocketChannel socket = listener.accept();
                      socket.configureBlocking(false);
                      // 将 socket 注册到 selector
                      // selector 关注 socket 的 OP_READ 事件
                      SelectionKey socketKey = socket.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                      System.out.println("[server] Select a readable channel");
                      var socket_ = (SocketChannel) key.channel();
                      ByteBuffer buffer = ByteBuffer.allocate(128);
                      int nBytes = socket_.read(buffer);
                      if (nBytes == -1) {
                        // cancel 方法
                        // 1. 从 selector 中取消注册该 key 对应的 channel
                        // 2. 从被选择的键的集合 selectedKeys 中移除该 key
                        key.cancel(); // 取消事件
                        socket_.close();
                      } else {
                        // 读 buf 前调用 flip 方法
                        buffer.flip();
                        System.out.println(
                            "[server] Received -- "
                                + StandardCharsets.UTF_8.decode(buffer).toString());
                      }
                    }
                    // 事件处理结束
                    // 必须从被选择的键的集合 selectedKeys 中移除事件的键
                    iter.remove();
                  }
                }
              } catch (IOException | RuntimeException e) {
                System.err.println(e.getMessage());
              } finally {
                waitGroup.countDown();
                System.out.println(
                    "[server] Is interrupted: " + Thread.currentThread().isInterrupted());
                System.out.println("[server] Waiting for: " + waitGroup.getCount() + " threads");
              }
            });

    var client =
        new Thread(
            () -> {
              try (Socket socket = new Socket("localhost", 3261)) {
                socket.getOutputStream().write("Greeting from client".getBytes());
                while (!Thread.currentThread().isInterrupted())
                  ;
                // 将调用者线程的中断标志设置为 true
                // * Thread.currentThread().interrupt();
                // 获取当前线程的中断标志（是否为 true）
                // * Thread.currentThread().isInterrupted();
                // 获取当前线程的中断标志，并重置为 false
                // * Thread.interrupted()
              } catch (IOException e) {
                System.err.println(e.getMessage());
              } finally {
                waitGroup.countDown();
                System.out.println(
                    "[client] Is interrupted: " + Thread.currentThread().isInterrupted());
                System.out.println("[client] Waiting for: " + waitGroup.getCount() + " threads");
              }
            });

    server.start();
    client.start();
    try {
      boolean zeroCnt = waitGroup.await(5, TimeUnit.SECONDS);
      assert !zeroCnt : "Unexpect zeroCnt";
      server.interrupt();
      client.interrupt();
      System.out.println("[main] Server is interrupted: " + server.isInterrupted());
      System.out.println("[main] Client is interrupted: " + client.isInterrupted());
      System.out.println("[main] Waiting for: " + waitGroup.getCount() + " threads");
    } catch (InterruptedException e) {
      System.err.println(e.getMessage());
    }
  }
}
