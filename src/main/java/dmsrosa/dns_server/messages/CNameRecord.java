package dmsrosa.dns_server.messages;

import dmsrosa.dns_server.BytePacketBuffer;

public class CNameRecord extends DnsRecord {
    private final String host;

    protected CNameRecord(String domain, String host, int ttl, QueryType c) {
        super(domain, ttl, c);
        this.host = host;
    }

    protected CNameRecord(String domain, String host, int ttl) {
        super(domain, ttl, new QueryType.CNameQueryType());
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public static CNameRecord createFromBuffer(BytePacketBuffer reader, String domain, int ttl) {
        String cname = reader.readQName();

        return new CNameRecord(domain, cname, ttl, new QueryType.NSQueryType());
    }

    public int write(BytePacketBuffer writer) {
        int start = writer.getPos();

        writer.writeQName(getDomain());
        writer.write2Bytes((short) 5);
        writer.write2Bytes((short) 1);
        writer.write4Bytes(getTtl());

        writer.write2Bytes((short) 0);

        int pos = writer.getPos();

        writer.writeQName(host);

        int size = pos - writer.getPos();

        writer.set2Bytes(pos - 2, (short) size);

        return writer.getPos() - start;
    }

    @Override
    public String toString() {
        return "CNameRecord { " +
                "domain: " + super.getDomain() +
                ", ttl: " + super.getTtl() +
                ", host: " + getHost() +
                " }";
    }
}
