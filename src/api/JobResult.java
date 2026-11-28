package api;

import java.util.Collections;
import java.util.List;

public class JobResult {
    public enum Status { SUCCESS, FAILURE }

    private final Status status;
    private final String message;
    private final List<String> results;

    public JobResult(Status status, String message) {
        this(status, message, Collections.emptyList());
    }

    public JobResult(Status status, String message, List<String> results) {
        this.status = status;
        this.message = message;
        this.results = (results != null) ? List.copyOf(results) : Collections.emptyList();
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getResults() {
        return Collections.unmodifiableList(results);
    }
}
