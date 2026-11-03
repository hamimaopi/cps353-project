package api;

public class JobResult {
    public enum Status { SUCCESS, FAILURE }

    private final Status status;
    private final String message;

    public JobResult(Status status, String message) {
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
