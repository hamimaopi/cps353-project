import api.DataStorageAPI;
import api.InputConfig;
import api.OutputConfig;

import java.util.Collections;
import java.util.List;

public class InMemoryDataStorage implements DataStorageAPI {

    @Override
    public List<Integer> readInputData(String inputSource) {
        return Collections.emptyList();
    }

    @Override
    public List<Integer> readInputData(InputConfig inputConfig) {
        if (inputConfig instanceof InMemoryInputConfig inMemoryInput) {
            return inMemoryInput.getInputs();
        }
        return Collections.emptyList();
    }

    @Override
    public void writeOutputData(OutputConfig outputConfig, List<String> outputData) {
        if (outputConfig instanceof InMemoryOutputConfig inMemoryOutput) {
            inMemoryOutput.getOutputs().addAll(outputData);
        }
    }
}
