package dmsrosa.dns_server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Optional;

import dmsrosa.dns_server.exceptions.QNameNotFound;
import dmsrosa.dns_server.messages.DnsHeader;
import dmsrosa.dns_server.messages.DnsPacket;
import dmsrosa.dns_server.messages.DnsQuestion;
import dmsrosa.dns_server.messages.QueryType;
import dmsrosa.dns_server.messages.ResultCode;

public class DnsServerOperations {

    public static void handleQuery(DatagramSocket socket, Cache cache, ZoneStorage zStorage) throws IOException {

        if (socket.isClosed()) {
            throw new RuntimeException("Socket is closed");
        }

        BytePacketBuffer reqBuffer = new BytePacketBuffer();
        DatagramPacket packet = new DatagramPacket(reqBuffer.getBuffer(), reqBuffer.getBuffer().length);
        socket.receive(packet);

        DnsPacket receivePacket = DnsPacket.read(reqBuffer);

        DnsPacket packet2Send = new DnsPacket();
        DnsHeader h = packet2Send.getHeader();

        System.out.println("Received query: " + receivePacket.getQuestions().getFirst());

        h.setPacketID(receivePacket.getHeader().getPacketID());
        h.setRD(true);
        h.setRA(true);
        h.setResponse(true);
        if (receivePacket.getQuestions().size() == 1) {
            DnsQuestion q = receivePacket.getQuestions().getFirst();

            String name = q.getName();
            QueryType qType = q.getType();
            try {
                DnsPacket l = lookup(name, qType, cache, zStorage);

                if (l == null) {
                    throw new IOException();
                }

                packet2Send.getQuestions().add(q);
                h.setRCODE(ResultCode.NO_ERROR);

                packet2Send.setAnswers(l.getAnswers());
                cache.store(l.getAnswers());
                packet2Send.setAuthorities(l.getAuthorities());
                packet2Send.setResources(l.getResources());

            } catch (IOException e) {
                h.setRCODE(ResultCode.SERVER_FAILURE);
            }
        } else {
            h.setRCODE(ResultCode.FORMAT_ERROR);
        }

        BytePacketBuffer buffer = new BytePacketBuffer();

        packet2Send.write(buffer);
        System.out.println("Response :" + packet2Send.getAnswers().getFirst());
        int len = buffer.getPos();
        byte[] data = buffer.getRange(0, len);

        DatagramPacket dp = new DatagramPacket(data, len, packet.getAddress(), packet.getPort());

        socket.send(dp);
    }

    private static DnsPacket look(DatagramSocket socket, InetAddress host, int port, String qname,
            QueryType qType) throws IOException {

        System.out.println("Lookup on ns: " + host.getHostAddress());
        DnsPacket packet = new DnsPacket();
        packet.getHeader().setPacketID((short) 6666);
        packet.getHeader().setQDCOUNT((short) 1);
        packet.getHeader().setRD(true);
        packet.getQuestions().add(new DnsQuestion(qname, qType));

        BytePacketBuffer reqBuffer = new BytePacketBuffer();
        packet.write(reqBuffer);

        byte[] sendData = reqBuffer.getBuffer();
        DatagramPacket sendPacket = new DatagramPacket(sendData, reqBuffer.getPos(), host, port);
        socket.send(sendPacket);

        BytePacketBuffer resBuffer = new BytePacketBuffer();
        DatagramPacket receivePacket = new DatagramPacket(resBuffer.getBuffer(), resBuffer.getBuffer().length);
        socket.receive(receivePacket);

        return DnsPacket.read(resBuffer);
    }

    // TODO maybe create a response that can be string or dnspacket
    public static DnsPacket lookup(String qname, QueryType qType, Cache cache, ZoneStorage zStorage)
            throws IOException {
        DnsPacket cached = cache.lookup(qname, qType);

        if (cached != null) {
            System.out.println("Using cache");
            return cached;
        }

        DnsPacket a = zStorage.lookup(qname, qType);

        if (a != null) {
            return a;
        }

        InetAddress addr = InetAddress.getByName("198.41.0.4");
        int port = 53;
        DnsPacket packet = null;
        System.out.println("Performing lookup for: " + qname);

        try (DatagramSocket socket = new DatagramSocket(43210)) {
            while (true) {
                packet = look(socket, addr, port, qname, qType);

                if (!packet.getAnswers().isEmpty() && packet.getHeader().getRCODE().equals(ResultCode.NO_ERROR)) {
                    return packet;
                }

                Optional<InetAddress> nextAddr = packet.getResolvedNS(qname);
                if (nextAddr.isPresent()) {
                    addr = nextAddr.get();
                    continue;
                }

                Optional<String> nextQName = packet.getUnresolvedNS(qname);

                if (!nextQName.isPresent()) {
                    // TODO This is weird technically is not found
                    // i will throw a exception for now
                    throw new QNameNotFound("No NS records exist");
                }

                DnsPacket p = lookup(nextQName.get(), new QueryType.AQueryType(), cache, zStorage);
                nextAddr = p.getRandomA();

                if (!nextAddr.isPresent()) {
                    // TODO Again this is also weird we did not find any record, maybe i should
                    // return the string from last response
                    throw new QNameNotFound("No NS records exist");
                }

                addr = nextAddr.get();
            }
        }

    }

}