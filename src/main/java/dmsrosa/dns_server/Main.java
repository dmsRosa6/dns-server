package dmsrosa.dns_server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Main {

  public static void main(String[] args) {
    try (DatagramSocket socket = new DatagramSocket(2053)) {

      while (true) { 
          try {
              DnsServerOperations.handleQuery(socket);
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
    } catch (SocketException e) {
      e.printStackTrace();
    }
  }
}
