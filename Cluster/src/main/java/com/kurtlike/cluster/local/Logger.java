package com.kurtlike.cluster.local;

import java.io.*;

public class Logger {
    private final FileWriter executionTime;
    private final FileWriter fileCopyTime;
    private final FileWriter waitForTaskTime;

    public Logger(String clientName) {
        File executionTimeF= new File("logs/" + clientName + "/executionTime.txt");
        File fileCopyTimeF = new File("logs/" + clientName + "/fileCopyTime.txt");
        File waitForTaskTimeF = new File("logs/" + clientName + "/waitForTaskTime.txt");
        executionTimeF.getParentFile().mkdirs();
        try {
            executionTimeF.createNewFile();
            fileCopyTimeF.createNewFile();
            waitForTaskTimeF.createNewFile();
            executionTime = new FileWriter(executionTimeF);
            fileCopyTime = new FileWriter(fileCopyTimeF);
            waitForTaskTime = new FileWriter(waitForTaskTimeF);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeTimeS(long executionTimes, long fileCopyTimes, long waitForTaskTimes){
        try {
            executionTime.write(executionTimes + "\n");
            fileCopyTime.write(fileCopyTimes + "\n");
            waitForTaskTime.write(waitForTaskTimes + "\n");
            executionTime.flush();
            fileCopyTime.flush();
            waitForTaskTime.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void closeWriters(){
        try {
            executionTime.close();
            fileCopyTime.close();
            waitForTaskTime.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
