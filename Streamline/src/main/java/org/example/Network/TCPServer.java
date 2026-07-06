package org.example.Network;

import org.example.Protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TODO: Change this to Netty
public class TCPServer {
    ExecutorService executor = Executors.newFixedThreadPool(40);


    public TCPServer() {
        Thread thread = new Thread(this::createServer);
        thread.start();
    }

    private void createServer(){
        try (ServerSocket serverSocket = new ServerSocket(Protocol.SERVER_PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept(); //new client

                executor.execute(() -> {
                    try {
                        ClientHandler clientHandler = new ClientHandler();
                        clientHandler.handleClient(clientSocket);
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
