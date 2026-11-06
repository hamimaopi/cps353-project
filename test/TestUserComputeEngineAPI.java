import api.ComputationAPI;
import api.ComputationRequest;
import api.ComputationResult;
import api.DataStorageAPI;
import api.DelimiterConfig;
import api.InputConfig;
import api.JobResult;
import api.OutputConfig;
import api.UserComputeEngineAPI;
import api.UserComputeEngineAPIImpl;
import api.WriteResult;

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

        UserComputeEngineAPI engine = new UserComputeEngineAPIImpl(mockDataStorage, mockComputation);
        JobResult response = engine.configureJob(mockInputConfig, mockOutputConfig);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(JobResult.Status.SUCCESS, response.getStatus());
    }

    @Test
    public void testComputeJobOrchestrationWithMockData() {
        DataStorageAPI mockDataStorage = Mockito.mock(DataStorageAPI.class);
        ComputationAPI mockComputation = Mockito.mock(ComputationAPI.class);
        InputConfig mockInputConfig = Mockito.mock(InputConfig.class);
        OutputConfig mockOutputConfig = Mockito.mock(OutputConfig.class);

        Mockito.when(mockDataStorage.readInputData(mockInputConfig)).thenReturn(List.of(10, 20));
        Mockito.when(mockComputation.countPrimesBelow(Mockito.any(ComputationRequest.class)))
                .thenAnswer(invocation -> {
                    ComputationRequest req = invocation.getArgument(0);
                    return req.getInputNumber() == 10 ? new ComputationResult(4) : new ComputationResult(8);
                });
        Mockito.when(mockDataStorage.writeOutputData(Mockito.eq(mockOutputConfig), Mockito.anyList()))
                .thenReturn(new WriteResult(WriteResult.Status.SUCCESS, "Written"));

        UserComputeEngineAPI engine = new UserComputeEngineAPIImpl(mockDataStorage, mockComputation);
        JobResult response = engine.configureJob(mockInputConfig, mockOutputConfig);

        Assertions.assertEquals(JobResult.Status.SUCCESS, response.getStatus());
        Mockito.verify(mockDataStorage).writeOutputData(mockOutputConfig, List.of("4", "8"));
    }

    @Test
    public void testComputeJobWithCustomDelimiterConfig() {
        DataStorageAPI mockDataStorage = Mockito.mock(DataStorageAPI.class);
        ComputationAPI mockComputation = Mockito.mock(ComputationAPI.class);
        InputConfig mockInputConfig = Mockito.mock(InputConfig.class);
        OutputConfig mockOutputConfig = Mockito.mock(OutputConfig.class);
        DelimiterConfig delimiterConfig = new DelimiterConfig(",", ":");

        UserComputeEngineAPI engine = new UserComputeEngineAPIImpl(mockDataStorage, mockComputation);
        JobResult response = engine.configureJob(mockInputConfig, mockOutputConfig, delimiterConfig);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(JobResult.Status.SUCCESS, response.getStatus());
    }

    @Test
    public void testComputeJobNullInputsReturnFailure() {
        DataStorageAPI mockDataStorage = Mockito.mock(DataStorageAPI.class);
        ComputationAPI mockComputation = Mockito.mock(ComputationAPI.class);
        InputConfig mockInputConfig = Mockito.mock(InputConfig.class);
        OutputConfig mockOutputConfig = Mockito.mock(OutputConfig.class);

        UserComputeEngineAPI engine = new UserComputeEngineAPIImpl(mockDataStorage, mockComputation);

        JobResult nullInputResponse = engine.configureJob(null, mockOutputConfig);
        Assertions.assertEquals(JobResult.Status.FAILURE, nullInputResponse.getStatus());

        JobResult nullOutputResponse = engine.configureJob(mockInputConfig, null);
        Assertions.assertEquals(JobResult.Status.FAILURE, nullOutputResponse.getStatus());
    }

    @Test
    public void testComputeJobWriteFailurePropagated() {
        DataStorageAPI mockDataStorage = Mockito.mock(DataStorageAPI.class);
        ComputationAPI mockComputation = Mockito.mock(ComputationAPI.class);
        InputConfig mockInputConfig = Mockito.mock(InputConfig.class);
        OutputConfig mockOutputConfig = Mockito.mock(OutputConfig.class);

        Mockito.when(mockDataStorage.writeOutputData(Mockito.any(), Mockito.anyList()))
                .thenReturn(new WriteResult(WriteResult.Status.FAILURE, "Disk full"));

        UserComputeEngineAPI engine = new UserComputeEngineAPIImpl(mockDataStorage, mockComputation);
        JobResult response = engine.configureJob(mockInputConfig, mockOutputConfig);

        Assertions.assertEquals(JobResult.Status.FAILURE, response.getStatus());
        Assertions.assertTrue(response.getMessage().contains("Disk full"));
    }
}

