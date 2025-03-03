import dmsrosa.dns_server.BytePacketBuffer;
import dmsrosa.dns_server.messages.DnsPacket;
import dmsrosa.dns_server.messages.DnsQuestion;
import dmsrosa.dns_server.messages.QueryType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DnsPacketTest {

    @Test
    public void testFromBuffer() throws Exception {
        byte[] dnsPacketData = new byte[] {
                // Header (12 bytes):
                0x12, 0x34,             // Packet ID: 0x1234
                0x01, 0x00,             // Flags: RD set (0x01 0x00)
                0x00, 0x01,             // QDCOUNT: 1
                0x00, 0x00,             // ANCOUNT: 0
                0x00, 0x00,             // NSCOUNT: 0
                0x00, 0x00,             // ARCOUNT: 0
                // Question section:
                0x03, 'w', 'w', 'w',     // Label: "www"
                0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e',  // Label: "example"
                0x03, 'c', 'o', 'm',     // Label: "com"
                0x00,                   // End of QNAME
                0x00, 0x01,             // QTYPE: A (0x0001)
                0x00, 0x01              // QCLASS: IN (0x0001)
        };

        BytePacketBuffer reader = new BytePacketBuffer(dnsPacketData);
        DnsPacket packet = DnsPacket.read(reader);

        assertNotNull(packet);
        assertNotNull(packet.getHeader());
        assertEquals(0x1234, packet.getHeader().getPacketID());
        assertEquals(1, packet.getHeader().getQDCOUNT());
        assertEquals(0, packet.getHeader().getANCOUNT());
        assertEquals(0, packet.getHeader().getNSCOUNT());
        assertEquals(0, packet.getHeader().getARCOUNT());
        assertEquals(1, packet.getQuestions().size());

        DnsQuestion question = packet.getQuestions().get(0);
        assertEquals("www.example.com", question.getName());
        assertTrue(question.getType() instanceof QueryType.AQueryType);
    }
}
