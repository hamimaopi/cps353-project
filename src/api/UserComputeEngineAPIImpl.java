package api;

public class UserComputeEngineAPIImpl implements UserComputeEngineAPI {

    private final DataStorageAPI dataStorageAPI;
    private final ComputationAPI computationAPI;

    public UserComputeEngineAPIImpl(DataStorageAPI dataStorageAPI, ComputationAPI computationAPI) {
        this.dataStorageAPI = dataStorageAPI;
        this.computationAPI = computationAPI;
    }

    @Override
    public void configureJob(String inputSource, String outputDestination, char delimiter) {
        // Empty implementation for checkpoint 3
    }

    @Override
    public ComputeJobResponse compute(ComputeJobRequest request) {
        // Returns default failure response for checkpoint 3
        return new ComputeJobResponse(ComputeJobStatus.FAILURE, "Not yet implemented");
    }
}
