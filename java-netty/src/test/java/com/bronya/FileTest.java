package com.bronya;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class FileTest {

  @Test
  public void testTransferTo() {
    String fromFile = "data.txt"; // 可以指定一个很大的文件
    String toFile = "to.txt";
    long start = System.nanoTime();
    try (
    // 通过 FileInputStream 获取的 channel 只读
    var fromStream = new FileInputStream(fromFile);
        FileChannel fromChan = fromStream.getChannel();
        // 通过 FileOutputStream 获取的 channel 只写
        var toStream = new FileOutputStream(toFile);
        FileChannel toChan = toStream.getChannel()) {
      long size = fromChan.size();
      for (long left = size; left > 0; ) {
        var position = size - left;
        log.info("size: {}, position: {}, left: {}", size, position, left);
        left -= fromChan.transferTo(position, left, toChan);
      }
    } catch (IOException e) {
      log.error(e.getMessage());
    }
    long end = System.nanoTime();
    log.info("Transfer to: {}", (end - start) / 1000_000.);
  }
}
