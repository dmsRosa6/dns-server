package dmsrosa.dns_server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Main {
  private static final int PORT = 2053;

  public static void main(String[] args) {
    try (DatagramSocket socket = new DatagramSocket(PORT)) {

      System.out.println("Server started");
      Cache cache = new Cache();
      ZoneStorage zStorage = new ZoneStorage();

      new Controller(zStorage);

      while (true) {
        try {
          DnsServerOperations.handleQuery(socket, cache, zStorage);

        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } catch (SocketException e) {
      e.printStackTrace();
    }
  }
}
