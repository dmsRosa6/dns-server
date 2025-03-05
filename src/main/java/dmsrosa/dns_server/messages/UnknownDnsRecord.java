package dmsrosa.dns_server.messages;

import dmsrosa.dns_server.BytePacketBuffer;

class UnknownDnsRecord extends DnsRecord {
    private final int type;
    private final int dataLen;

    public UnknownDnsRecord(String domain, int type, int dataLen, int ttl) {
        super(domain, ttl);
        this.type = type;
        this.dataLen = dataLen;
    }

    public int getType() {
        return type;
    }

    public int getDataLen() {
        return dataLen;
    }


    @Override
    public int write(BytePacketBuffer writer) {
        throw new RuntimeException("Unknown Record type!");
    }

    @Override
    public String toString() {
        return "UnknownDnsRecord{" +
                "domain='" + getDomain() +
                ", qtype=" + type +
                ", dataLen=" + dataLen +
                ", ttl=" + getTtl() +
                '}';
    }
}

