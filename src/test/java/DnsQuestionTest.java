import dmsrosa.dns_server.BytePacketBuffer;
import dmsrosa.dns_server.messages.DnsQuestion;
import dmsrosa.dns_server.messages.QueryType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for DnsQuestion.read.
 */
public class DnsQuestionTest {

    /**
     * A simple dummy implementation of BytePacketReader for testing purposes.
     * It returns a preset QName and a query type value.
     */
    private static class DummyBytePacketReader extends BytePacketBuffer {
        private final String qname;
        private final int queryTypeValue;
        private boolean qnameRead = false;  // to simulate sequential reads

        public DummyBytePacketReader(String qname, int queryTypeValue) {
            this.qname = qname;
            this.queryTypeValue = queryTypeValue;
        }

        @Override
        public String readQName() {
            // In a real implementation, this would read the QNAME from the DNS packet.
            // Here we simply return the preset qname.
            qnameRead = true;
            return qname;
        }

        @Override
        public int read2Bytes() {
            // After reading the QNAME, the next 2 bytes represent the QueryType.
            // We return the preset value.
            if (!qnameRead) {
                throw new IllegalStateException("QName must be read before reading query type");
            }
            return queryTypeValue;
        }
    }

    /**
     * Test the DnsQuestion.read method by simulating a DNS question.
     */
    @Test
    public void testReadDnsQuestion() {
        String expectedName = "www.example.com";
        int queryTypeValue = 1; // Typically, QueryType.A is represented by the value 1

        // Create a dummy reader with the expected QName and query type value.
        DummyBytePacketReader reader = new DummyBytePacketReader(expectedName, queryTypeValue);

        // Read the DNS question from the dummy reader.
        DnsQuestion question = DnsQuestion.read(reader);

        // Verify that the question name matches the expected value.
        assertEquals(expectedName, question.getName(), "The DNS question name should match the expected value.");

        // Verify that the query type is correctly derived.
        // Here we use the QueryType.fromNum method to generate the expected QueryType.
        QueryType expectedType = QueryType.fromNum(queryTypeValue);
        assertEquals(expectedType, question.getType(), "The DNS question type should match the expected QueryType.");
    }
}
