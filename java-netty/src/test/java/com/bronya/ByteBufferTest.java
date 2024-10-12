package com.bronya;

import io.vertx.core.buffer.Buffer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j(topic = "ByteBufferTest")
public class ByteBufferTest {

  static {
    System.out.println("ByteBufferTest");
  }

  @Test
  public void testAssertTrue() {
    assertTrue(true);
  }

  // 读前 flip: 写模式 -> 读模式
  // 写前 clear: 丢弃脏数据
  // 写前 compact: 不丢弃脏数据
  @Test
  public void testByteBuffer() {
    try (var fileInputStream = new FileInputStream("data.txt");
         FileChannel channel = fileInputStream.getChannel()) {

      // 为 buffer 分配 10 个字节
      var buffer = ByteBuffer.allocate(10);
      while (true) {
        // <<< 写开始，写前 clear: 丢弃脏数据

        //            []byte{1, 6, 1, 0, 4, 3, 2, 0, 6, 1}
        // clear   -> []byte{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        // compact -> []byte{1, 6, 1, 4, 3, 2, 6, 1, 0, 0}

        buffer.clear();
        int byteCnt = channel.read(buffer); // 从 channel 中读数据，向 buffer 中写
        if (byteCnt == -1) {
          break;
        }
        // >>> 写结束

        // <<< 写开始，写前 compact: 不丢弃脏数据
        buffer.compact();

        // <<< 读开始，读前 flip: 写模式 -> 读模式
        buffer.flip();
        while (buffer.hasRemaining()) {
          byte b = buffer.get(); // 从 buffer 中读一个字符
          System.out.print((char) b);
        }
        // >>> 读结束

        System.out.println(", byteCnt: " + byteCnt);
      }
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  // ByteBuffer 方法
  // 分配 jvm 堆内存，不能动态扩容
  // public static ByteBuffer allocate(int capacity);
  // 分配堆外内存，不能动态扩容
  // public static ByteBuffer allocateDirect(int capacity);


}
