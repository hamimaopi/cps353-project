package threadharness;

import api.DelimiterConfig;
import api.FileInputConfig;
import api.FileOutputConfig;
import api.UserComputeEngineAPI;

import java.io.File;

public class TestUser {

    // TODO 3: change the type of this variable to the name you're using for your
    // @NetworkAPI interface; also update the parameter passed to the constructor
    private final UserComputeEngineAPI coordinator;

    public TestUser(UserComputeEngineAPI coordinator) {
        this.coordinator = coordinator;
    }

    public void run(String outputPath) {
        char delimiter = ';';
        String inputPath = "test" + File.separatorChar + "testInputFile.test";

        // TODO 4: Call the appropriate method(s) on the coordinator to get it to
        // run the compute job specified by inputPath, outputPath, and delimiter
        FileInputConfig inputConfig = new FileInputConfig(inputPath);
        FileOutputConfig outputConfig = new FileOutputConfig(outputPath);
        DelimiterConfig delimiterConfig = new DelimiterConfig(String.valueOf(delimiter), ":");
        coordinator.configureJob(inputConfig, outputConfig, delimiterConfig);
    }
}
