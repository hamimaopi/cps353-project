package api;

public class DelimiterConfig {
    private final String itemDelimiter;
    private final String keyValueDelimiter;
    private final boolean isDefault;

    public DelimiterConfig(String itemDelimiter, String keyValueDelimiter) {
        if (itemDelimiter == null || keyValueDelimiter == null) {
            throw new IllegalArgumentException("Delimiters cannot be null");
        }
        this.itemDelimiter = itemDelimiter;
        this.keyValueDelimiter = keyValueDelimiter;
        this.isDefault = false;
    }

    private DelimiterConfig() {
        this.itemDelimiter = ";";
        this.keyValueDelimiter = ":";
        this.isDefault = true;
    }

    public static DelimiterConfig defaultConfig() {
        return new DelimiterConfig();
    }

    public String getItemDelimiter() {
        return itemDelimiter;
    }

    public String getKeyValueDelimiter() {
        return keyValueDelimiter;
    }

    public boolean isDefault() {
        return isDefault;
    }
}
