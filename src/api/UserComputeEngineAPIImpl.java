package api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserComputeEngineAPIImpl implements UserComputeEngineAPI {

    private final DataStorageAPI dataStorageAPI;
    private final ComputationAPI computationAPI;

    public UserComputeEngineAPIImpl(DataStorageAPI dataStorageAPI, ComputationAPI computationAPI) {
        this.dataStorageAPI = dataStorageAPI;
        this.computationAPI = computationAPI;
    }

    @Override
    public JobResult configureJob(InputConfig inputSource, OutputConfig outputDestination, DelimiterConfig delimiterConfig) {
        JobResult validationResult = validateJobParameters(inputSource, outputDestination);
        if (validationResult != null) {
            return validationResult;
        }

        try {
            List<Integer> inputs = readInputData(inputSource);
            List<String> outputs = executeComputation(inputs);
            return writeOutputResults(outputDestination, outputs);
        } catch (Exception e) {
            return new JobResult(JobResult.Status.FAILURE, "Job failed: " + e.getMessage());
        }
    }

    @Override
    public JobResult configureJob(InputConfig inputSource, OutputConfig outputDestination) {
        return configureJob(inputSource, outputDestination, DelimiterConfig.defaultConfig());
    }

    private JobResult validateJobParameters(InputConfig inputSource, OutputConfig outputDestination) {
        if (inputSource == null) {
            return new JobResult(JobResult.Status.FAILURE, "Input source cannot be null");
        }
        if (outputDestination == null) {
            return new JobResult(JobResult.Status.FAILURE, "Output destination cannot be null");
        }
        return null;
    }

    private List<Integer> readInputData(InputConfig inputSource) {
        List<Integer> inputs = dataStorageAPI.readInputData(inputSource);
        return (inputs != null) ? inputs : Collections.emptyList();
    }

    private List<String> executeComputation(List<Integer> inputs) {
        List<String> results = new ArrayList<>();
        for (Integer inputNumber : inputs) {
            ComputationRequest request = new ComputationRequest(inputNumber);
            ComputationResult computationResult = computationAPI.countPrimesBelow(request);
            int primeCount = (computationResult != null) ? computationResult.getPrimeCount() : 0;
            results.add(String.valueOf(primeCount));
        }
        return results;
    }

    private JobResult writeOutputResults(OutputConfig outputDestination, List<String> outputs) {
        WriteResult writeResult = dataStorageAPI.writeOutputData(outputDestination, outputs);
        if (writeResult != null && writeResult.getStatus() == WriteResult.Status.FAILURE) {
            return new JobResult(JobResult.Status.FAILURE, writeResult.getMessage());
        }
        return new JobResult(JobResult.Status.SUCCESS, "Job completed successfully");
    }
}

