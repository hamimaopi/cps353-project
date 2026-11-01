package api;

import java.util.Collections;
import java.util.List;

public class DataStorageAPIImpl implements DataStorageAPI {

    public DataStorageAPIImpl() {
    }

    @Override
    public List<Integer> readInputData(String inputSource) {
        return Collections.emptyList();
    }

    @Override
    public List<Integer> readInputData(InputConfig inputConfig) {
        return Collections.emptyList();
    }

    @Override
    public void writeOutputData(OutputConfig outputConfig, List<String> outputData) {
        // Empty implementation for checkpoint 3
    }
}
