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

    public short getPacketLen() {
        return packetLen;
    }

    public int getFlags() {
        return flags;
    }

    public String getData() {
        return data;
    }

    @Override
    public int write(BytePacketBuffer writer) {
        throw new UnsupportedOperationException("Unimplemented method 'write'");
    }

    public static DnsRecord createFromBuffer(BytePacketBuffer reader) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createFromBuffer'");
    }

}
