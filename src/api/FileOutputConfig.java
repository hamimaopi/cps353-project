package api;

public class FileOutputConfig implements OutputConfig {
    private final String filePath;

    public FileOutputConfig(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        this.filePath = filePath;
    }

    @Override
    public String getDestinationDetails() {
        return filePath;
    }
}
