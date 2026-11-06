package api;

public class ComputationAPIImpl implements ComputationAPI {

    public ComputationAPIImpl() {
    }

    @Override
    public ComputationResult countPrimesBelow(ComputationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Computation request cannot be null");
        }
        int upperBound = request.getInputNumber();
        int primeCount = countPrimesLessThan(upperBound);
        return new ComputationResult(primeCount);
    }

    private int countPrimesLessThan(int upperBound) {
        if (upperBound <= 2) {
            return 0;
        }
        int count = 0;
        for (int candidate = 2; candidate < upperBound; candidate++) {
            if (isPrime(candidate)) {
                count++;
            }
        }
        return count;
    }

    private boolean isPrime(int candidate) {
        if (candidate < 2) {
            return false;
        }
        if (candidate == 2) {
            return true;
        }
        if (candidate % 2 == 0) {
            return false;
        }
        for (int divisor = 3; (long) divisor * divisor <= candidate; divisor += 2) {
            if (candidate % divisor == 0) {
                return false;
            }
        }
        return true;
    }
}
