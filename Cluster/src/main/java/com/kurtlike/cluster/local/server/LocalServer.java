package com.kurtlike.cluster.local.server;

import com.kurtlike.cluster.local.queues.QueuesController;
import com.kurtlike.cluster.local.queues.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@PropertySource("classpath:application.properties")
public class LocalServer implements Runnable {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private int port;
    @Autowired
    private  QueuesController queuesController;
    private final int maxThreads;

    public LocalServer(@Value("${application.maxThreads}") int maxThreads, @Value("${application.localPort}") int port) {
        this.maxThreads = maxThreads;
        this.port = port;
        Thread thread = new Thread(this, "server");
        thread.start();
    }
    private final HashMap<Long,Request> readyRequests;
    {
        readyRequests = new HashMap<>();
    }
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        ThreadPoolExecutor executor =
                (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThreads);

        while(true) {
            clientSocket = serverSocket.accept();
            executor.execute(new ClientHandler(clientSocket, queuesController, readyRequests));
        }
    }
    public boolean addRequest(Request request){
        return queuesController.addRequest(request);
    }
    public Request getReadyRequest(long id){
        synchronized (readyRequests){
            return readyRequests.remove(id);
        }
    }

    @Override
    public void run() {
        try {
            this.start(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
