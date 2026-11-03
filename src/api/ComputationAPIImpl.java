package api;

public class ComputationAPIImpl implements ComputationAPI {

    public ComputationAPIImpl() {
    }

    @Override
    public ComputationResult countPrimesBelow(ComputationRequest request) {
        return new ComputationResult(0);
    }
}
