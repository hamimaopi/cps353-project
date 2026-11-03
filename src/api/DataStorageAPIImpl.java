package api;

import java.util.Collections;
import java.util.List;

public class DataStorageAPIImpl implements DataStorageAPI {

    public DataStorageAPIImpl() {
    }

    @Override
    public List<Integer> readInputData(InputConfig inputSource) {
        return Collections.emptyList();
    }

    @Override
    public WriteResult writeOutputData(OutputConfig outputDestination, List<String> outputData) {
        return new WriteResult(WriteResult.Status.FAILURE, "Not yet implemented");
    }
}
