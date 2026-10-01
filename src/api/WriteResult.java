package api;

public class WriteResult {
    public enum Status { SUCCESS, FAILURE }

    private final Status status;
    private final String message;

    public WriteResult(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
