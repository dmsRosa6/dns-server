package dmsrosa.dns_server.messages;

import dmsrosa.dns_server.BytePacketBuffer;

//TODO CHECK THIS FORMAT
public class OPTRecord extends DnsRecord {
    private final short packetLen;
    private final int flags;
    private final String data;

    public OPTRecord(short packetLen, int flags, String data) {
        super(null, 0, new QueryType.OPTQueryType()); // OPT records do not have a domain, and TTL is 0
        this.packetLen = packetLen;
        this.flags = flags;
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
        int start = writer.getPos();
        writer.writeByte((byte) 0);
        writer.write2Bytes((short) 41);
        writer.write2Bytes(packetLen);
        writer.write4Bytes(flags);
        byte[] dataBytes = data.getBytes();
        writer.write2Bytes((short) dataBytes.length);

        for (byte b : dataBytes) {
            writer.writeByte(b);
        }

        return writer.getPos() - start;
    }

    public static OPTRecord createFromBuffer(BytePacketBuffer reader) {
        int packetLen = reader.read2Bytes();
        int flags = reader.read4Bytes();
        int dataLength = reader.read2Bytes();
        byte[] dataBytes = reader.getRange(reader.getPos(), dataLength);
        reader.step(dataLength);
        String data = new String(dataBytes);
        return new OPTRecord((short) packetLen, flags, data);
    }

    @Override
    public String toString() {
        return "OPTRecord { " +
                "packetLen=" + packetLen +
                ", flags=" + flags +
                ", data='" + data + '\'' +
                " }";
    }
}
