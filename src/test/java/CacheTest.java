import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import dmsrosa.dns_server.BytePacketBuffer;
import dmsrosa.dns_server.Cache;
import dmsrosa.dns_server.messages.DnsHeader;
import dmsrosa.dns_server.messages.DnsPacket;
import dmsrosa.dns_server.messages.DnsRecord;
import dmsrosa.dns_server.messages.QueryType;
import dmsrosa.dns_server.messages.ResultCode;

// A simple dummy DnsRecord for testing.
class DummyDnsRecord extends DnsRecord {

    public DummyDnsRecord(String domain, int ttl, QueryType qtype) {
        super(domain, ttl, new QueryType.AQueryType());
    }

    // For testing equality based on domain, ttl, and qtype.
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof DummyDnsRecord))
            return false;
        DummyDnsRecord other = (DummyDnsRecord) obj;
        return getDomain().equals(other.getDomain()) && getTtl() == other.getTtl()
                && getQueryType().getValue() == other.getQueryType().getValue();
    }

    @Override
    public int hashCode() {
        return getDomain().hashCode() + getTtl() + getQueryType().getValue();
    }

    @Override
    public int write(BytePacketBuffer writer) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'write'");
    }
}

public class CacheTest {

    @Test
    public void testPositiveCache() {
        Cache cache = new Cache();
        QueryType aQuery = new QueryType.AQueryType();
        String domain = "example.com";
        // Use a TTL of 60 seconds.
        DummyDnsRecord record = new DummyDnsRecord(domain, 60, aQuery);
        // Store the record.
        cache.store(new DnsRecord[] { record });

        // Immediately look up should return a DnsPacket with an answer.
        DnsPacket packet = cache.lookup(domain, aQuery);
        assertNotNull(packet, "Expected a non-null packet from cache");
        List<DnsRecord> answers = packet.getAnswers();
        assertFalse(answers.isEmpty(), "Expected at least one answer in the packet");
        assertEquals(record, answers.get(0), "The cached record should match the stored record");
    }

    @Test
    public void testNegativeCache() {
        Cache cache = new Cache();
        QueryType aQuery = new QueryType.AQueryType();
        String domain = "nonexistent.com";
        // Store a negative cache result with TTL 60.
        cache.storeNXDomain(domain, aQuery, 60);

        DnsPacket packet = cache.lookup(domain, aQuery);
        assertNotNull(packet, "Expected a non-null packet for negative caching");
        DnsHeader header = packet.getHeader();
        assertEquals(ResultCode.NX_DOMAIN, header.getRCODE(), "Expected NX_DOMAIN result code");
    }

    @Test
    public void testTTLExpiration() throws InterruptedException {
        Cache cache = new Cache();
        QueryType aQuery = new QueryType.AQueryType();
        String domain = "expire.com";
        // Use a very short TTL: 1 second.
        DummyDnsRecord record = new DummyDnsRecord(domain, 1, aQuery);
        cache.store(new DnsRecord[] { record });

        // Immediately, the record should be available.
        DnsPacket packet = cache.lookup(domain, aQuery);
        assertNotNull(packet, "Expected record in cache before TTL expiration");

        // Wait for 2 seconds so the TTL expires.
        Thread.sleep(2000);

        // Now, lookup should return null.
        DnsPacket expiredPacket = cache.lookup(domain, aQuery);
        assertNull(expiredPacket, "Expected cache to have expired the record");
    }
}
