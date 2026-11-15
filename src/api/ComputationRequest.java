package api;

public class ComputationRequest {
    private final int inputNumber;

    // Parameter inputNumber does not require validation because all integer values
    // (including negatives and zero) are valid bounds for prime counting.
    public ComputationRequest(int inputNumber) {
        this.inputNumber = inputNumber;
    }

    public int getInputNumber() {
        return inputNumber;
    }
}
