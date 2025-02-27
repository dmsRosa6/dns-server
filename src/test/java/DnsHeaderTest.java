import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import dmsrosa.dns_server.BytePacketReader;
import dmsrosa.dns_server.messages.DnsHeader;

/**
 * Test class for DnsHeader.read.
 */
public class DnsHeaderTest {

    /**
     * A simple dummy implementation of BytePacketReader for testing purposes.
     * It reads from a provided byte array.
     */
    private static class DummyBytePacketReader extends BytePacketReader {
        private final byte[] data;
        private int pos = 0;

        public DummyBytePacketReader(byte[] data) {
            this.data = data;
        }

        /**
         * Reads 2 bytes from the internal buffer and returns them as an integer.
         */
        @Override
        public int read2Bytes() {
            if (pos + 1 >= data.length) {
                throw new IllegalStateException("Not enough bytes in the packet");
            }
            int high = data[pos++] & 0xFF;
            int low = data[pos++] & 0xFF;
            return (high << 8) | low;
        }
    }

    /**
     * Tests the DnsHeader.read method by constructing a valid header byte array.
     *
     * The header is constructed as follows (12 bytes total):
     * <pre>
     *  Bytes 0-1: ID = 0x1234
     *  Bytes 2-3: Flags = 0x85FA (crafted so that after the reader's
     *             bit manipulation, we expect:
     *             - RD = true, TC = false, AA = true,
     *             - OpCode = 0, response = true,
     *             and (using the broken modulo extraction) RCODE = 0,
     *             RA = true)
     *  Bytes 4-5: QDCOUNT = 1
     *  Bytes 6-7: ANCOUNT = 1
     *  Bytes 8-9: NSCOUNT = 0
     *  Bytes 10-11: ARCOUNT = 0
     * </pre>
     */
    @Test
    public void testReadDnsHeader() {
        // Construct the header bytes.
        byte[] headerData = new byte[] {
            0x12, 0x34,             // ID = 0x1234
            (byte) 0x85, (byte) 0xFA, // Flags = 0x85FA (crafted value)
            0x00, 0x01,             // QDCOUNT = 1
            0x00, 0x01,             // ANCOUNT = 1
            0x00, 0x00,             // NSCOUNT = 0
            0x00, 0x00              // ARCOUNT = 0
        };

        // Create a dummy reader with the above header data.
        DummyBytePacketReader reader = new DummyBytePacketReader(headerData);

        // Read the header from the dummy reader.
        DnsHeader header = DnsHeader.read(reader);
        System.out.println(header);
        // Assert that the header fields match the values encoded.
        // Note: 0x1234 in decimal is 4660.
        assertEquals(0x1234, header.getPacketID(), "Packet ID should be 0x1234 (4660)");

        // For flags:
        //  - The first byte (0x85) sets: RD (bit 0) true, AA (bit 2) true, response (bit 7) true.
        assertTrue(header.isRD(), "Recursion Desired (RD) should be true");
        assertTrue(header.isAA(), "Authoritative Answer (AA) should be true");
        assertFalse(header.isTC(), "Truncated (TC) should be false");
        // getOpCode() extracts bits 3-6 from the first flag byte. (0x85 >> 3 = 0x10 & 0x0F = 0)
        assertEquals(0, header.getOpCode(), "OpCode should be 0");
        // The second byte is derived using modulo 0xFF.
        // With the crafted flags, RCODE (lower 4 bits) is 0 and RA (bit 7) is true.
        assertEquals(0, header.getRCODE().getCode(), "RCODE should be 0 (NO_ERROR)");
        assertTrue(header.isRA(), "Recursion Available (RA) should be true");
        // The reserved bit (Z) should be false.
        assertFalse(header.getZ(), "Reserved (Z) should be false");

        // Assert the counts.
        assertEquals(1, header.getQDCOUNT(), "QDCOUNT should be 1");
        assertEquals(1, header.getANCOUNT(), "ANCOUNT should be 1");
        assertEquals(0, header.getNSCOUNT(), "NSCOUNT should be 0");
        assertEquals(0, header.getARCOUNT(), "ARCOUNT should be 0");

        // Optionally, check the toString() output contains key values.
        String headerString = header.toString();
        assertTrue(headerString.contains("packetID=4660"), "toString() should include packetID=4660");
        assertTrue(headerString.contains("AA=true"), "toString() should include AA=true");
    }
}
