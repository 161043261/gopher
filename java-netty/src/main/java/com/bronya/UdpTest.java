package com.bronya;

import java.io.IOException;
import java.nio.channels.DatagramChannel;

public class UdpTest {

  public static void main(String[] args) {

    var udpServer = new Thread(() -> {

      try (DatagramChannel entrypoint = DatagramChannel.open()) {
        System.out.println(entrypoint);
      } catch (IOException e) {
        System.err.println(e.getMessage());
      }
    });
  }
}
