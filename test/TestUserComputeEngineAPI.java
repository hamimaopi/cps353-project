import api.ComputationAPI;
import api.DataStorageAPI;
import api.InputConfig;
import api.JobResult;
import api.OutputConfig;
import api.UserComputeEngineAPI;
import api.UserComputeEngineAPIImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TestUserComputeEngineAPI {

    @Test
    public void testComputeJobSmoke() {
        DataStorageAPI mockDataStorage = Mockito.mock(DataStorageAPI.class);
        ComputationAPI mockComputation = Mockito.mock(ComputationAPI.class);
        InputConfig mockInputConfig = Mockito.mock(InputConfig.class);
        OutputConfig mockOutputConfig = Mockito.mock(OutputConfig.class);

        UserComputeEngineAPI engine = new UserComputeEngineAPIImpl(mockDataStorage, mockComputation);
        JobResult response = engine.configureJob(mockInputConfig, mockOutputConfig);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(JobResult.Status.SUCCESS, response.getStatus());
    }
}
