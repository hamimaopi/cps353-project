## Checkpoint 2: Draft the APIs

### 1. Computation Selection
**Count the Number of Prime Numbers Below the Input**

The chosen computation counts how many prime numbers are smaller than a given positive integer input.

- **Input Requirement**: A single positive integer greater than 0 and less than `Integer.MAX_VALUE`.
- **CPU Intensive Nature**: The prime counting algorithm checks each number below the input for primality via nested divisibility loops, making it CPU intensive for performance benchmarking.
- **Sample Input and Output**:

| Input | Prime Numbers Below Input | Output |
| ----- | ------------------------- | -----: |
| 10    | 2, 3, 5, 7                |      4 |
| 20    | 2, 3, 5, 7, 11, 13, 17, 19|      8 |
| 100   | 2, 3, 5, 7, ... , 97      |     25 |

---

### 2. System Architecture Diagram
The system architecture includes three distinct API boundaries:

1. **Network API (`UserComputeEngineAPI`)**: Network boundary between User/Client and the Compute Engine process.
2. **Process API (`DataStorageAPI`)**: Process boundary between Compute Engine (Job Manager) and Data Storage System, handling both reading input and writing computed output.
3. **Conceptual API (`ComputationAPI`)**: Conceptual boundary situated **inside** the Compute Engine process, connecting the Job Manager/Orchestrator to the Computation Component.

Each boundary features explicit request and response flows.

![System Diagram](checkpoint2.png)

---

### 3. API Design & Key Features

- **Network API (`UserComputeEngineAPI`)**: Uses generic `InputConfig` and `OutputConfig` objects instead of hardcoded strings. Supports both custom delimiters via `DelimiterConfig` and optional default delimiters (`DelimiterConfig.defaultConfig()`).
- **Process API (`DataStorageAPI`)**: Provides methods to read input data (`readInputData`) AND write computed results back out (`writeOutputData`) using generic `InputConfig` and `OutputConfig` location objects.
- **Conceptual API (`ComputationAPI`)**: Operates inside the Compute Engine process. Wraps primitive values inside flexible objects (`ComputationRequest` and `ComputationResult`).

---

## Checkpoint 6: Multi-Threading Configuration

### Thread Pool Upper Bound
The multi-threaded coordinator implementation (`MultiThreadedUserComputeEngineAPIImpl`) uses a fixed thread pool created via `Executors.newFixedThreadPool(4)`.
- **Upper Bound**: 4 concurrent worker threads.
- **Rationale**: Prime-counting is a CPU-intensive operation. Setting an upper bound of 4 threads balances parallelism across common modern quad-core and multi-core CPU architectures while preventing thread creation explosion, thread thrashing, and excessive CPU context-switching overhead.
- **Deterministic Ordering**: Subtask executions are collected in input order using `List<Future<String>>` and resolved sequentially, ensuring multi-threaded outputs match single-threaded outputs exactly.

---

## Checkpoint 7: gRPC Network & Process Architecture

The system runs across three independent processes communicating over gRPC and Protocol Buffers:
1. **Data Storage Server** (`api.grpc.DataStorageServer`, default port `50052`): Exposes `DataStorageService` (`proto/datastorage.proto`) wrapping `DataStorageAPIImpl`.
2. **Compute Engine Server** (`api.grpc.ComputeEngineServer`, default port `50051`): Exposes `ComputeEngineService` (`proto/computeengine.proto`) backed by `MultiThreadedUserComputeEngineAPIImpl` and connects to the Data Storage process via `GrpcDataStorageClient` (`DataStorageAPI`).
3. **User Frontend Client** (`api.grpc.ComputeEngineClient`): Accepts either an input file path or a typed-in list of numbers, an output file path, and an optional output delimiter (via interactive `Scanner` prompts or CLI flags).

### Running the 3 Processes End-to-End
Open three separate terminals in the project root:

1. **Start the Data Storage server** (default port `50052`):
   ```bash
   ./gradlew runDataStorageServer
   ```
2. **Start the Compute Engine server** (default port `50051`, connecting to `localhost:50052`):
   ```bash
   ./gradlew runComputeEngineServer
   ```
3. **Run the User Client**:
   - **Interactive prompts**:
     ```bash
     ./gradlew runComputeClient --console=plain
     ```
   - **CLI arguments (file input)**:
     ```bash
     ./gradlew runComputeClient --args="--input-file test/testInputFile.test --output-file manualTestOutput.txt"
     ```
   - **CLI arguments (typed-in in-memory numbers with custom delimiter)**:
     ```bash
     ./gradlew runComputeClient --args="--numbers 1,10,20 --output-file manualTestOutput.txt --delimiter ;"
     ```
