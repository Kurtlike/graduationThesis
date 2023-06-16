package com.kurtlike.cluster.local.queues;

public class Request {
    public final long id;
    public final int priority;
    public final String userName;
    public final String fileName;
    public final String dataFormat;
    private Answer answer;
    public Request(long id, int priority, String userName, String fileName, String dataFormat) {
        this.id = id;
        this.priority = priority;
        this.userName = userName;
        this.fileName = fileName;
        this.dataFormat = dataFormat;
    }
    public void setAnswer (Answer answer){
        this.answer = answer;
    }
    public boolean isReady(){
        if(answer == null) return false;
        return answer.ready;
    }
    public Answer getAnswer(){
        return answer;
    }

}

