package api;

import project.annotations.NetworkAPI;

@NetworkAPI
public interface UserComputeEngineAPI {
    void configureJob(
            String inputSource,
            String outputDestination,
            char delimiter
    );
}