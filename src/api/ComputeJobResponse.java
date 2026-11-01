package api;

public class ComputeJobResponse {

    private final ComputeJobStatus status;
    private final String message;

    public ComputeJobResponse(ComputeJobStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ComputeJobStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
