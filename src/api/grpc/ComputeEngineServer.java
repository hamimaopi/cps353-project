package api.grpc;

import api.ComputationAPIImpl;
import api.MultiThreadedUserComputeEngineAPIImpl;
import api.UserComputeEngineAPI;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ComputeEngineServer {

    public static final int DEFAULT_PORT = 50051;
    public static final String DEFAULT_DATA_STORAGE_HOST = "localhost";

    private final int port;
    private final UserComputeEngineAPI computeEngineAPI;
    private final GrpcDataStorageClient ownedDataStorageClient;
    private Server server;

    public ComputeEngineServer(int port, String dataStorageHost, int dataStoragePort) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 0 and 65535");
        }
        this.ownedDataStorageClient = new GrpcDataStorageClient(dataStorageHost, dataStoragePort);
        this.computeEngineAPI = new MultiThreadedUserComputeEngineAPIImpl(
                ownedDataStorageClient,
                new ComputationAPIImpl());
        this.port = port;
    }

    public ComputeEngineServer(int port, UserComputeEngineAPI computeEngineAPI) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 0 and 65535");
        }
        if (computeEngineAPI == null) {
            throw new IllegalArgumentException("UserComputeEngineAPI cannot be null");
        }
        this.port = port;
        this.computeEngineAPI = computeEngineAPI;
        this.ownedDataStorageClient = null;
    }

    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new ComputeEngineServiceImpl(computeEngineAPI))
                .build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ComputeEngineServer.this.stop();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));
    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
        if (ownedDataStorageClient != null) {
            ownedDataStorageClient.close();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public int getPort() {
        return (server != null) ? server.getPort() : port;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int serverPort = (args.length > 0) ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        String dataStorageHost = (args.length > 1) ? args[1] : DEFAULT_DATA_STORAGE_HOST;
        int dataStoragePort = (args.length > 2) ? Integer.parseInt(args[2]) : DataStorageServer.DEFAULT_PORT;

        ComputeEngineServer computeServer = new ComputeEngineServer(
                serverPort,
                dataStorageHost,
                dataStoragePort);
        computeServer.start();
        System.out.println("ComputeEngineServer started, listening on port " + computeServer.getPort()
                + " (DataStorage at " + dataStorageHost + ":" + dataStoragePort + ")");
        computeServer.blockUntilShutdown();
    }
}
