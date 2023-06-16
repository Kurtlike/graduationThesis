package com.kurtlike.cluster.local.server;

import com.kurtlike.cluster.local.Logger;
import com.kurtlike.cluster.local.ScriptBuilder;
import com.kurtlike.cluster.local.queues.Answer;
import com.kurtlike.cluster.local.queues.QueuesController;
import com.kurtlike.cluster.local.queues.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;

import static java.lang.Thread.sleep;

class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final QueuesController queuesController;
    private final HashMap<Long, Request> readyRequests;
    private final Logger logger;
    private final InetSocketAddress socketAddress;
    public ClientHandler(Socket socket, QueuesController controller, HashMap<Long, Request> readyRequests) {
        this.clientSocket = socket;
        this.queuesController = controller;
        this.readyRequests = readyRequests;
        socketAddress = (InetSocketAddress) clientSocket.getRemoteSocketAddress();
        this.logger = new Logger(socketAddress.getAddress().getHostAddress());
    }
    private void addReadyRequest(Request request){
        synchronized (readyRequests){
            readyRequests.put(request.id, request);
        }
    }
    @Override
    public void run() {
        PrintWriter out = null;
        BufferedReader in = null;
        long waitForTaskTime = System.currentTimeMillis();
        long fileCopyTime;
        long executionTime;
        double waitForTaskTimeSum = 0;
        double fileCopyTimeSum = 0;
        double executionTimeSum = 0;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while (true) {
                Request req = queuesController.getNextRequest();
                if (req != null) {
                    waitForTaskTime = System.currentTimeMillis() - waitForTaskTime;
                    waitForTaskTimeSum+= waitForTaskTime;
                    //String command = ScriptBuilder.buildScript(req, socketAddress.getAddress().getHostAddress(), "/home/rock/cluster/data");
                    String command = "scp requestsData/"+ req.userName + "/" + req.id + "." + req.dataFormat + " sudokurt@192.168.88.13:/home/sudokurt/cluster/data";
                    fileCopyTime = System.currentTimeMillis();
                    Process process = Runtime.getRuntime().exec(command);
                    process.waitFor();
                    fileCopyTime = System.currentTimeMillis() - fileCopyTime;
                    fileCopyTimeSum+=fileCopyTime;
                    out.println(req.id + "." + req.dataFormat);
                    executionTime = System.currentTimeMillis();
                    String ans = in.readLine();
                    executionTime = System.currentTimeMillis() - executionTime;
                    executionTimeSum+=executionTime;
                    Answer answer = new Answer(ans);
                    req.setAnswer(answer);
                    addReadyRequest(req);
                    logger.writeTimeS(executionTime, fileCopyTime, waitForTaskTime);
                    waitForTaskTime = System.currentTimeMillis();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.closeWriters();
        }
    }
}
