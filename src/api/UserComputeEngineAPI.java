package api;

import project.annotations.NetworkAPI;

@NetworkAPI
public interface UserComputeEngineAPI {

    // Method allowing custom delimiters
    JobResult configureJob(InputConfig inputSource, OutputConfig outputDestination, DelimiterConfig delimiterConfig);

    // Method allowing default delimiters
    JobResult configureJob(InputConfig inputSource, OutputConfig outputDestination);
}