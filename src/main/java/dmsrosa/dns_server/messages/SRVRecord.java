package dmsrosa.dns_server.messages;

import dmsrosa.dns_server.BytePacketBuffer;

public class SRVRecord extends DnsRecord {

    private short priority;
    private short weight;
    private short port;
    private String host;

    public SRVRecord(short priority, short weight, short port, String host, String domain, int ttl) {
        super(domain, ttl, new QueryType.SRVQueryType());
        this.priority = priority;
        this.host = host;
        this.port = port;
        this.weight = weight;
    }

    public short getWeight() {
        return weight;
    }

    public short getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public short getPriority() {
        return priority;
    }

    @Override
    public int write(BytePacketBuffer writer) {
        int start = writer.getPos();

        writer.writeQName(getDomain());
        writer.write2Bytes((short) 33);
        writer.write2Bytes((short) 1);
        writer.write4Bytes(getTtl());
        writer.write2Bytes((short) 0);

        int pos = writer.getPos();

        writer.write2Bytes(priority);
        writer.write2Bytes(weight);
        writer.write2Bytes(port);
        writer.writeQName(host);

        int len = writer.getPos() - pos;
        writer.set2Bytes(pos - 2, (short) len);
        ;
        return writer.getPos() - start;
    }

    public static DnsRecord createFromBuffer(BytePacketBuffer reader, String domain, int ttl) {
        int priority = reader.read2Bytes();
        int weight = reader.read2Bytes();
        int port = reader.read2Bytes();
        String host = reader.readQName();

        return new SRVRecord((short) priority, (short) weight, (short) port, host, domain, ttl);
    }
}
