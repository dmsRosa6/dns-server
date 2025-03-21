package dmsrosa.dns_server.messages;

import java.io.IOException;

import dmsrosa.dns_server.BytePacketBuffer;

/**
 * Represents a DNS Resource Record as defined in RFC 1035.
 *
 * <p>
 * This abstract class encapsulates the common elements of a DNS record,
 * such as the domain name and TTL (Time-to-Live). The DNS record format
 * generally follows:
 *
 * <pre>
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                                               |
 *   /                     NAME                      /
 *   |                                               |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                     TYPE                      |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                    CLASS                      |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                      TTL                      |
 *   |                                               |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                   RDLENGTH                    |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *   |                     RDATA                     |
 *   |                                               |
 *   +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * </pre>
 *
 * <p>
 * The supported record types in this implementation include:
 * <ul>
 * <li><b>A (1)</b>: Maps a domain name to an IPv4 address.</li>
 * <li><b>NS (2)</b>: Specifies an authoritative name server for the
 * domain.</li>
 * <li><b>CNAME (5)</b>: Provides an alias for the canonical (true) domain
 * name.</li>
 * <li><b>MX (15)</b>: Designates a mail exchange server for handling email for
 * the domain.</li>
 * <li><b>AAAA (28)</b>: Maps a domain name to an IPv6 address.</li>
 * </ul>
 *
 * <p>
 * The static {@code read} method parses a DNS record from a
 * {@link BytePacketBuffer}.
 * It first reads the common header fields (domain, type, class, TTL, and data
 * length)
 * and then delegates to type-specific factory methods (e.g.,
 * {@code ARecord.createFromBuffer})
 * based on the record type.
 *
 * <p>
 * The abstract {@code write} method must be implemented by each concrete
 * subclass to
 * serialize the record back into a {@link BytePacketBuffer} using the proper
 * format.
 *
 * <p>
 * Unknown record types are handled by skipping the data section and creating an
 * instance
 * of {@code UnknownDnsRecord}.
 *
 * @see <a href="https://tools.ietf.org/html/rfc1035">RFC 1035 - Domain Names -
 *      Implementation and Specification</a>
 */
public abstract class DnsRecord {
    private final String domain;
    private final int ttl;
    private final QueryType queryType;

    protected DnsRecord(QueryType qType) {
        this.domain = null;
        this.ttl = -1;
        queryType = qType;
    }

    protected DnsRecord(String domain, int ttl, QueryType c) {
        this.domain = domain;
        this.ttl = ttl;
        this.queryType = c;
    }

    protected DnsRecord(String domain, int ttl) {
        this.domain = domain;
        this.ttl = ttl;
        this.queryType = null;
    }

    public QueryType getQueryType() {
        return queryType;
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

        switch (qtype.getValue()) {
            case 1 -> {
                return ARecord.createFromBuffer(reader, domain, ttl);
            }
            case 2 -> {
                return NSRecord.createFromBuffer(reader, domain, ttl);
            }
            case 5 -> {
                return CNameRecord.createFromBuffer(reader, domain, ttl);
            }
            case 6 -> {
                return SOARecord.createFromBuffer(reader, domain, ttl);
            }
            case 15 -> {
                return MXRecord.createFromBuffer(reader, domain, ttl);
            }
            case 16 -> {
                return TXTRecord.createFromBuffer(reader, domain, ttl);
            }
            case 28 -> {
                return AAAARecord.createFromBuffer(reader, domain, ttl);
            }
            case 33 -> {
                return SRVRecord.createFromBuffer(reader, domain, ttl);
            }
            case 41 -> {
                // return OPTRecord.createFromBuffer(reader);
            }
        }

        reader.step(dataLen);

        return new UnknownDnsRecord(domain, qtypeNum, dataLen, ttl);
    }

    public abstract int write(BytePacketBuffer writer);

    @Override
    public String toString() {
        return "DnsRecord { " +
                "domain: " + domain +
                ", ttl: " + ttl +
                " }";
    }
}
