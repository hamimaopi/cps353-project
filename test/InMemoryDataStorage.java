import api.DataStorageAPI;
import api.InputConfig;
import api.OutputConfig;
import api.WriteResult;

import java.util.Collections;
import java.util.List;

public class InMemoryDataStorage implements DataStorageAPI {

    @Override
    public List<Integer> readInputData(InputConfig inputConfig) {
        if (inputConfig instanceof InMemoryInputConfig inMemoryInput) {
            return inMemoryInput.getInputs();
        }
        return Collections.emptyList();
    }

    @Override
    public WriteResult writeOutputData(OutputConfig outputConfig, List<String> outputData) {
        if (outputConfig instanceof InMemoryOutputConfig inMemoryOutput) {
            inMemoryOutput.getOutputs().addAll(outputData);
            return new WriteResult(WriteResult.Status.SUCCESS, "Success");
        }
        return new WriteResult(WriteResult.Status.FAILURE, "Unsupported config");
    }
}
