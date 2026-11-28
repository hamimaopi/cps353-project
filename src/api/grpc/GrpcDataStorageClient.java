package api.grpc;

import api.DataStorageAPI;
import api.FileOutputConfig;
import api.InputConfig;
import api.ListInputConfig;
import api.OutputConfig;
import api.WriteResult;
import api.grpc.proto.DataStorageServiceGrpc;
import api.grpc.proto.InMemoryNumbers;
import api.grpc.proto.ReadInputRequest;
import api.grpc.proto.ReadInputResponse;
import api.grpc.proto.WriteOutputRequest;
import api.grpc.proto.WriteOutputResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GrpcDataStorageClient implements DataStorageAPI, AutoCloseable {

    private final ManagedChannel channel;
    private final DataStorageServiceGrpc.DataStorageServiceBlockingStub blockingStub;
    private final boolean ownsChannel;

    public GrpcDataStorageClient(String host, int port) {
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("Host cannot be null or blank");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 1 and 65535");
        }
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = DataStorageServiceGrpc.newBlockingStub(this.channel);
        this.ownsChannel = true;
    }

    public GrpcDataStorageClient(ManagedChannel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("ManagedChannel cannot be null");
        }
        this.channel = channel;
        this.blockingStub = DataStorageServiceGrpc.newBlockingStub(channel);
        this.ownsChannel = false;
    }

    @Override
    public List<Integer> readInputData(InputConfig inputSource) {
        try {
            if (inputSource == null) {
                return Collections.emptyList();
            }

            ReadInputRequest.Builder requestBuilder = ReadInputRequest.newBuilder();
            if (inputSource instanceof ListInputConfig listInput) {
                requestBuilder.setInMemoryNumbers(
                        InMemoryNumbers.newBuilder()
                                .addAllNumbers(listInput.getNumbers())
                                .build());
            } else {
                String sourceDetails = inputSource.getSourceDetails();
                if (sourceDetails == null || sourceDetails.isBlank()) {
                    return Collections.emptyList();
                }
                requestBuilder.setFilePath(sourceDetails);
            }

            ReadInputResponse response = blockingStub.readInput(requestBuilder.build());
            if (!response.getSuccess()) {
                return Collections.emptyList();
            }
            return response.getNumbersList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public WriteResult writeOutputData(OutputConfig outputDestination, List<String> outputData) {
        try {
            if (outputDestination == null) {
                return new WriteResult(WriteResult.Status.FAILURE, "Output destination cannot be null");
            }
            String destinationDetails = outputDestination.getDestinationDetails();
            if (destinationDetails == null || destinationDetails.isBlank()) {
                return new WriteResult(WriteResult.Status.FAILURE, "Output destination path cannot be null or empty");
            }
            if (outputData == null) {
                return new WriteResult(WriteResult.Status.FAILURE, "Output data cannot be null");
            }

            WriteOutputRequest.Builder requestBuilder = WriteOutputRequest.newBuilder()
                    .setDestinationPath(destinationDetails)
                    .addAllOutputData(outputData);

            if (outputDestination instanceof FileOutputConfig fileOutput && fileOutput.getDelimiter() != null) {
                requestBuilder.setDelimiter(fileOutput.getDelimiter());
            }

            WriteOutputResponse response = blockingStub.writeOutput(requestBuilder.build());
            WriteResult.Status status = response.getSuccess()
                    ? WriteResult.Status.SUCCESS
                    : WriteResult.Status.FAILURE;
            return new WriteResult(status, response.getMessage());
        } catch (Exception e) {
            String errorMsg = (e.getMessage() != null && !e.getMessage().isBlank())
                    ? e.getMessage()
                    : e.getClass().getSimpleName();
            return new WriteResult(WriteResult.Status.FAILURE, "gRPC write failed: " + errorMsg);
        }
    }

    @Override
    public void close() {
        if (ownsChannel && !channel.isShutdown()) {
            channel.shutdown();
            try {
                if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                    channel.shutdownNow();
                }
            } catch (InterruptedException e) {
                channel.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
