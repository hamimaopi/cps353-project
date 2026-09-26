package api;

import project.annotations.NetworkAPIPrototype;

public class UserComputeEnginePrototype {

    @NetworkAPIPrototype
    public void prototypeUserComputeEngine(UserComputeEngineAPI api) {
        api.configureJob("input.txt", "output.txt", ',');
    }
}