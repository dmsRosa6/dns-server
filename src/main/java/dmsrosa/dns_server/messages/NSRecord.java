package dmsrosa.dns_server.messages;

import dmsrosa.dns_server.BytePacketBuffer;

public class NSRecord extends DnsRecord{
    private final String host;

    protected NSRecord(String domain, String host, int ttl, QueryType queryType) {
        super(domain, ttl, queryType);
        this.host = host;
    }

    protected NSRecord(String domain, String host, int ttl) {
        super(domain, ttl, new QueryType.NSQueryType());
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public static NSRecord createFromBuffer(BytePacketBuffer reader, String domain, int ttl){
        String ns = reader.readQName();

        return new NSRecord(domain,ns,ttl, new QueryType.NSQueryType());
    }

    public int write(BytePacketBuffer writer){
        int start = writer.getPos();

        writer.writeQName(getDomain());
        writer.write2Bytes((short) 2);
        writer.write2Bytes((short) 1);
        writer.write4Bytes(getTtl());

        writer.write2Bytes((short) 0);
        int pos = writer.getPos();
        writer.writeQName(host);

        int size = writer.getPos() - pos;

        writer.set2Bytes(pos, (short) size);

        return writer.getPos() - start;
    }

    @Override
    public String toString(){
        return "NSRecord { " +
                "domain: " + getDomain() +
                ", ttl: " + getTtl() +
                ", host: " + getHost()+
                " }";
    }
}
