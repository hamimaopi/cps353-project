import api.grpc.ComputeEngineClient;
import api.grpc.ComputeEngineServer;
import api.grpc.DataStorageServer;
import api.grpc.proto.ComputeJobResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class TestGrpcEndToEnd {

    @Test
    public void testEndToEndThreeProcessesWithFileAndInlineInputs() throws Exception {
        DataStorageServer dataStorageServer = new DataStorageServer(0);
        dataStorageServer.start();
        ComputeEngineServer computeServer = new ComputeEngineServer(
                0,
                "localhost",
                dataStorageServer.getPort());
        computeServer.start();

        Path inputFile = Files.createTempFile("e2e_input", ".txt");
        Path fileOutput = Files.createTempFile("e2e_file_out", ".txt");
        Path inlineOutput = Files.createTempFile("e2e_inline_out", ".txt");

        try (ComputeEngineClient client = new ComputeEngineClient("localhost", computeServer.getPort())) {
            Files.writeString(inputFile, "1,15,10,5,2,3,8");

            ComputeJobResponse fileResponse = client.submitFileJob(
                    inputFile.toString(),
                    fileOutput.toString(),
                    "");
            Assertions.assertTrue(fileResponse.getSuccess());
            Assertions.assertEquals(List.of("0", "6", "4", "2", "0", "1", "4"), fileResponse.getResultsList());
            Assertions.assertEquals("0,6,4,2,0,1,4", Files.readString(fileOutput));

            ComputeJobResponse inlineResponse = client.submitInlineNumbersJob(
                    List.of(10, 20, 100),
                    inlineOutput.toString(),
                    ";");
            Assertions.assertTrue(inlineResponse.getSuccess());
            Assertions.assertEquals(List.of("4", "8", "25"), inlineResponse.getResultsList());
            Assertions.assertEquals("4;8;25", Files.readString(inlineOutput));
        } finally {
            computeServer.stop();
            dataStorageServer.stop();
            Files.deleteIfExists(inputFile);
            Files.deleteIfExists(fileOutput);
            Files.deleteIfExists(inlineOutput);
        }
    }

    @Test
    public void testInteractiveClientPromptsForInlineNumbersAndCustomDelimiter() throws Exception {
        DataStorageServer dataStorageServer = new DataStorageServer(0);
        dataStorageServer.start();
        ComputeEngineServer computeServer = new ComputeEngineServer(
                0,
                "localhost",
                dataStorageServer.getPort());
        computeServer.start();

        Path outputFile = Files.createTempFile("e2e_interactive_out", ".txt");
        String simulatedUserInput = String.join("\n",
                "2",
                "10, 20, 5",
                outputFile.toString(),
                "|") + "\n";

        ByteArrayInputStream inStream = new ByteArrayInputStream(
                simulatedUserInput.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();

        try (ComputeEngineClient client = new ComputeEngineClient("localhost", computeServer.getPort());
             Scanner scanner = new Scanner(inStream);
             PrintStream outStream = new PrintStream(outBytes, true, StandardCharsets.UTF_8)) {
            ComputeJobResponse response = client.runInteractive(scanner, outStream);

            Assertions.assertTrue(response.getSuccess());
            Assertions.assertEquals("4|8|2", Files.readString(outputFile));
            String consoleOutput = outBytes.toString(StandardCharsets.UTF_8);
            Assertions.assertTrue(consoleOutput.contains("Job Status: SUCCESS"));
            Assertions.assertTrue(consoleOutput.contains("Results: 4, 8, 2"));
        } finally {
            computeServer.stop();
            dataStorageServer.stop();
            Files.deleteIfExists(outputFile);
        }
    }

    @Test
    public void testClientReportsFailureWhenServerUnreachable() throws Exception {
        ComputeEngineServer computeServer = new ComputeEngineServer(0, "localhost", 59999);
        computeServer.start();
        int closedPort = computeServer.getPort();
        computeServer.stop();

        try (ComputeEngineClient client = new ComputeEngineClient("localhost", closedPort)) {
            ComputeJobResponse response = client.submitInlineNumbersJob(
                    List.of(10),
                    "unused_out.txt",
                    ",");
            Assertions.assertFalse(response.getSuccess());
            Assertions.assertTrue(response.getMessage().contains("Failed to reach Compute Engine server"));
        }
    }
}
