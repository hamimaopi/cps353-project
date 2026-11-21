package api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserComputeEngineAPIImpl implements UserComputeEngineAPI {

    private final DataStorageAPI dataStorageAPI;
    private final ComputationAPI computationAPI;

    public UserComputeEngineAPIImpl(DataStorageAPI dataStorageAPI, ComputationAPI computationAPI) {
        if (dataStorageAPI == null) {
            throw new IllegalArgumentException("DataStorageAPI cannot be null");
        }
        if (computationAPI == null) {
            throw new IllegalArgumentException("ComputationAPI cannot be null");
        }
        this.dataStorageAPI = dataStorageAPI;
        this.computationAPI = computationAPI;
    }

    @Override
    public JobResult configureJob(InputConfig inputSource, OutputConfig outputDestination, DelimiterConfig delimiterConfig) {
        try {
            JobResult validationResult = validateJobParameters(inputSource, outputDestination, delimiterConfig);
            if (validationResult != null) {
                return validationResult;
            }

            List<Integer> inputs = readInputData(inputSource);
            List<String> outputs = executeComputation(inputs);
            return writeOutputResults(outputDestination, outputs);
        } catch (Exception e) {
            // Translates both checked and unexpected runtime exceptions into an explicit error response
            String errorMsg = (e.getMessage() != null && !e.getMessage().isBlank()) ? e.getMessage() : e.getClass().getSimpleName();
            return new JobResult(JobResult.Status.FAILURE, "Job failed: " + errorMsg);
        }
    }

    @Override
    public JobResult configureJob(InputConfig inputSource, OutputConfig outputDestination) {
        // Delegates to configureJob(InputConfig, OutputConfig, DelimiterConfig) using default DelimiterConfig;
        // parameter validation for inputSource and outputDestination is performed in that method.
        try {
            return configureJob(inputSource, outputDestination, DelimiterConfig.defaultConfig());
        } catch (Exception e) {
            String errorMsg = (e.getMessage() != null && !e.getMessage().isBlank()) ? e.getMessage() : e.getClass().getSimpleName();
            return new JobResult(JobResult.Status.FAILURE, "Job failed: " + errorMsg);
        }
    }

    private JobResult validateJobParameters(InputConfig inputSource, OutputConfig outputDestination, DelimiterConfig delimiterConfig) {
        if (inputSource == null) {
            return new JobResult(JobResult.Status.FAILURE, "Input source cannot be null");
        }
        if (outputDestination == null) {
            return new JobResult(JobResult.Status.FAILURE, "Output destination cannot be null");
        }
        if (delimiterConfig == null) {
            return new JobResult(JobResult.Status.FAILURE, "Delimiter config cannot be null");
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

