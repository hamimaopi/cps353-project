package api;

import project.annotations.ConceptualAPI;

@ConceptualAPI
public interface ComputationAPI {

    int countPrimesBelow(int input);
}