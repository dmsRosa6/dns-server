package dmsrosa.dns_server;

import dmsrosa.dns_server.messages.DnsPacket;
import dmsrosa.dns_server.messages.DnsQuestion;
import dmsrosa.dns_server.messages.DnsRecord;
import dmsrosa.dns_server.messages.QueryType;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Main {

  public static void main(String[] args) {
    try {
      String qname = "google.com";
      QueryType qtype = new QueryType.AQueryType();
      InetAddress dnsServer = InetAddress.getByName("8.8.8.8");
      int dnsPort = 53;
      DatagramSocket socket = new DatagramSocket(43210);

      DnsPacket packet = new DnsPacket();
      packet.getHeader().setPacketID((short) 6666);
      packet.getHeader().setQDCOUNT((short) 1);
      packet.getHeader().setRD(true);
      packet.getQuestions().add(new DnsQuestion(qname, qtype));

      BytePacketBuffer reqBuffer = new BytePacketBuffer();
      packet.write(reqBuffer);

      byte[] sendData = reqBuffer.getBuffer();
      DatagramPacket sendPacket = new DatagramPacket(sendData, reqBuffer.getPos(), dnsServer, dnsPort);
      socket.send(sendPacket);

      BytePacketBuffer resBuffer = new BytePacketBuffer();
      DatagramPacket receivePacket = new DatagramPacket(resBuffer.getBuffer(), resBuffer.getBuffer().length);
      socket.receive(receivePacket);

      System.out.println("Receiving...");
      System.out.println(resBuffer.getDump());
      DnsPacket resPacket = DnsPacket.read(resBuffer);
      System.out.println(resPacket.getHeader());

      for (DnsQuestion q : resPacket.getQuestions()) {
        System.out.println(q);
      }
      for (DnsRecord rec : resPacket.getAnswers()) {
        System.out.println(rec);
      }
      for (DnsRecord rec : resPacket.getAuthorities()) {
        System.out.println(rec);
      }
      for (DnsRecord rec : resPacket.getResources()) {
        System.out.println(rec);
      }
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
