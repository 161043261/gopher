package com.bronya;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j(topic = "ByteBufferTest")
public class ByteBufferTest {

  @Test
  public void testAssertTrue() {
    assertTrue(true);
  }

  // 读前 flip: 写模式 -> 读模式
  // 写前 clear: 清空脏数据
  // 写前 compact: 紧凑数据，不清空数据
  @Test
  public void testByteBuffer() {
    try (var fileInputStream = new FileInputStream("data.txt");
        FileChannel channel = fileInputStream.getChannel()) {

      // 为 buffer 分配 10 个字节
      var buffer = ByteBuffer.allocate(10);
      while (true) {
        // ! <<< 写开始，写前 clear: 清空脏数据

        // []byte{1, 6, 1, 0, 4, 3, 2, 0, 6, 1}
        // clear -> []byte{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        // compact -> []byte{1, 6, 1, 4, 3, 2, 6, 1, 0, 0}

        buffer.clear();
        int nBytes = channel.read(buffer); // 从 channel 中读，向 buffer 中写
        if (nBytes == -1) {
          break;
        }
        // >>> 写结束

        // ! <<< 读开始，读前 flip: 写模式 -> 读模式
        buffer.flip();
        while (buffer.hasRemaining()) {
          byte b = buffer.get(); // 从 buffer 中读一个字符
          System.out.print((char) b);
        }
        // >>> 读结束

        System.out.println(", nBytes: " + nBytes);
      }
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  @Test
  void testToString() {
    ByteBuffer buf1 = StandardCharsets.UTF_8.encode("str1");
    ByteBuffer buf2 = Charset.forName("utf-8").encode("str2");
    CharBuffer buf3 = StandardCharsets.UTF_8.decode(buf1);
    String str3 = buf3.toString();
    System.out.println("str3: " + str3); // str1
  }

  @Test
  void testForkJoin() {
    Thread scatteringRead =
        new Thread(
            () -> {
              try (var file = new RandomAccessFile("len11.txt", "rw")) {
                var channel = file.getChannel();
                var buf1 = ByteBuffer.allocate(3); // one
                var buf2 = ByteBuffer.allocate(3); // two
                var buf3 = ByteBuffer.allocate(5); // three
                var nBytes = channel.read(new ByteBuffer[] {buf1, buf2, buf3});
                assertEquals(11, nBytes);
                // 读前 flip
                buf1.flip();
                buf2.flip();
                buf3.flip();
                log.info(StandardCharsets.UTF_8.decode(buf1).toString());
                log.info(StandardCharsets.UTF_8.decode(buf2).toString());
                log.info(StandardCharsets.UTF_8.decode(buf3).toString());
              } catch (IOException e) {
                log.error(e.getMessage());
              }
            });

    Thread gatheringRead =
        new Thread(
            () -> {
              try (var file = new RandomAccessFile("len11.txt", "rw")) {
                var channel = file.getChannel();
                channel.position(11);
                var nBytes =
                    channel.write(
                        new ByteBuffer[] {
                          StandardCharsets.UTF_8.encode("four"),
                          StandardCharsets.UTF_8.encode("five")
                        });
                assertEquals(nBytes, 8);
              } catch (IOException e) {
                log.error(e.getMessage());
              }
            });
    scatteringRead.start();
    gatheringRead.start();
    try {
      scatteringRead.join();
      gatheringRead.join();
    } catch (InterruptedException e) {
      log.error(e.getMessage());
    }
  }

  // 原数据
  // Hello world!\n
  // I'm Duke.\n
  // How are you?\n

  // 粘包、半包后
  // Hello world!\nI'm Duke.\nHo -- 粘包
  // w are you?\n                -- 半包
  @Test
  void testPacket() {

    @FunctionalInterface
    interface Split {
      void call(ByteBuffer srcBuf);
    }

    Split split =
        srcBuf -> {
          // 读前 flip
          srcBuf.flip();

          assertEquals(srcBuf.position() /* 读写指针 */, 0);
          assertEquals(srcBuf.limit() /* 读写限制 */, srcBuf.remaining());
          assertEquals(srcBuf.capacity() /* 容量 */, 32);

          var len = srcBuf.limit(); // cap = srcBuf.capacity();
          for (int i = 0; i < len; i++) {
            if (srcBuf.get(i) == '\n') { // position 读写指针未移动
              System.out.printf("Line break is at the No.%d element\n", i);
              var dstBuf = ByteBuffer.allocate(i + 1 - srcBuf.position());
              srcBuf.limit(i + 1);
              dstBuf.put(srcBuf);
              // 读前 flip
              dstBuf.flip();
              System.out.println(StandardCharsets.UTF_8.decode(dstBuf).toString());
              srcBuf.limit(len);
            }
          }
          srcBuf.compact();
        };

    var srcBuf = ByteBuffer.allocate(32);
    //                      12         22
    srcBuf.put("Hello world!\nI'm Duke.\nHo".getBytes());
    split.call(srcBuf);
    //                    12
    srcBuf.put("w are you?\n".getBytes());
    split.call(srcBuf);
  }
}
