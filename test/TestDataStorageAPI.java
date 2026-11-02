import api.DataStorageAPI;
import api.DataStorageAPIImpl;
import api.InputConfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

public class TestDataStorageAPI {

    @Test
    public void testReadInputDataSmoke() {
        DataStorageAPI dataStorage = new DataStorageAPIImpl();
        InputConfig mockInputConfig = Mockito.mock(InputConfig.class);

        List<Integer> result = dataStorage.readInputData(mockInputConfig);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }
}
