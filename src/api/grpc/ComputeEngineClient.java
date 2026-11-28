package api.grpc;

import api.grpc.proto.ComputeEngineServiceGrpc;
import api.grpc.proto.ComputeJobRequest;
import api.grpc.proto.ComputeJobResponse;
import api.grpc.proto.InlineNumbers;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ComputeEngineClient implements AutoCloseable {

    public static final String DEFAULT_HOST = "localhost";

    private final ManagedChannel channel;
    private final ComputeEngineServiceGrpc.ComputeEngineServiceBlockingStub blockingStub;
    private final boolean ownsChannel;

    public ComputeEngineClient(String host, int port) {
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("Host cannot be null or blank");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 1 and 65535");
        }
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = ComputeEngineServiceGrpc.newBlockingStub(this.channel);
        this.ownsChannel = true;
    }

    public ComputeEngineClient(ManagedChannel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("ManagedChannel cannot be null");
        }
        this.channel = channel;
        this.blockingStub = ComputeEngineServiceGrpc.newBlockingStub(channel);
        this.ownsChannel = false;
    }

    public ComputeJobResponse submitFileJob(String inputFilePath, String outputFilePath, String delimiter) {
        ComputeJobRequest.Builder builder = ComputeJobRequest.newBuilder()
                .setInputFilePath(inputFilePath != null ? inputFilePath : "")
                .setOutputFilePath(outputFilePath != null ? outputFilePath : "");
        if (delimiter != null && !delimiter.isEmpty()) {
            builder.setDelimiter(delimiter);
        }
        return executeRequest(builder.build());
    }

    public ComputeJobResponse submitInlineNumbersJob(
            List<Integer> numbers,
            String outputFilePath,
            String delimiter) {
        InlineNumbers inlineNumbers = InlineNumbers.newBuilder()
                .addAllNumbers(numbers != null ? numbers : List.of())
                .build();
        ComputeJobRequest.Builder builder = ComputeJobRequest.newBuilder()
                .setInlineNumbers(inlineNumbers)
                .setOutputFilePath(outputFilePath != null ? outputFilePath : "");
        if (delimiter != null && !delimiter.isEmpty()) {
            builder.setDelimiter(delimiter);
        }
        return executeRequest(builder.build());
    }

    private ComputeJobResponse executeRequest(ComputeJobRequest request) {
        try {
            return blockingStub.submitJob(request);
        } catch (Exception e) {
            String errorMsg = (e.getMessage() != null && !e.getMessage().isBlank())
                    ? e.getMessage()
                    : e.getClass().getSimpleName();
            return ComputeJobResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Failed to reach Compute Engine server: " + errorMsg)
                    .build();
        }
    }

    public static List<Integer> parseNumbers(String rawInput) {
        if (rawInput == null || rawInput.isBlank()) {
            throw new IllegalArgumentException("Please provide at least one integer.");
        }
        String[] tokens = rawInput.trim().split("[,;\\s]+");
        List<Integer> numbers = new ArrayList<>();
        for (String token : tokens) {
            if (!token.isEmpty()) {
                numbers.add(Integer.parseInt(token));
            }
        }
        if (numbers.isEmpty()) {
            throw new IllegalArgumentException("Please provide at least one integer.");
        }
        return numbers;
    }

    public ComputeJobResponse runInteractive(Scanner scanner, PrintStream out) {
        out.println("Compute Engine Client");
        out.println("Choose input type:");
        out.println("  1) Input file path");
        out.println("  2) Typed-in list of numbers");
        out.print("Selection (1 or 2): ");
        String choice = scanner.nextLine().trim();

        String inputFilePath = null;
        List<Integer> inlineNumbers = null;
        if ("1".equals(choice)) {
            out.print("Enter input file path: ");
            inputFilePath = scanner.nextLine().trim();
        } else if ("2".equals(choice)) {
            out.print("Enter numbers (comma or space separated): ");
            String rawNumbers = scanner.nextLine();
            inlineNumbers = parseNumbers(rawNumbers);
        } else {
            ComputeJobResponse invalidChoice = ComputeJobResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Invalid selection '" + choice + "'. Expected 1 or 2.")
                    .build();
            printResponse(invalidChoice, "", out);
            return invalidChoice;
        }

        out.print("Enter output file path: ");
        String outputFilePath = scanner.nextLine().trim();

        out.print("Enter output delimiter (press Enter for default ','): ");
        String delimiter = scanner.nextLine();

        ComputeJobResponse response = (inputFilePath != null)
                ? submitFileJob(inputFilePath, outputFilePath, delimiter)
                : submitInlineNumbersJob(inlineNumbers, outputFilePath, delimiter);
        printResponse(response, outputFilePath, out);
        return response;
    }

    public static void printResponse(ComputeJobResponse response, String outputFilePath, PrintStream out) {
        if (response.getSuccess()) {
            out.println("Job Status: SUCCESS");
            out.println("Message: " + response.getMessage());
            out.println("Output written to: " + outputFilePath);
            if (response.getResultsCount() > 0) {
                out.println("Results: " + String.join(", ", response.getResultsList()));
            }
        } else {
            out.println("Job Status: FAILURE");
            out.println("Message: " + response.getMessage());
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

    public static void main(String[] args) {
        String host = DEFAULT_HOST;
        int port = ComputeEngineServer.DEFAULT_PORT;
        String inputFile = null;
        String rawNumbers = null;
        String outputFile = null;
        String delimiter = "";

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--host":
                    host = requireArgValue(args, ++i, "--host");
                    break;
                case "--port":
                    port = Integer.parseInt(requireArgValue(args, ++i, "--port"));
                    break;
                case "--input-file":
                    inputFile = requireArgValue(args, ++i, "--input-file");
                    break;
                case "--numbers":
                    rawNumbers = requireArgValue(args, ++i, "--numbers");
                    break;
                case "--output-file":
                    outputFile = requireArgValue(args, ++i, "--output-file");
                    break;
                case "--delimiter":
                    delimiter = requireArgValue(args, ++i, "--delimiter");
                    break;
                default:
                    System.err.println("Unknown argument: " + args[i]);
                    System.exit(1);
                    return;
            }
        }

        try (ComputeEngineClient client = new ComputeEngineClient(host, port)) {
            ComputeJobResponse response;
            if (inputFile != null || rawNumbers != null || outputFile != null) {
                if (inputFile != null && rawNumbers != null) {
                    System.err.println("Specify either --input-file or --numbers, not both.");
                    System.exit(1);
                    return;
                }
                if (outputFile == null || outputFile.isBlank()) {
                    System.err.println("--output-file is required when running with CLI flags.");
                    System.exit(1);
                    return;
                }
                response = (inputFile != null)
                        ? client.submitFileJob(inputFile, outputFile, delimiter)
                        : client.submitInlineNumbersJob(parseNumbers(rawNumbers), outputFile, delimiter);
                printResponse(response, outputFile, System.out);
            } else {
                try (Scanner scanner = new Scanner(System.in)) {
                    response = client.runInteractive(scanner, System.out);
                }
            }

            if (!response.getSuccess()) {
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Job Status: FAILURE");
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static String requireArgValue(String[] args, int index, String flag) {
        if (index >= args.length) {
            throw new IllegalArgumentException("Missing value for " + flag);
        }
        return args[index];
    }
}
