package api;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataStorageAPIImpl implements DataStorageAPI {

    public DataStorageAPIImpl() {
    }

    @Override
    public List<Integer> readInputData(InputConfig inputSource) {
        try {
            if (inputSource instanceof ListInputConfig listInput) {
                return listInput.getNumbers();
            }
            if (inputSource == null || inputSource.getSourceDetails() == null || inputSource.getSourceDetails().isBlank()) {
                return Collections.emptyList();
            }

            Path path = Paths.get(inputSource.getSourceDetails());
            if (!Files.exists(path) || !Files.isRegularFile(path)) {
                return Collections.emptyList();
            }

            String content = Files.readString(path);
            return parseIntegers(content);
        } catch (Exception e) {
            // Catches both checked exceptions (IOException) and unexpected runtime exceptions
            // (e.g., InvalidPathException, SecurityException) returning a safe sentinel value.
            return Collections.emptyList();
        }
    }

    @Override
    public WriteResult writeOutputData(OutputConfig outputDestination, List<String> outputData) {
        try {
            if (outputDestination == null) {
                return new WriteResult(WriteResult.Status.FAILURE, "Output destination cannot be null");
            }
            if (outputDestination.getDestinationDetails() == null || outputDestination.getDestinationDetails().isBlank()) {
                return new WriteResult(WriteResult.Status.FAILURE, "Output destination path cannot be null or empty");
            }
            if (outputData == null) {
                return new WriteResult(WriteResult.Status.FAILURE, "Output data cannot be null");
            }

            Path path = Paths.get(outputDestination.getDestinationDetails());
            Path parent = path.getParent();
            if (parent != null && !Files.isDirectory(parent)) {
                Files.createDirectories(parent);
            }

            String delimiter = ",";
            if (outputDestination instanceof FileOutputConfig fileOutput
                    && fileOutput.getDelimiter() != null
                    && !fileOutput.getDelimiter().isEmpty()) {
                delimiter = fileOutput.getDelimiter();
            }

            String content = String.join(delimiter, outputData);
            Files.writeString(path, content);
            return new WriteResult(WriteResult.Status.SUCCESS, "Output successfully written to " + path);
        } catch (Exception e) {
            // Catches both checked exceptions (IOException) and unexpected runtime exceptions,
            // returning an explicit error return status.
            String errorMsg = (e.getMessage() != null && !e.getMessage().isBlank()) ? e.getMessage() : e.getClass().getSimpleName();
            return new WriteResult(WriteResult.Status.FAILURE, "Failed to write output: " + errorMsg);
        }
    }

    private List<Integer> parseIntegers(String content) {
        if (content == null || content.isBlank()) {
            return Collections.emptyList();
        }

        List<Integer> integers = new ArrayList<>();
        String[] tokens = content.split("[,;\\s]+");
        for (String token : tokens) {
            String trimmedToken = token.trim();
            if (!trimmedToken.isEmpty()) {
                try {
                    integers.add(Integer.parseInt(trimmedToken));
                } catch (NumberFormatException e) {
                    // Skip malformed or non-integer tokens
                }
            }
        }
        return integers;
    }
}

