import api.ComputationAPIImpl;
import api.DelimiterConfig;
import api.FileInputConfig;
import api.FileOutputConfig;
import api.JobResult;
import api.ListInputConfig;
import api.MultiThreadedUserComputeEngineAPIImpl;
import api.UserComputeEngineAPI;
import api.WriteResult;
import api.grpc.DataStorageServer;
import api.grpc.GrpcDataStorageClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TestGrpcDataStorage {

    @Test
    public void testGrpcReadFileAndWriteWithDefaultAndCustomDelimiter() throws Exception {
        DataStorageServer server = new DataStorageServer(0);
        server.start();
        Path inputFile = Files.createTempFile("grpc_ds_in", ".txt");
        Path outputFileDefault = Files.createTempFile("grpc_ds_out_default", ".txt");
        Path outputFileCustom = Files.createTempFile("grpc_ds_out_custom", ".txt");

        try (GrpcDataStorageClient client = new GrpcDataStorageClient("localhost", server.getPort())) {
            Files.writeString(inputFile, "1,10,15");

            List<Integer> numbers = client.readInputData(new FileInputConfig(inputFile.toString()));
            Assertions.assertEquals(List.of(1, 10, 15), numbers);

            WriteResult defaultWrite = client.writeOutputData(
                    new FileOutputConfig(outputFileDefault.toString()),
                    List.of("0", "4", "6"));
            Assertions.assertEquals(WriteResult.Status.SUCCESS, defaultWrite.getStatus());
            Assertions.assertEquals("0,4,6", Files.readString(outputFileDefault));

            WriteResult customWrite = client.writeOutputData(
                    new FileOutputConfig(outputFileCustom.toString(), ";"),
                    List.of("0", "4", "6"));
            Assertions.assertEquals(WriteResult.Status.SUCCESS, customWrite.getStatus());
            Assertions.assertEquals("0;4;6", Files.readString(outputFileCustom));
        } finally {
            server.stop();
            Files.deleteIfExists(inputFile);
            Files.deleteIfExists(outputFileDefault);
            Files.deleteIfExists(outputFileCustom);
        }
    }

    @Test
    public void testGrpcReadInMemoryListAndComputeEngineEndToEnd() throws Exception {
        DataStorageServer server = new DataStorageServer(0);
        server.start();
        Path outputFile = Files.createTempFile("grpc_compute_out", ".txt");

        try (GrpcDataStorageClient client = new GrpcDataStorageClient("localhost", server.getPort())) {
            List<Integer> inlineNumbers = client.readInputData(new ListInputConfig(List.of(1, 5, 10)));
            Assertions.assertEquals(List.of(1, 5, 10), inlineNumbers);

            UserComputeEngineAPI engine = new MultiThreadedUserComputeEngineAPIImpl(client, new ComputationAPIImpl());
            JobResult result = engine.configureJob(
                    new ListInputConfig(List.of(1, 5, 10)),
                    new FileOutputConfig(outputFile.toString()),
                    new DelimiterConfig("|", ":"));

            Assertions.assertEquals(JobResult.Status.SUCCESS, result.getStatus());
            Assertions.assertEquals("0|2|4", Files.readString(outputFile));
        } finally {
            server.stop();
            Files.deleteIfExists(outputFile);
        }
    }

    @Test
    public void testGrpcClientHandlesInvalidInputsAndUnreachableServer() throws Exception {
        DataStorageServer server = new DataStorageServer(0);
        server.start();
        int closedPort = server.getPort();
        server.stop();

        try (GrpcDataStorageClient client = new GrpcDataStorageClient("localhost", closedPort)) {
            Assertions.assertTrue(client.readInputData(null).isEmpty());
            Assertions.assertTrue(client.readInputData(new FileInputConfig("missing_file.txt")).isEmpty());

            WriteResult nullResult = client.writeOutputData(null, List.of("1"));
            Assertions.assertEquals(WriteResult.Status.FAILURE, nullResult.getStatus());

            WriteResult unreachableWrite = client.writeOutputData(
                    new FileOutputConfig("unreachable_out.txt"),
                    List.of("1"));
            Assertions.assertEquals(WriteResult.Status.FAILURE, unreachableWrite.getStatus());
        }
    }
}
