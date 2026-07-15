package org.example.Network;

import org.example.Protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TODO: Change this to Netty
public class TCPServer {
    ExecutorService executor;
    private int port;

    public TCPServer(int port) {
        this.port = port;
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    /// Start the server
    public void start() {
        Thread thread = new Thread(this::createServer);
        thread.start();
    }

    /// Handles accepting incoming connections and dispatching them against a threadpool
    private void createServer(){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept(); //new client

                executor.execute(() -> {
                    try {
                        ClientHandler clientHandler = new ClientHandler(clientSocket);
                        clientHandler.handleClient();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }

}
