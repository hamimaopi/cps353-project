package api;

public class ComputeJobRequest {

    private final InputConfig inputConfig;
    private final OutputConfig outputConfig;
    private final Character delimiter;

    public ComputeJobRequest(InputConfig inputConfig, OutputConfig outputConfig) {
        this(inputConfig, outputConfig, null);
    }

    public ComputeJobRequest(InputConfig inputConfig, OutputConfig outputConfig, Character delimiter) {
        this.inputConfig = inputConfig;
        this.outputConfig = outputConfig;
        this.delimiter = delimiter;
    }

    public InputConfig getInputConfig() {
        return inputConfig;
    }

    public OutputConfig getOutputConfig() {
        return outputConfig;
    }

    public Character getDelimiter() {
        return delimiter;
    }
}
