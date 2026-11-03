import api.ComputationAPI;
import api.ComputationAPIImpl;
import api.ComputationRequest;
import api.ComputationResult;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestComputationAPI {

    @Test
    public void testCountPrimesBelowSmoke() {
        ComputationAPI computation = new ComputationAPIImpl();

        ComputationResult result = computation.countPrimesBelow(new ComputationRequest(10));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(4, result.getPrimeCount());
    }
}
