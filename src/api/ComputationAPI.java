package api;

import project.annotations.ConceptualAPI;

@ConceptualAPI
public interface ComputationAPI {

    ComputationResult countPrimesBelow(ComputationRequest request);
}