package dmsrosa.dns_server;

import dmsrosa.dns_server.messages.DnsPacket;
import dmsrosa.dns_server.messages.DnsHeader;
import dmsrosa.dns_server.BytePacketReader;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Main {
  public static void main(String[] args) {
    System.out.println("Logs from your program will appear here!");
    try (DatagramSocket serverSocket = new DatagramSocket(2053)) {
      while (true) {
        byte[] buf = new byte[512];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        serverSocket.receive(packet);
        System.out.println("Received data");

        // Parse the received packet into a DNS query.
        BytePacketReader reader = new BytePacketReader(packet.getData());
        DnsPacket query = DnsPacket.fromBuffer(reader);
        System.out.println("Parsed DNS query: " + query);

      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }

}
