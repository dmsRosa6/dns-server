package dmsrosa.dns_server.messages;

import dmsrosa.dns_server.BytePacketBuffer;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;

public abstract class DnsRecord {
    private final String domain;
    private final int ttl;

    protected DnsRecord(String domain, int ttl) {
        this.domain = domain;
        this.ttl = ttl;
    }

    public String getDomain() {
        return domain;
    }

    public int getTtl() {
        return ttl;
    }

    public static DnsRecord read(BytePacketBuffer reader) throws IOException {
        String domain = reader.readQName();
        int qtypeNum = reader.read2Bytes();
        QueryType qtype = QueryType.fromNum(qtypeNum);
        reader.read2Bytes();
        int ttl = reader.read4Bytes();
        int dataLen = reader.read2Bytes();

        if (qtype instanceof QueryType.AQueryType) {
            int rawAddr = reader.read4Bytes();
            byte[] addrBytes = new byte[4];
            addrBytes[0] = (byte) ((rawAddr >> 24) & 0xFF);
            addrBytes[1] = (byte) ((rawAddr >> 16) & 0xFF);
            addrBytes[2] = (byte) ((rawAddr >> 8) & 0xFF);
            addrBytes[3] = (byte) (rawAddr & 0xFF);
            Inet4Address addr = (Inet4Address) InetAddress.getByAddress(addrBytes);
            return new ARecord(domain, addr, ttl);
        }
        reader.step(dataLen);
        return new UnknownDnsRecord(domain, qtypeNum, dataLen, ttl);
    }

    public int write(BytePacketBuffer writer){
        int start = writer.getPos();

        if(this instanceof ARecord){
            writer.writeQName(domain);
            writer.write2Bytes((short) 1);
            writer.write2Bytes((short) 1);
            writer.write4Bytes(ttl);
            writer.write2Bytes((short) 4);
            for(byte b :((ARecord) this).getAddr().getAddress()){
                writer.writeByte(b);
            }
        }else{
            System.out.println("Skipping unknown record: " + this);
            return -1;
        }

        return writer.getPos() - start;
    }

    @Override
    public String toString(){
        return "DnsRecord { " +
                "domain: " + domain +
                ",ttl: " + ttl +
                " }";
    }
}
