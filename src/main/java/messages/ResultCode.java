package messages;

public enum ResultCode {
    
    NO_ERROR("No Error", (byte) 0),
    FORMAT_ERROR("Format Error", (byte) 1),
    SERVER_FAILURE("Server Failure", (byte) 2),
    NAME_ERROR("Name Error", (byte) 3),
    NOT_IMPLEMENTED("Not Implemented", (byte) 4),
    REFUSED("Refused", (byte) 5);

    private final byte code;
    private final String description;

    private ResultCode(String description, byte code) {
        this.description = description;
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
