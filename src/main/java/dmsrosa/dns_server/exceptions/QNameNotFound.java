package dmsrosa.dns_server.exceptions;

public class QNameNotFound extends RuntimeException {

    private static final String MESSAGE = "QName not found, reason: ";

    public QNameNotFound(String m) {
        super(MESSAGE + m);
    }
}
