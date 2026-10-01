package api;

public class ComputationRequest {
    private final int inputNumber;

    public ComputationRequest(int inputNumber) {
        this.inputNumber = inputNumber;
    }

    public int getInputNumber() {
        return inputNumber;
    }
}
