package api.grpc;

import api.DataStorageAPI;
import api.FileInputConfig;
import api.FileOutputConfig;
import api.ListInputConfig;
import api.OutputConfig;
import api.WriteResult;
import api.grpc.proto.DataStorageServiceGrpc;
import api.grpc.proto.ReadInputRequest;
import api.grpc.proto.ReadInputResponse;
import api.grpc.proto.WriteOutputRequest;
import api.grpc.proto.WriteOutputResponse;
import io.grpc.stub.StreamObserver;

import java.util.List;

public class DataStorageServiceImpl extends DataStorageServiceGrpc.DataStorageServiceImplBase {

    private final DataStorageAPI dataStorageAPI;

    public DataStorageServiceImpl(DataStorageAPI dataStorageAPI) {
        if (dataStorageAPI == null) {
            throw new IllegalArgumentException("DataStorageAPI cannot be null");
        }
        this.dataStorageAPI = dataStorageAPI;
    }

    @Override
    public void readInput(ReadInputRequest request, StreamObserver<ReadInputResponse> responseObserver) {
        ReadInputResponse.Builder responseBuilder = ReadInputResponse.newBuilder();
        try {
            switch (request.getSourceCase()) {
                case FILE_PATH:
                    List<Integer> fileNumbers = dataStorageAPI.readInputData(
                            new FileInputConfig(request.getFilePath()));
                    responseBuilder.addAllNumbers(fileNumbers)
                            .setSuccess(true)
                            .setMessage("Read " + fileNumbers.size() + " numbers from file");
                    break;
                case IN_MEMORY_NUMBERS:
                    List<Integer> inlineNumbers = dataStorageAPI.readInputData(
                            new ListInputConfig(request.getInMemoryNumbers().getNumbersList()));
                    responseBuilder.addAllNumbers(inlineNumbers)
                            .setSuccess(true)
                            .setMessage("Read " + inlineNumbers.size() + " in-memory numbers");
                    break;
                case SOURCE_NOT_SET:
                default:
                    responseBuilder.setSuccess(false)
                            .setMessage("Input source not specified");
                    break;
            }
        } catch (Exception e) {
            String errorMsg = (e.getMessage() != null && !e.getMessage().isBlank())
                    ? e.getMessage()
                    : e.getClass().getSimpleName();
            responseBuilder.clearNumbers()
                    .setSuccess(false)
                    .setMessage("Failed to read input: " + errorMsg);
        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void writeOutput(WriteOutputRequest request, StreamObserver<WriteOutputResponse> responseObserver) {
        WriteOutputResponse.Builder responseBuilder = WriteOutputResponse.newBuilder();
        try {
            String delimiter = (request.getDelimiter() != null && !request.getDelimiter().isEmpty())
                    ? request.getDelimiter()
                    : null;
            OutputConfig outputConfig = new FileOutputConfig(request.getDestinationPath(), delimiter);
            WriteResult result = dataStorageAPI.writeOutputData(outputConfig, request.getOutputDataList());
            boolean success = result != null && result.getStatus() == WriteResult.Status.SUCCESS;
            String message = (result != null && result.getMessage() != null)
                    ? result.getMessage()
                    : "Write finished";
            responseBuilder.setSuccess(success).setMessage(message);
        } catch (Exception e) {
            String errorMsg = (e.getMessage() != null && !e.getMessage().isBlank())
                    ? e.getMessage()
                    : e.getClass().getSimpleName();
            responseBuilder.setSuccess(false)
                    .setMessage("Failed to write output: " + errorMsg);
        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
