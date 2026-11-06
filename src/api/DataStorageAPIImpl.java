package api;

import java.io.IOException;
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
        if (inputSource == null || inputSource.getSourceDetails() == null) {
            return Collections.emptyList();
        }

        Path path = Paths.get(inputSource.getSourceDetails());
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            return Collections.emptyList();
        }

        try {
            String content = Files.readString(path);
            return parseIntegers(content);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public WriteResult writeOutputData(OutputConfig outputDestination, List<String> outputData) {
        if (outputDestination == null || outputDestination.getDestinationDetails() == null) {
            return new WriteResult(WriteResult.Status.FAILURE, "Output destination cannot be null");
        }

        Path path = Paths.get(outputDestination.getDestinationDetails());
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            List<String> dataToWrite = (outputData != null) ? outputData : Collections.emptyList();
            String content = String.join(",", dataToWrite);
            Files.writeString(path, content);
            return new WriteResult(WriteResult.Status.SUCCESS, "Output successfully written to " + path);
        } catch (IOException e) {
            return new WriteResult(WriteResult.Status.FAILURE, "Failed to write output: " + e.getMessage());
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

