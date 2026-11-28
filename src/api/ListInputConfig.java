package api;

import java.util.Collections;
import java.util.List;

public class ListInputConfig implements InputConfig {
    private final List<Integer> numbers;

    public ListInputConfig(List<Integer> numbers) {
        if (numbers == null) {
            throw new IllegalArgumentException("Input numbers list cannot be null");
        }
        this.numbers = List.copyOf(numbers);
    }

    public List<Integer> getNumbers() {
        return Collections.unmodifiableList(numbers);
    }

    @Override
    public String getSourceDetails() {
        return "in-memory";
    }
}
