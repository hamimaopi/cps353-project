package api;

import project.annotations.NetworkAPIPrototype;

public class UserComputeEnginePrototype {

    @NetworkAPIPrototype
    public void prototypeUserComputeEngine(UserComputeEngineAPI api) {
        InputConfig inputSource = new FileInputConfig("input.txt");
        OutputConfig outputDestination = new FileOutputConfig("output.txt");

        // 1. Configure job with explicit custom delimiters
        DelimiterConfig customDelimiters = new DelimiterConfig(";", ":");
        api.configureJob(inputSource, outputDestination, customDelimiters);

        // 2. Configure job with default delimiters
        api.configureJob(inputSource, outputDestination);
    }
}