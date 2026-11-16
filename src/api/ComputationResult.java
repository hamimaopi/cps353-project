package api;

public class ComputationResult {
    private final int primeCount;

    public ComputationResult(int primeCount) {
        if (primeCount < 0) {
            throw new IllegalArgumentException("Prime count cannot be negative");
        }
        this.primeCount = primeCount;
    }

    public int getPrimeCount() {
        return primeCount;
    }
}
