package api;

public class FileInputConfig implements InputConfig {
    private final String filePath;

    public FileInputConfig(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        this.filePath = filePath;
    }

    @Override
    public String getSourceDetails() {
        return filePath;
    }
}
