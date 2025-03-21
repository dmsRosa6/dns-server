package dmsrosa.dns_server.messages;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import dmsrosa.dns_server.BytePacketBuffer;

class ARecord extends DnsRecord {
    private final Inet4Address addr;

    protected ARecord(String domain, Inet4Address addr, int ttl, QueryType c) {
        super(domain, ttl, c);
        this.addr = addr;
    }

    protected ARecord(String domain, Inet4Address addr, int ttl) {
        super(domain, ttl, new QueryType.AQueryType());
        this.addr = addr;
    }

    public Inet4Address getAddr() {
        return addr;
    }

    public static ARecord createFromBuffer(BytePacketBuffer reader, String domain, int ttl)
            throws UnknownHostException {
        int rawAddr = reader.read4Bytes();
        byte[] addrBytes = new byte[4];
        addrBytes[0] = (byte) ((rawAddr >> 24) & 0xFF);
        addrBytes[1] = (byte) ((rawAddr >> 16) & 0xFF);
        addrBytes[2] = (byte) ((rawAddr >> 8) & 0xFF);
        addrBytes[3] = (byte) (rawAddr & 0xFF);
        Inet4Address addr = (Inet4Address) InetAddress.getByAddress(addrBytes);

        return new ARecord(domain, addr, ttl, new QueryType.AQueryType());
    }

    @Override
    public int write(BytePacketBuffer writer) {
        int start = writer.getPos();
        writer.writeQName(getDomain());
        writer.write2Bytes((short) 1);
        writer.write2Bytes((short) 1);
        writer.write4Bytes(getTtl());
        writer.write2Bytes((short) 4);

        for (byte b : addr.getAddress()) {
            writer.writeByte(b);
        }

        return writer.getPos() - start;
    }

    @Override
    public String toString() {
        return "ARecord{" +
                "domain='" + getDomain() + '\'' +
                ", addr=" + addr.getHostAddress() +
                ", ttl=" + getTtl() +
                '}';
    }
}
