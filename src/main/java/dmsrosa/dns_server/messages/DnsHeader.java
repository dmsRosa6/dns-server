package dmsrosa.dns_server.messages;


import dmsrosa.dns_server.BytePacketReader;
/**
 * Represents the header of a DNS protocol message as defined in RFC 1035.
 *
 * <p>The DNS header is 12 bytes long and has the following structure:
 * <pre>
 *                                   1  1  1  1  1  1
 *         0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 *        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * ID     |                      Identification             |
 *        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * Flags: |QR|   OpCode  |AA|TC|RD|RA|   Z    |   RCODE    |
 *        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * QDCOUNT| ANCOUNT | NSCOUNT | ARCOUNT
 *        +--------+---------+---------+---------+
 * </pre>
 *
 * <p>Notes:
 * <ul>
 *   <li><b>ID</b>: A 16-bit identifier assigned by the program that generates any kind of query.
 *   <li><b>QR</b>: A 1-bit flag that specifies whether this message is a query (false) or a response (true).
 *   <li><b>OpCode</b>: A 4-bit field specifying the kind of query (values 0–15).
 *   <li><b>AA</b>: A 1-bit flag indicating if the responding server is authoritative.
 *   <li><b>TC</b>: A 1-bit flag that indicates if the message was truncated.
 *   <li><b>RD</b>: A 1-bit flag that directs the server to pursue recursive query behavior.
 *   <li><b>RA</b>: A 1-bit flag that denotes whether recursive query support is available.
 *   <li><b>Z</b>: A 3-bit reserved field, which should always be 0.
 *   <li><b>RCODE</b>: A 4-bit field representing the response code (values 0–15).
 *   <li><b>QDCOUNT, ANCOUNT, NSCOUNT, ARCOUNT</b>: 16-bit fields indicating the number of entries in the question,
 *         answer, authority, and additional sections, respectively.
 * </ul>
 */
public class DnsHeader {

    // Fields
    private short packetID; // 16-bit identifier for the DNS packet
    
    private boolean RD; // Recursion Desired flag
    private boolean TC; // Truncated flag (true if message was truncated)
    private boolean AA; // Authoritative Answer flag
    private byte OpCode; // 4-bit field (values 0-15) specifying the type of query
    private boolean response;
    
    private ResultCode RCODE; // 4-bit response code (values 0-15)
    private boolean CD;
    private boolean authed;
    private boolean Z; // 3-bit reserved field (should be 0); stored in a byte (only lower 3 bits are used)
    private boolean RA; // Recursion Available flag
    
    private short QDCOUNT; // Number of entries in the question section
    private short ANCOUNT; // Number of resource records in the answer section
    private short NSCOUNT; // Number of name server records in the authority section
    private short ARCOUNT; // Number of resource records in the additional section

    public DnsHeader(short packetID, byte OpCode, boolean  AA, boolean  TC, boolean RD, boolean  RA, boolean Z, ResultCode RCODE, short  QDCOUNT, short  ANCOUNT, short  NSCOUNT, short  ARCOUNT, boolean  authed, boolean response, boolean CD){
        this.packetID = packetID;
        this.OpCode = OpCode;
        this.AA = AA;
        this.TC = TC;
        this.RD = RD;
        this.RA = RA;
        this.Z = Z;
        this.RCODE = RCODE;
        this.QDCOUNT = QDCOUNT;
        this.NSCOUNT = NSCOUNT;
        this.ARCOUNT = ARCOUNT;
        this.ANCOUNT = ANCOUNT;
        this.authed = authed;
        this.response = response;
        this.CD = CD;
    }

    // Getters and Setters

    public short getPacketID() {
        return packetID;
    }
    
    public int getOpCode() {
        return OpCode & 0x0F;
    }

    public boolean isAA() {
        return AA;
    }

    public boolean isTC() {
        return TC;
    }

    public boolean isRD() {
        return RD;
    }

    public boolean isRA() {
        return RA;
    }

    public boolean getZ() {
        return Z;
    }

    public ResultCode getRCODE() {
        return RCODE;
    }

    public short getQDCOUNT() {
        return QDCOUNT;
    }

    public short getANCOUNT() {
        return ANCOUNT;
    }

    public short getNSCOUNT() {
        return NSCOUNT;
    }

    public short getARCOUNT() {
        return ARCOUNT;
    }

    public static DnsHeader read(BytePacketReader reader){
        short id = (short) reader.read2Bytes();

        short flags = (short) reader.read2Bytes();

        byte a = (byte) (flags >> 8);
        byte b = (byte) (flags % 0xFF);

        boolean rd = ((a & (1 << 0)) > 0);
        boolean tc = ((a & (1 << 1)) > 0);
        boolean aa = (a & (1 << 2)) > 0;
        byte opcode = (byte) ((a >> 3) & 0x0F);
        boolean response = (a & (1 << 7)) > 0;

        ResultCode rescode = ResultCode.fromCode((byte)(b & 0x0F));
        boolean cd = (b & (1 << 4)) > 0;
        boolean authed_data = (b & (1 << 5)) > 0;
        boolean z = (b & (1 << 6)) > 0;
        boolean ra = (b & (1 << 7)) > 0;
        
        short questions = (short) reader.read2Bytes();
        short answers = (short) reader.read2Bytes();
        short authoritative_entries = (short) reader.read2Bytes();
        short resource_entries = (short) reader.read2Bytes();

        return new DnsHeader(id, opcode, aa, tc, rd, ra, z, rescode, questions, answers, authoritative_entries,resource_entries, authed_data, response, cd);
    }

    @Override
    public String toString() {
        return "Header{" +
                "packetID=" + packetID +
                ", CD=" + CD +
                ", response=" + response +
                ", auhted=" + authed +
                ", OpCode=" + getOpCode() +
                ", AA=" + AA +
                ", TC=" + TC +
                ", RD=" + RD +
                ", RA=" + RA +
                ", Z=" + getZ() +
                ", RCODE=" + getRCODE() +
                ", QDCOUNT=" + QDCOUNT +
                ", ANCOUNT=" + ANCOUNT +
                ", NSCOUNT=" + NSCOUNT +
                ", ARCOUNT=" + ARCOUNT +
                '}';
    }
}
