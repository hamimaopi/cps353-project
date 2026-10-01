package api;

import project.annotations.ProcessAPI;
import java.util.List;

@ProcessAPI
public interface DataStorageAPI {

    List<Integer> readInputData(InputConfig inputSource);

    WriteResult writeOutputData(OutputConfig outputDestination, List<String> outputData);
}