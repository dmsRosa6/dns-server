package dmsrosa.dns_server.messages;

import dmsrosa.dns_server.BytePacketReader;

public class DnsQuestion {

    private String name;
    private QueryType type;

    public DnsQuestion(String name, QueryType type){
        this.name = name;
        this.type = type;
    }

    public QueryType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static DnsQuestion read(BytePacketReader reader){
        String qname = reader.readQName();
        QueryType t = QueryType.fromNum(reader.read2Bytes());

        return new DnsQuestion(qname,t);
    }
}
