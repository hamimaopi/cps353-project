import api.OutputConfig;

import java.util.ArrayList;
import java.util.List;

public class InMemoryOutputConfig implements OutputConfig {

    private final List<String> outputs;

    public InMemoryOutputConfig() {
        this.outputs = new ArrayList<>();
    }

    public InMemoryOutputConfig(List<String> outputs) {
        this.outputs = outputs;
    }

    public List<String> getOutputs() {
        return outputs;
    }
}
