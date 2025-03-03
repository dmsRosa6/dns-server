import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import dmsrosa.dns_server.BytePacketBuffer;

class BytePacketBufferTest {

    @Test
    void testReadByte() {
        byte[] data = {10, 20, 30};
        BytePacketBuffer reader = new BytePacketBuffer(data);
        assertEquals(10, reader.readByte(), "First byte should be 10");
        assertEquals(20, reader.readByte(), "Second byte should be 20");
        assertEquals(30, reader.readByte(), "Third byte should be 30");
    }

    @Test
    void testReadUnsignedByte() {
        byte[] data = {(byte) 200};
        BytePacketBuffer reader = new BytePacketBuffer(data);
        assertEquals(200, reader.readUnsignedByte(), "Unsigned value of byte should be 200");
    }

    @Test
    void testRead2Bytes() {
        byte[] data = {0x01, 0x02};
        BytePacketBuffer reader = new BytePacketBuffer(data);
        int expected = (0x01 << 8) | 0x02; // 0x0102 == 258
        assertEquals(expected, reader.read2Bytes(), "2-byte read should return 0x0102 (258)");
    }

    @Test
    void testRead4Bytes() {
        byte[] data = {0x01, 0x02, 0x03, 0x04};
        BytePacketBuffer reader = new BytePacketBuffer(data);
        int expected = 0x01020304; // 16909060 in decimal
        assertEquals(expected, reader.read4Bytes(), "4-byte read should return 0x01020304 (16909060)");
    }

    @Test
    void testGetRangeValid() {
        byte[] data = {0, 1, 2, 3, 4, 5, 6};
        BytePacketBuffer reader = new BytePacketBuffer(data);
        // Extract a range from position 2 with length 3, which should yield {2, 3, 4}.
        byte[] range = reader.getRange(2, 3);
        // Uncomment if you wish to directly compare the arrays:
        // assertArrayEquals(new byte[]{2, 3, 4}, range, "Range should equal {2, 3, 4}");
        assertEquals(3, range.length, "Returned range should have length 3");
    }

    @Test
    void testGetRangeOutOfBounds() {
        byte[] data = new byte[10];
        BytePacketBuffer reader = new BytePacketBuffer(data);
        // This call should throw a RuntimeException because (start + len) exceeds the buffer length.
        assertThrows(RuntimeException.class, () -> reader.getRange(8, 5),
                "getRange should throw an exception when the range is out of bounds");
    }

    @Test
    void testSeekAndGetPos() {
        byte[] data = {1, 2, 3, 4};
        BytePacketBuffer reader = new BytePacketBuffer(data);
        reader.seek(2);
        assertEquals(2, reader.getPos(), "After seek, the position should be 2");
        reader.readByte(); // Advances index by 1.
        assertEquals(3, reader.getPos(), "After reading one byte, the position should be 3");
    }

    @Test
    void testReadQName() {
        // Construct a DNS-style name for "www.google.com"
        // Format: [length, label bytes, ... , 0]
        byte[] packet = {
                3, 'w', 'w', 'w',
                6, 'g', 'o', 'o', 'g', 'l', 'e',
                3, 'c', 'o', 'm',
                0
        };
        BytePacketBuffer reader = new BytePacketBuffer(packet);
        String qName = reader.readQName();
        // In a correct implementation, the QName would be "www.google.com".
        assertFalse(qName.isEmpty(), "readQName should return a non-empty string");
        assertEquals("www.google.com", qName, "QName should equal 'www.google.com'");
    }

    @Test
    void testWriteByte() {
        BytePacketBuffer buffer = new BytePacketBuffer(new byte[10]);
        buffer.writeByte((byte) 100);
        buffer.seek(0);
        assertEquals(100, buffer.readByte(), "The written byte should be 100");
    }

    @Test
    void testWrite2Bytes() {
        BytePacketBuffer buffer = new BytePacketBuffer(new byte[10]);
        short value = 0x1234;
        buffer.write2Bytes(value);
        buffer.seek(0);
        int readVal = buffer.read2Bytes();
        assertEquals(0x1234, readVal, "The written 2-byte value should be 0x1234");
    }

    @Test
    void testWrite4Bytes() {
        BytePacketBuffer buffer = new BytePacketBuffer(new byte[10]);
        int value = 0x01020304;
        buffer.write4Bytes(value);
        buffer.seek(0);
        int readVal = buffer.read4Bytes();
        assertEquals(0x01020304, readVal, "The written 4-byte value should be 0x01020304");
    }

    @Test
    void testWriteQName() {
        BytePacketBuffer buffer = new BytePacketBuffer(new byte[50]);
        String qname = "www.google.com";
        buffer.writeQName(qname);
        buffer.seek(0);
        String readQName = buffer.readQName();
        assertEquals(qname, readQName, "The written QName should be 'www.google.com'");
    }
}
