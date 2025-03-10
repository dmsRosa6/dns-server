package dmsrosa.dns_server.messages;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import dmsrosa.dns_server.BytePacketBuffer;

public class AAAARecord extends DnsRecord {
    private Inet6Address addr;

    protected AAAARecord(String domain, Inet6Address addr, int ttl, QueryType c) {
        super(domain, ttl, c);
        this.addr = addr;
    }

    protected AAAARecord(String domain, Inet6Address addr, int ttl) {
        super(domain, ttl, new QueryType.AAAAQueryType());
        this.addr = addr;
    }

    public Inet6Address getAddr() {
        return addr;
    }

    public static AAAARecord createFromBuffer(BytePacketBuffer reader, String domain, int ttl)
            throws UnknownHostException {
        int rawAddr1 = reader.read4Bytes();
        int rawAddr2 = reader.read4Bytes();
        int rawAddr3 = reader.read4Bytes();
        int rawAddr4 = reader.read4Bytes();
        byte[] addrBytes = new byte[16];

        addrBytes[0] = (byte) ((rawAddr1 >> 24) & 0xFF);
        addrBytes[1] = (byte) ((rawAddr1 >> 16) & 0xFF);
        addrBytes[2] = (byte) ((rawAddr1 >> 8) & 0xFF);
        addrBytes[3] = (byte) (rawAddr1 & 0xFF);

        addrBytes[4] = (byte) ((rawAddr2 >> 24) & 0xFF);
        addrBytes[5] = (byte) ((rawAddr2 >> 16) & 0xFF);
        addrBytes[6] = (byte) ((rawAddr2 >> 8) & 0xFF);
        addrBytes[7] = (byte) (rawAddr2 & 0xFF);

        addrBytes[8] = (byte) ((rawAddr3 >> 24) & 0xFF);
        addrBytes[9] = (byte) ((rawAddr3 >> 16) & 0xFF);
        addrBytes[10] = (byte) ((rawAddr3 >> 8) & 0xFF);
        addrBytes[11] = (byte) (rawAddr3 & 0xFF);

        addrBytes[12] = (byte) ((rawAddr4 >> 24) & 0xFF);
        addrBytes[13] = (byte) ((rawAddr4 >> 16) & 0xFF);
        addrBytes[14] = (byte) ((rawAddr4 >> 8) & 0xFF);
        addrBytes[15] = (byte) (rawAddr4 & 0xFF);

        Inet6Address addr = (Inet6Address) InetAddress.getByAddress(addrBytes);

        return new AAAARecord(domain, addr, ttl, new QueryType.AAAAQueryType());
    }

    @Override
    public int write(BytePacketBuffer writer) {
        int start = writer.getPos();

        writer.writeQName(getDomain());
        writer.write2Bytes((short) 28);
        writer.write2Bytes((short) 1);
        writer.write4Bytes((short) getTtl());
        writer.write2Bytes((short) 16);

        // TODO: this can be problematic
        for (byte b : getAddr().getAddress()) {
            writer.writeByte(b);
        }

        return writer.getPos() - start;
    }

    @Override
    public String toString() {
        return "AAAARecord { " +
                "domain: " + getDomain() +
                ", ttl: " + getTtl() +
                ", addr: " + getAddr().getHostAddress() +
                " }";
    }
}
