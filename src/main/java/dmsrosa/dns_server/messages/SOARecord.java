package dmsrosa.dns_server.messages;

import dmsrosa.dns_server.BytePacketBuffer;

public class SOARecord extends DnsRecord {
    private String m_name;
    private String r_name;
    private int serial;
    private int refresh;
    private int retry;
    private int expire;
    private int minimum;

    public SOARecord(String m_name, String r_name, int serial, int refresh, int retry, int expire, int minimum,
            String domain, int ttl) {
        super(domain, ttl);
        this.m_name = m_name;
        this.r_name = r_name;
        this.serial = serial;
        this.refresh = refresh;
        this.retry = retry;
        this.expire = expire;
        this.minimum = minimum;
    }

    public String getM_name() {
        return m_name;
    }

    public String getR_name() {
        return r_name;
    }

    public int getMinimum() {
        return minimum;
    }

    public int getExpire() {
        return expire;
    }

    public int getRefresh() {
        return refresh;
    }

    public int getSerial() {
        return serial;
    }

    public int getRetry() {
        return retry;
    }

    @Override
    public int write(BytePacketBuffer writer) {

        int start = writer.getPos();

        writer.writeQName(getDomain());
        writer.write2Bytes((short) 6);
        writer.write2Bytes((short) 1);
        writer.write4Bytes(getTtl());

        writer.write2Bytes((short) 0);

        int pos = writer.getPos();

        writer.writeQName(m_name);
        writer.writeQName(r_name);
        writer.write4Bytes(serial);
        writer.write4Bytes(refresh);
        writer.write4Bytes(retry);
        writer.write4Bytes(expire);
        writer.write4Bytes(minimum);

        int len = writer.getPos() - pos;

        writer.set2Bytes(pos - 2, (short) len);

        return writer.getPos() - start;
    }

    public static DnsRecord createFromBuffer(BytePacketBuffer reader, String domain, int ttl) {
        String m_name = reader.readQName();
        String r_name = reader.readQName();
        int serial = reader.read4Bytes();
        int refresh = reader.read4Bytes();
        int retry = reader.read4Bytes();
        int expire = reader.read4Bytes();
        int minimum = reader.read4Bytes();

        return new SOARecord(m_name, r_name, serial, refresh, retry, expire, minimum, domain, ttl);
    }
}
