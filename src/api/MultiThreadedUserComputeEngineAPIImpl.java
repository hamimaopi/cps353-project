package api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiThreadedUserComputeEngineAPIImpl extends UserComputeEngineAPIImpl implements UserComputeEngineAPI {

    public static final int DEFAULT_MAX_THREADS = 4;
    private final int maxThreads;

    public MultiThreadedUserComputeEngineAPIImpl(DataStorageAPI dataStorageAPI, ComputationAPI computationAPI) {
        this(dataStorageAPI, computationAPI, DEFAULT_MAX_THREADS);
    }

    public MultiThreadedUserComputeEngineAPIImpl(DataStorageAPI dataStorageAPI, ComputationAPI computationAPI, int maxThreads) {
        super(dataStorageAPI, computationAPI);
        if (maxThreads <= 0) {
            throw new IllegalArgumentException("Maximum number of threads must be positive");
        }
        this.maxThreads = maxThreads;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    @Override
    protected List<String> executeComputation(List<Integer> inputs) throws Exception {
        if (inputs == null || inputs.isEmpty()) {
            return new ArrayList<>();
        }

        ExecutorService threadPool = Executors.newFixedThreadPool(maxThreads);
        try {
            List<Future<String>> futures = new ArrayList<>();
            for (Integer inputNumber : inputs) {
                futures.add(threadPool.submit(() -> {
                    ComputationRequest request = new ComputationRequest(inputNumber);
                    ComputationResult computationResult = getComputationAPI().countPrimesBelow(request);
                    int primeCount = (computationResult != null) ? computationResult.getPrimeCount() : 0;
                    return String.valueOf(primeCount);
                }));
            }

            List<String> results = new ArrayList<>();
            for (Future<String> future : futures) {
                results.add(future.get());
            }
            return results;
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Exception exception) {
                throw exception;
            }
            throw new RuntimeException("Unexpected error during multi-threaded computation", cause);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Multi-threaded computation was interrupted", e);
        } finally {
            threadPool.shutdown();
        }
    }
}
