package dmsrosa.dns_server.messages;

import dmsrosa.dns_server.BytePacketBuffer;

public class TXTRecord extends DnsRecord {

    private String data;

    public TXTRecord(String data, String domain, int ttl) {
        super(domain, ttl, new QueryType.TXTQueryType());
        this.data = data;
    }

    public String getData() {
        return data;
    }

    @Override
    public int write(BytePacketBuffer writer) {
        int start = writer.getPos();

        writer.writeQName(getDomain());
        writer.write2Bytes((short) 16);
        writer.write2Bytes((short) 1);
        writer.write4Bytes(getTtl());

        writer.write2Bytes((short) data.length());

        for (Byte b : data.getBytes()) {
            writer.writeByte(b);
        }

        return writer.getPos() - start;
    }

    public static DnsRecord createFromBuffer(BytePacketBuffer reader, String domain, int ttl) {
        int len = reader.read2Bytes();
        int pos = reader.getPos();

        byte[] arr = reader.getRange(pos, len);
        String data = String.valueOf(arr);
        reader.step(len);

        return new TXTRecord(data, domain, ttl);
    }

}
