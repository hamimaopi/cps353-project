package api;

import project.annotations.ConceptualAPIPrototype;

public class ComputationPrototype {

    @ConceptualAPIPrototype
    public void prototypeComputation(ComputationAPI api) {
        ComputationRequest request = new ComputationRequest(20);
        ComputationResult result = api.countPrimesBelow(request);
    }
}