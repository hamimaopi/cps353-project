package api.grpc;

import api.DataStorageAPI;
import api.DataStorageAPIImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class DataStorageServer {

    public static final int DEFAULT_PORT = 50052;

    private final int port;
    private final DataStorageAPI dataStorageAPI;
    private Server server;

    public DataStorageServer(int port) {
        this(port, new DataStorageAPIImpl());
    }

    public DataStorageServer(int port, DataStorageAPI dataStorageAPI) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 0 and 65535");
        }
        if (dataStorageAPI == null) {
            throw new IllegalArgumentException("DataStorageAPI cannot be null");
        }
        this.port = port;
        this.dataStorageAPI = dataStorageAPI;
    }

    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new DataStorageServiceImpl(dataStorageAPI))
                .build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                DataStorageServer.this.stop();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));
    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(5, TimeUnit.SECONDS);
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
        int serverPort = DEFAULT_PORT;
        if (args.length > 0) {
            serverPort = Integer.parseInt(args[0]);
        }
        DataStorageServer dataStorageServer = new DataStorageServer(serverPort);
        dataStorageServer.start();
        System.out.println("DataStorageServer started, listening on port " + dataStorageServer.getPort());
        dataStorageServer.blockUntilShutdown();
    }
}
