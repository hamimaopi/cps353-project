import api.ComputationAPI;
import api.ComputationAPIImpl;
import api.ComputeJobRequest;
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

        ComputeJobRequest request = new ComputeJobRequest(inputConfig, outputConfig);
        realEngine.compute(request);

        Assertions.assertEquals(List.of("0", "4", "9"), outputConfig.getOutputs());
    }
}
