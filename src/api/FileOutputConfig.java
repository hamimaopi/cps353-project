package api;

public class FileOutputConfig implements OutputConfig {
    private final String filePath;
    private final String delimiter;

    public FileOutputConfig(String filePath) {
        this(filePath, null);
    }

    public FileOutputConfig(String filePath, String delimiter) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        this.filePath = filePath;
        this.delimiter = delimiter;
    }

    @Override
    public String getDestinationDetails() {
        return filePath;
    }

    public String getDelimiter() {
        return delimiter;
    }
}
