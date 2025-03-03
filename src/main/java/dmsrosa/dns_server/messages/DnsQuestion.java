package dmsrosa.dns_server.messages;

import dmsrosa.dns_server.BytePacketBuffer;

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

    public static DnsQuestion read(BytePacketBuffer reader){
        String qname = reader.readQName();
        QueryType t = QueryType.fromNum(reader.read2Bytes());
        reader.read2Bytes(); // TODO: this is for the QCLASS

        return new DnsQuestion(qname,t);
    }

    public void write(BytePacketBuffer writer){
        writer.writeQName(name);
        writer.write2Bytes((short) type.getValue());
        writer.write2Bytes((short) 1);
    }

    @Override
    public String toString(){
        return "DnsQuestion { " +
                "name: " + name +
                ",type: " + type +
                " }";
    }
}
