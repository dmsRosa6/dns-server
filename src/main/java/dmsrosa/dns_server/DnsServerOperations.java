package dmsrosa.dns_server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import dmsrosa.dns_server.messages.DnsHeader;
import dmsrosa.dns_server.messages.DnsPacket;
import dmsrosa.dns_server.messages.DnsQuestion;
import dmsrosa.dns_server.messages.QueryType;
import dmsrosa.dns_server.messages.ResultCode;

public class DnsServerOperations {

    public static void handleQuery(DatagramSocket socket) throws IOException{
        if(socket.isClosed()){
            throw new RuntimeException("Socket is closed");
        }

        BytePacketBuffer reqBuffer = new BytePacketBuffer();
        DatagramPacket packet = new DatagramPacket(reqBuffer.getBuffer(), reqBuffer.getBuffer().length);
        socket.receive(packet);

        DnsPacket receivePacket = DnsPacket.read(reqBuffer);


        DnsPacket packet2Send = new DnsPacket();
        DnsHeader h = packet2Send.getHeader();

        h.setPacketID(receivePacket.getHeader().getPacketID());
        h.setRD(true);
        h.setRA(true);
        h.setResponse(true);

        if(receivePacket.getQuestions().size() == 1){
            DnsQuestion q = receivePacket.getQuestions().getFirst();
            
            String name = q.getName();
            QueryType qType = q.getType();
            try {
                DnsPacket l = lookup(name, qType);

                if (l == null) {
                    throw new IOException();
                }

                packet2Send.getQuestions().add(q);
                h.setRCODE(ResultCode.NO_ERROR);

                packet2Send.setAnswers(l.getAnswers());
                packet2Send.setAuthorities(l.getAuthorities());
                packet2Send.setResources(l.getResources());

            }catch(IOException e){
                h.setRCODE(ResultCode.SERVER_FAILURE);
            }
        }else{
            h.setRCODE(ResultCode.FORMAT_ERROR);
        }

        BytePacketBuffer buffer = new BytePacketBuffer();

        packet2Send.write(buffer);

        int len = buffer.getPos();
        byte[] data = buffer.getRange(0, len);

        DatagramPacket dp = new DatagramPacket(data, len, packet.getAddress(), packet.getPort());

        socket.send(dp);
    }

    public static DnsPacket lookup(String qname, QueryType qType) throws IOException{
        
        InetAddress dnsServer = InetAddress.getByName("8.8.8.8");
        int dnsPort = 53;

        try (DatagramSocket socket = new DatagramSocket(43210)) {
            DnsPacket packet = new DnsPacket();
            packet.getHeader().setPacketID((short) 6666);
            packet.getHeader().setQDCOUNT((short) 1);
            packet.getHeader().setRD(true);
            packet.getQuestions().add(new DnsQuestion(qname, qType));

            BytePacketBuffer reqBuffer = new BytePacketBuffer();
            packet.write(reqBuffer);

            byte[] sendData = reqBuffer.getBuffer();
            DatagramPacket sendPacket = new DatagramPacket(sendData, reqBuffer.getPos(), dnsServer, dnsPort);
            socket.send(sendPacket);

            BytePacketBuffer resBuffer = new BytePacketBuffer();
            DatagramPacket receivePacket = new DatagramPacket(resBuffer.getBuffer(), resBuffer.getBuffer().length);
            socket.receive(receivePacket);
            
            return DnsPacket.read(resBuffer);
        }
    }

    

}
