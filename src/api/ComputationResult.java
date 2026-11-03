package api;

public class ComputationResult {
    private final int primeCount;

    public ComputationResult(int primeCount) {
        this.primeCount = primeCount;
    }

    public int getPrimeCount() {
        return primeCount;
    }
}
