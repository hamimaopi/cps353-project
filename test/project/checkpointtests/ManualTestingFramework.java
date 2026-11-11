package project.checkpointtests;

import api.ComputationAPI;
import api.ComputationAPIImpl;
import api.DataStorageAPI;
import api.DataStorageAPIImpl;
import api.DelimiterConfig;
import api.FileInputConfig;
import api.FileOutputConfig;
import api.UserComputeEngineAPI;
import api.UserComputeEngineAPIImpl;

public class ManualTestingFramework {
    
    public static final String INPUT = "manualTestInput.txt";
    public static final String OUTPUT = "manualTestOutput.txt";

    public static void main(String[] args) {
        // TODO 1:
        // Instantiate a real (ie, class definition lives in the src/ folder) implementation 
        // of all 3 APIs
        DataStorageAPI dataStorage = new DataStorageAPIImpl();
        ComputationAPI computation = new ComputationAPIImpl();
        UserComputeEngineAPI computeEngine = new UserComputeEngineAPIImpl(dataStorage, computation);

        // TODO 2:
        // Run a computation with an input file of <root project dir>/manualTestInput.txt
        // and an output of <root project dir>/manualTestOutput.txt, with a delimiter of ',' 
        FileInputConfig inputConfig = new FileInputConfig(INPUT);
        FileOutputConfig outputConfig = new FileOutputConfig(OUTPUT);
        DelimiterConfig delimiterConfig = new DelimiterConfig(",", ":");

        computeEngine.configureJob(inputConfig, outputConfig, delimiterConfig);
    }
}

