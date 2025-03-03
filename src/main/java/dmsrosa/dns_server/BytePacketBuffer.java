package dmsrosa.dns_server;

import java.util.Arrays;

public class BytePacketBuffer {
 
    private final byte[] buffer;
    private int index;

    public BytePacketBuffer(byte[] buffer){
        this.buffer = buffer;
        index = 0;
    }

    public BytePacketBuffer(){
        this.buffer = new byte[512];
        index = 0;
    }

    public byte readByte(){
        return buffer[index++];
    }

    public byte getByte(int pos){
        if(pos < 0 || pos >= buffer.length){
            throw new RuntimeException("Out of the buffer");
        }
        return buffer[pos];
    }

    public int readUnsignedByte() {
        return readByte() & 0XFF;
    }


    public int read2Bytes(){
        int h = readUnsignedByte();
        int l = readUnsignedByte();

        h = h << 8;
        return h | l;
    }

    public int read4Bytes(){
        int h = read2Bytes();
        int l = read2Bytes();

        h = h << 16;

        return h | l;
    }

    public byte[] getRange(int start, int len) throws RuntimeException {
        if(start < 0 || start + len > buffer.length){
            throw new RuntimeException("Out of buffer");
        }
        return Arrays.copyOfRange(buffer, start, start + len);
    }
    

    public void seek(int pos){
        index = pos;
    }

    public int getPos(){
        return index;
    }

    public void step(int s){index += s;}

    public byte[] getBuffer(){
        return buffer;
    }

    public String readQName() {
        int pos = this.index;
        boolean jumped = false;
        int maxJumps = 5;
        int numJumps = 0;
        String delim = "";
        StringBuilder sb = new StringBuilder();

        while (true) {
            if (numJumps > maxJumps) {
                throw new RuntimeException("Loop found. Max jumps: " + maxJumps);
            }

            byte len = getByte(pos);

            if ((len & 0xC0) == 0xC0) {
                if (!jumped) {
                    seek(pos + 2);
                }
                byte b2 = getByte(pos + 1);
                int offset = ((len & 0x3F) << 8) | (b2 & 0xFF);
                pos = offset;
                jumped = true;
                numJumps++;
                continue;
            } else {
                pos += 1;
                if (len == 0) {
                    break;
                }
                sb.append(delim);
                byte[] arr = getRange(pos, len);
                sb.append(new String(arr));
                delim = ".";
                pos += len;
            }
        }

        if (!jumped) {
            seek(pos);
        }

        return sb.toString();
    }

    public void writeByte(byte b){
        if(index >= buffer.length){
            throw new RuntimeException("Out of buffer");
        }
        buffer[index] = b;
        index++;
    }

    public void write2Bytes(short b){

        writeByte((byte)(b >> 8));
        writeByte((byte)(b & 0xFF));
    }

    public void write4Bytes(int b){

        writeByte((byte)((b >> 24) & 0xFF));
        writeByte((byte)((b >> 16) & 0xFF));
        writeByte((byte)((b >> 8) & 0xFF));
        writeByte((byte)(b & 0xFF));
    }

    public void writeQName(String qname){
        for(String label: qname.split("\\.")){
            int size = label.length();

            if(size > 63){
                throw new RuntimeException("Single label exceeds the 63 char limit.");
            }

            writeByte((byte) size);
            for(byte b : label.getBytes()){
                writeByte(b);
            }
        }

        writeByte((byte) 0);
    }

    public String getDump() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buffer.length; i++) {
            if (i % 16 == 0) {
                sb.append(String.format("%04X: ", i));
            }
            sb.append(String.format("%02X ", buffer[i]));
            if (i % 16 == 15) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

}
