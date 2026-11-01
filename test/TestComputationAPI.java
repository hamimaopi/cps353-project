import api.ComputationAPI;
import api.ComputationAPIImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestComputationAPI {

    @Test
    public void testCountPrimesBelowSmoke() {
        ComputationAPI computation = new ComputationAPIImpl();

        int result = computation.countPrimesBelow(10);

        Assertions.assertEquals(4, result);
    }
}
