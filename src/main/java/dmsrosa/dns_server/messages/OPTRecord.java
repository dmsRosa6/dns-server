package dmsrosa.dns_server.messages;

import dmsrosa.dns_server.BytePacketBuffer;

public class OPTRecord extends DnsRecord {
    private short packetLen;
    private int flags;
    private String data;

    public OPTRecord(short len, int f, String data) {
        super(new QueryType.OPTQueryType());
        this.packetLen = len;
        this.flags = f;
        this.data = data;
    }

    @Override
    public int write(BytePacketBuffer writer) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'write'");
    }

}
