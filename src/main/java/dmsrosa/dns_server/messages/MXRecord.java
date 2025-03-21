package dmsrosa.dns_server.messages;

import dmsrosa.dns_server.BytePacketBuffer;

public class MXRecord extends DnsRecord {
    private final String host;
    private final short priority;

    protected MXRecord(String domain, String host, short priority, int ttl, QueryType c) {
        super(domain, ttl, c);
        this.host = host;
        this.priority = priority;
    }

    protected MXRecord(String domain, String host, short priority, int ttl) {
        super(domain, ttl, new QueryType.MXQueryType());
        this.host = host;
        this.priority = priority;
    }

    public String getHost() {
        return host;
    }

    public short getPriority() {
        return priority;
    }

    public static MXRecord createFromBuffer(BytePacketBuffer reader, String domain, int ttl) {
        int prio = reader.read2Bytes();
        String mx = reader.readQName();

        return new MXRecord(domain, mx, (short) prio, ttl, new QueryType.NSQueryType());
    }

    @Override
    public int write(BytePacketBuffer writer) {
        int start = writer.getPos();

        writer.writeQName(getDomain());
        writer.write2Bytes((short) 15);
        writer.write2Bytes((short) 1);
        writer.write4Bytes(getTtl());

        writer.write2Bytes((short) 0);
        int pos = writer.getPos();

        writer.write2Bytes(priority);
        writer.writeQName(host);

        int size = writer.getPos() - pos;

        writer.set2Bytes(pos - 2, (short) size);

        return writer.getPos() - start;
    }

    @Override
    public String toString() {
        return "MXRecord { " +
                "domain: " + getDomain() +
                ", ttl: " + getTtl() +
                ", host: " + getHost() +
                ", priority: " + getPriority() +
                " }";
    }
}
