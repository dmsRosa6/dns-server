package dmsrosa.dns_server.messages;

import dmsrosa.dns_server.BytePacketBuffer;

public class TXTRecord extends DnsRecord {

    private String data;

    public TXTRecord(String data, String domain, int ttl) {
        super(domain, ttl, new QueryType.TXTQueryType());
        this.data = data;
    }

    @Override
    public int write(BytePacketBuffer writer) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'write'");
    }

}
