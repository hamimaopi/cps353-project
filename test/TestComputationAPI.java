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

    @Test
    public void testCountPrimesBelowEdgeCases() {
        ComputationAPI computation = new ComputationAPIImpl();

        Assertions.assertEquals(0, computation.countPrimesBelow(new ComputationRequest(-5)).getPrimeCount());
        Assertions.assertEquals(0, computation.countPrimesBelow(new ComputationRequest(0)).getPrimeCount());
        Assertions.assertEquals(0, computation.countPrimesBelow(new ComputationRequest(1)).getPrimeCount());
        Assertions.assertEquals(0, computation.countPrimesBelow(new ComputationRequest(2)).getPrimeCount());
        Assertions.assertEquals(1, computation.countPrimesBelow(new ComputationRequest(3)).getPrimeCount());
    }

    @Test
    public void testCountPrimesBelowKnownValues() {
        ComputationAPI computation = new ComputationAPIImpl();

        Assertions.assertEquals(8, computation.countPrimesBelow(new ComputationRequest(20)).getPrimeCount());
        Assertions.assertEquals(9, computation.countPrimesBelow(new ComputationRequest(25)).getPrimeCount());
        Assertions.assertEquals(25, computation.countPrimesBelow(new ComputationRequest(100)).getPrimeCount());
    }

    @Test
    public void testCountPrimesBelowNullRequestThrows() {
        ComputationAPI computation = new ComputationAPIImpl();

        Assertions.assertThrows(IllegalArgumentException.class, () -> computation.countPrimesBelow(null));
    }
}

