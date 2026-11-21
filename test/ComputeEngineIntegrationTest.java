import api.ComputationAPI;
import api.ComputationAPIImpl;
import api.DataStorageAPI;
import api.UserComputeEngineAPI;
import api.UserComputeEngineAPIImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ComputeEngineIntegrationTest {

    @Test
    public void testComputeEngineIntegration() {
        DataStorageAPI testDataStore = new InMemoryDataStorage();
        ComputationAPI realComputation = new ComputationAPIImpl();
        UserComputeEngineAPI realEngine = new UserComputeEngineAPIImpl(testDataStore, realComputation);

        InMemoryInputConfig inputConfig = new InMemoryInputConfig(List.of(1, 10, 25));
        InMemoryOutputConfig outputConfig = new InMemoryOutputConfig();

        realEngine.configureJob(inputConfig, outputConfig);

        Assertions.assertEquals(List.of("0", "4", "9"), outputConfig.getOutputs());
    }

    @Test
    public void testExceptionHandlingWhenDataStorageReadThrows() {
        DataStorageAPI failingStorage = new DataStorageAPI() {
            @Override
            public List<Integer> readInputData(api.InputConfig inputSource) {
                throw new RuntimeException("Simulated disk read failure");
            }

            @Override
            public api.WriteResult writeOutputData(api.OutputConfig outputDestination, List<String> outputData) {
                return new api.WriteResult(api.WriteResult.Status.SUCCESS, "Success");
            }
        };

        ComputationAPI realComputation = new ComputationAPIImpl();
        UserComputeEngineAPI realEngine = new UserComputeEngineAPIImpl(failingStorage, realComputation);

        InMemoryInputConfig inputConfig = new InMemoryInputConfig(List.of(10));
        InMemoryOutputConfig outputConfig = new InMemoryOutputConfig();

        api.JobResult result = Assertions.assertDoesNotThrow(() -> realEngine.configureJob(inputConfig, outputConfig));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(api.JobResult.Status.FAILURE, result.getStatus());
        Assertions.assertTrue(result.getMessage().contains("Simulated disk read failure"));
    }

    @Test
    public void testExceptionHandlingWhenComputationThrows() {
        DataStorageAPI testDataStore = new InMemoryDataStorage();
        ComputationAPI failingComputation = new ComputationAPI() {
            @Override
            public api.ComputationResult countPrimesBelow(api.ComputationRequest request) {
                throw new RuntimeException("Simulated computation arithmetic overflow");
            }
        };

        UserComputeEngineAPI realEngine = new UserComputeEngineAPIImpl(testDataStore, failingComputation);

        InMemoryInputConfig inputConfig = new InMemoryInputConfig(List.of(10));
        InMemoryOutputConfig outputConfig = new InMemoryOutputConfig();

        api.JobResult result = Assertions.assertDoesNotThrow(() -> realEngine.configureJob(inputConfig, outputConfig));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(api.JobResult.Status.FAILURE, result.getStatus());
        Assertions.assertTrue(result.getMessage().contains("Simulated computation arithmetic overflow"));
    }

    @Test
    public void testExceptionHandlingWhenDataStorageWriteThrows() {
        DataStorageAPI failingStorage = new DataStorageAPI() {
            @Override
            public List<Integer> readInputData(api.InputConfig inputSource) {
                return List.of(10);
            }

            @Override
            public api.WriteResult writeOutputData(api.OutputConfig outputDestination, List<String> outputData) {
                throw new RuntimeException("Simulated disk write failure");
            }
        };

        ComputationAPI realComputation = new ComputationAPIImpl();
        UserComputeEngineAPI realEngine = new UserComputeEngineAPIImpl(failingStorage, realComputation);

        InMemoryInputConfig inputConfig = new InMemoryInputConfig(List.of(10));
        InMemoryOutputConfig outputConfig = new InMemoryOutputConfig();

        api.JobResult result = Assertions.assertDoesNotThrow(() -> realEngine.configureJob(inputConfig, outputConfig));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(api.JobResult.Status.FAILURE, result.getStatus());
        Assertions.assertTrue(result.getMessage().contains("Simulated disk write failure"));
    }
}
