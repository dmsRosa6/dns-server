package dmsrosa.dns_server.messages;

import java.net.Inet4Address;

class UnknownDnsRecord extends DnsRecord {
    private final int qtype;
    private final int dataLen;

    public UnknownDnsRecord(String domain, int qtype, int dataLen, int ttl) {
        super(domain, ttl);
        this.qtype = qtype;
        this.dataLen = dataLen;
    }

    public int getQtype() {
        return qtype;
    }

    public int getDataLen() {
        return dataLen;
    }

    @Override
    public String toString() {
        return "UnknownDnsRecord{" +
                "domain='" + getDomain() + '\'' +
                ", qtype=" + qtype +
                ", dataLen=" + dataLen +
                ", ttl=" + getTtl() +
                '}';
    }
}

