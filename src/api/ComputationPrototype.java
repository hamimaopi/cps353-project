package api;

import project.annotations.ConceptualAPIPrototype;

public class ComputationPrototype {

    @ConceptualAPIPrototype
    public void prototypeComputation(ComputationAPI api) {
        int input = 20;
        int result = api.countPrimesBelow(input);
    }
}