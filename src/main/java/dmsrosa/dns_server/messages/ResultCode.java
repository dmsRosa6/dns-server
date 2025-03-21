package dmsrosa.dns_server.messages;

public enum ResultCode {

    NO_ERROR("No Error", (byte) 0),
    FORMAT_ERROR("Format Error", (byte) 1),
    SERVER_FAILURE("Server Failure", (byte) 2),
    NX_DOMAIN("Domain doesnt exist", (byte) 3),
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

    public static ResultCode fromCode(byte code) {
        for (ResultCode elem : ResultCode.values()) {
            if (elem.getCode() == code) {
                return elem;
            }
        }

        return NO_ERROR;
    }
}
