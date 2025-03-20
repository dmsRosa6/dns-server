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

    @Override
    public int write(BytePacketBuffer writer) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'write'");
    }
}
