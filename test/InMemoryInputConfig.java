import api.InputConfig;

import java.util.Collections;
import java.util.List;

public class InMemoryInputConfig implements InputConfig {

    private final List<Integer> inputs;

    public InMemoryInputConfig(List<Integer> inputs) {
        this.inputs = inputs;
    }

    public List<Integer> getInputs() {
        return Collections.unmodifiableList(inputs);
    }

    @Override
    public String getSourceDetails() {
        return "in-memory";
    }
}
