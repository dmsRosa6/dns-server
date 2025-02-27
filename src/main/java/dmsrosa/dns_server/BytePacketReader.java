package dmsrosa.dns_server;

import java.util.Arrays;

public class BytePacketReader {
 
    private final byte[] buffer;
    private int index;

    public BytePacketReader(byte[] buffer){
        this.buffer = buffer;
        index = 0;
    }

    public BytePacketReader(){
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

    public byte[] getBuffer(){
        return buffer;
    }

    public String readQName(){

        int pos = this.index;

        boolean jumped = false;
        int maxJumps = 5;
        int numJumps = 0;

        String delim = "";

        StringBuilder sb = new StringBuilder();

        while (true) { 

            if(numJumps > maxJumps){
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
}
