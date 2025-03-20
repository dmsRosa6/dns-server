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

    @Override
    public int write(BytePacketBuffer writer) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'write'");
    }
}
