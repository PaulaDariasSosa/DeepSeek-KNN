package datos;

public class DatasetOperationException extends RuntimeException {
    public DatasetOperationException(String message) {
        super(message);
    }

    public DatasetOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
