package api.grpc;

import api.DelimiterConfig;
import api.FileInputConfig;
import api.FileOutputConfig;
import api.InputConfig;
import api.JobResult;
import api.ListInputConfig;
import api.OutputConfig;
import api.UserComputeEngineAPI;
import api.grpc.proto.ComputeEngineServiceGrpc;
import api.grpc.proto.ComputeJobRequest;
import api.grpc.proto.ComputeJobResponse;
import io.grpc.stub.StreamObserver;

import java.util.List;

public class ComputeEngineServiceImpl extends ComputeEngineServiceGrpc.ComputeEngineServiceImplBase {

    private final UserComputeEngineAPI computeEngineAPI;

    public ComputeEngineServiceImpl(UserComputeEngineAPI computeEngineAPI) {
        if (computeEngineAPI == null) {
            throw new IllegalArgumentException("UserComputeEngineAPI cannot be null");
        }
        this.computeEngineAPI = computeEngineAPI;
    }

    @Override
    public void submitJob(ComputeJobRequest request, StreamObserver<ComputeJobResponse> responseObserver) {
        ComputeJobResponse.Builder responseBuilder = ComputeJobResponse.newBuilder();
        try {
            if (request.getOutputFilePath() == null || request.getOutputFilePath().isBlank()) {
                responseBuilder.setSuccess(false)
                        .setMessage("Output file path cannot be blank");
                responseObserver.onNext(responseBuilder.build());
                responseObserver.onCompleted();
                return;
            }

            InputConfig inputConfig = buildInputConfig(request, responseBuilder);
            if (inputConfig == null) {
                responseObserver.onNext(responseBuilder.build());
                responseObserver.onCompleted();
                return;
            }

            String customDelimiter = request.getDelimiter();
            boolean hasCustomDelimiter = customDelimiter != null && !customDelimiter.isEmpty();
            OutputConfig outputConfig = hasCustomDelimiter
                    ? new FileOutputConfig(request.getOutputFilePath(), customDelimiter)
                    : new FileOutputConfig(request.getOutputFilePath());
            DelimiterConfig delimiterConfig = hasCustomDelimiter
                    ? new DelimiterConfig(customDelimiter, ":")
                    : DelimiterConfig.defaultConfig();

            JobResult jobResult = computeEngineAPI.configureJob(inputConfig, outputConfig, delimiterConfig);
            boolean success = jobResult != null && jobResult.getStatus() == JobResult.Status.SUCCESS;
            String message = (jobResult != null && jobResult.getMessage() != null)
                    ? jobResult.getMessage()
                    : "Job finished";

            responseBuilder.setSuccess(success).setMessage(message);
            if (success && jobResult.getResults() != null) {
                responseBuilder.addAllResults(jobResult.getResults());
            }
        } catch (Exception e) {
            String errorMsg = (e.getMessage() != null && !e.getMessage().isBlank())
                    ? e.getMessage()
                    : e.getClass().getSimpleName();
            responseBuilder.clearResults()
                    .setSuccess(false)
                    .setMessage("Compute job failed: " + errorMsg);
        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    private InputConfig buildInputConfig(
            ComputeJobRequest request,
            ComputeJobResponse.Builder responseBuilder) {
        switch (request.getInputSourceCase()) {
            case INPUT_FILE_PATH:
                String inputPath = request.getInputFilePath();
                if (inputPath == null || inputPath.isBlank()) {
                    responseBuilder.setSuccess(false)
                            .setMessage("Input file path cannot be blank");
                    return null;
                }
                return new FileInputConfig(inputPath);
            case INLINE_NUMBERS:
                List<Integer> numbers = request.getInlineNumbers().getNumbersList();
                if (numbers.isEmpty()) {
                    responseBuilder.setSuccess(false)
                            .setMessage("Inline numbers list cannot be empty");
                    return null;
                }
                return new ListInputConfig(numbers);
            case INPUTSOURCE_NOT_SET:
            default:
                responseBuilder.setSuccess(false)
                        .setMessage("Input source must be specified");
                return null;
        }
    }
}
