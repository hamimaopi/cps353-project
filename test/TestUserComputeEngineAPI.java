import api.ComputationAPI;
import api.ComputeJobRequest;
import api.ComputeJobResponse;
import api.ComputeJobStatus;
import api.DataStorageAPI;
import api.InputConfig;
import api.OutputConfig;
import api.UserComputeEngineAPI;
import api.UserComputeEngineAPIImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

public class TestUserComputeEngineAPI {

    @Test
    public void testComputeJobSmoke() {
        DataStorageAPI mockDataStorage = Mockito.mock(DataStorageAPI.class);
        ComputationAPI mockComputation = Mockito.mock(ComputationAPI.class);
        InputConfig mockInputConfig = Mockito.mock(InputConfig.class);
        OutputConfig mockOutputConfig = Mockito.mock(OutputConfig.class);

        Mockito.when(mockDataStorage.readInputData(mockInputConfig)).thenReturn(List.of(1, 10, 25));
        Mockito.when(mockComputation.countPrimesBelow(10)).thenReturn(4);

        UserComputeEngineAPI engine = new UserComputeEngineAPIImpl(mockDataStorage, mockComputation);
        ComputeJobRequest request = new ComputeJobRequest(mockInputConfig, mockOutputConfig);

        ComputeJobResponse response = engine.compute(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(ComputeJobStatus.SUCCESS, response.getStatus());
    }
}
