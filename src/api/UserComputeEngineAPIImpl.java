package api;

public class UserComputeEngineAPIImpl implements UserComputeEngineAPI {

    private final DataStorageAPI dataStorageAPI;
    private final ComputationAPI computationAPI;

    public UserComputeEngineAPIImpl(DataStorageAPI dataStorageAPI, ComputationAPI computationAPI) {
        this.dataStorageAPI = dataStorageAPI;
        this.computationAPI = computationAPI;
    }

    @Override
    public JobResult configureJob(InputConfig inputSource, OutputConfig outputDestination, DelimiterConfig delimiterConfig) {
        return new JobResult(JobResult.Status.FAILURE, "Not yet implemented");
    }

    @Override
    public JobResult configureJob(InputConfig inputSource, OutputConfig outputDestination) {
        return new JobResult(JobResult.Status.FAILURE, "Not yet implemented");
    }
}
