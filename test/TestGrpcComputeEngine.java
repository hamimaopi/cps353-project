import api.UserComputeEngineAPI;
import api.grpc.ComputeEngineServer;
import api.grpc.DataStorageServer;
import api.grpc.proto.ComputeEngineServiceGrpc;
import api.grpc.proto.ComputeJobRequest;
import api.grpc.proto.ComputeJobResponse;
import api.grpc.proto.InlineNumbers;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestGrpcComputeEngine {

    @Test
    public void testComputeEngineWithFileInputAndDefaultDelimiter() throws Exception {
        DataStorageServer dataStorageServer = new DataStorageServer(0);
        dataStorageServer.start();
        ComputeEngineServer computeServer = new ComputeEngineServer(
                0,
                "localhost",
                dataStorageServer.getPort());
        computeServer.start();

        Path inputFile = Files.createTempFile("compute_grpc_in", ".txt");
        Path outputFile = Files.createTempFile("compute_grpc_out", ".txt");
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", computeServer.getPort())
                .usePlaintext()
                .build();

        try {
            Files.writeString(inputFile, "1,15,10,5,2,3,8");
            ComputeEngineServiceGrpc.ComputeEngineServiceBlockingStub stub =
                    ComputeEngineServiceGrpc.newBlockingStub(channel);

            ComputeJobRequest request = ComputeJobRequest.newBuilder()
                    .setInputFilePath(inputFile.toString())
                    .setOutputFilePath(outputFile.toString())
                    .build();

            ComputeJobResponse response = stub.submitJob(request);

            Assertions.assertTrue(response.getSuccess());
            Assertions.assertEquals(List.of("0", "6", "4", "2", "0", "1", "4"), response.getResultsList());
            Assertions.assertEquals("0,6,4,2,0,1,4", Files.readString(outputFile));
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
            computeServer.stop();
            dataStorageServer.stop();
            Files.deleteIfExists(inputFile);
            Files.deleteIfExists(outputFile);
        }
    }

    @Test
    public void testComputeEngineWithInlineNumbersAndCustomDelimiter() throws Exception {
        DataStorageServer dataStorageServer = new DataStorageServer(0);
        dataStorageServer.start();
        ComputeEngineServer computeServer = new ComputeEngineServer(
                0,
                "localhost",
                dataStorageServer.getPort());
        computeServer.start();

        Path outputFile = Files.createTempFile("compute_grpc_inline_out", ".txt");
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", computeServer.getPort())
                .usePlaintext()
                .build();

        try {
            ComputeEngineServiceGrpc.ComputeEngineServiceBlockingStub stub =
                    ComputeEngineServiceGrpc.newBlockingStub(channel);

            ComputeJobRequest request = ComputeJobRequest.newBuilder()
                    .setInlineNumbers(InlineNumbers.newBuilder().addAllNumbers(List.of(10, 20, 25)).build())
                    .setOutputFilePath(outputFile.toString())
                    .setDelimiter(";")
                    .build();

            ComputeJobResponse response = stub.submitJob(request);

            Assertions.assertTrue(response.getSuccess());
            Assertions.assertEquals(List.of("4", "8", "9"), response.getResultsList());
            Assertions.assertEquals("4;8;9", Files.readString(outputFile));
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
            computeServer.stop();
            dataStorageServer.stop();
            Files.deleteIfExists(outputFile);
        }
    }

    @Test
    public void testComputeEngineValidationAndExceptionBoundary() throws Exception {
        UserComputeEngineAPI failingEngine = new UserComputeEngineAPI() {
            @Override
            public api.JobResult configureJob(
                    api.InputConfig inputSource,
                    api.OutputConfig outputDestination,
                    api.DelimiterConfig delimiterConfig) {
                throw new RuntimeException("Simulated engine crash");
            }

            @Override
            public api.JobResult configureJob(api.InputConfig inputSource, api.OutputConfig outputDestination) {
                throw new RuntimeException("Simulated engine crash");
            }
        };

        ComputeEngineServer computeServer = new ComputeEngineServer(0, failingEngine);
        computeServer.start();
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", computeServer.getPort())
                .usePlaintext()
                .build();

        try {
            ComputeEngineServiceGrpc.ComputeEngineServiceBlockingStub stub =
                    ComputeEngineServiceGrpc.newBlockingStub(channel);

            ComputeJobResponse missingOutput = stub.submitJob(
                    ComputeJobRequest.newBuilder()
                            .setInlineNumbers(InlineNumbers.newBuilder().addNumbers(10).build())
                            .build());
            Assertions.assertFalse(missingOutput.getSuccess());
            Assertions.assertTrue(missingOutput.getMessage().toLowerCase().contains("output"));

            ComputeJobResponse missingInput = stub.submitJob(
                    ComputeJobRequest.newBuilder()
                            .setOutputFilePath("out.txt")
                            .build());
            Assertions.assertFalse(missingInput.getSuccess());
            Assertions.assertTrue(missingInput.getMessage().toLowerCase().contains("input"));

            ComputeJobResponse emptyInline = stub.submitJob(
                    ComputeJobRequest.newBuilder()
                            .setInlineNumbers(InlineNumbers.newBuilder().build())
                            .setOutputFilePath("out.txt")
                            .build());
            Assertions.assertFalse(emptyInline.getSuccess());

            ComputeJobResponse crashResponse = stub.submitJob(
                    ComputeJobRequest.newBuilder()
                            .setInlineNumbers(InlineNumbers.newBuilder().addNumbers(10).build())
                            .setOutputFilePath("out.txt")
                            .build());
            Assertions.assertFalse(crashResponse.getSuccess());
            Assertions.assertTrue(crashResponse.getMessage().contains("Simulated engine crash"));
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
            computeServer.stop();
        }
    }
}
