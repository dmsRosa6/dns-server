package dmsrosa.dns_server.messages;

import java.net.Inet4Address;

class ARecord extends DnsRecord {
    private final Inet4Address addr;

    public ARecord(String domain, Inet4Address addr, int ttl) {
        super(domain, ttl);
        this.addr = addr;
    }

    public Inet4Address getAddr() {
        return addr;
    }

    @Override
    public String toString() {
        return "ARecord{" +
                "domain='" + getDomain() + '\'' +
                ", addr=" + addr.getHostAddress() +
                ", ttl=" + getTtl() +
                '}';
    }
}

