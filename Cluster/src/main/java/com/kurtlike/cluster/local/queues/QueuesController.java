package com.kurtlike.cluster.local.queues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
@PropertySource("classpath:application.properties")
public class QueuesController {
    private final List<LinkedList<Request>> queueList;
    private final int prioritiesNum;
    @Autowired
    public QueuesController(@Value("${application.prioritiesNum}") int prioritiesNum) {
        this.prioritiesNum = prioritiesNum;
        queueList = new ArrayList<>(prioritiesNum);
        fillQueueList();
    }
    private void fillQueueList(){
        for(int i = 0; i < prioritiesNum; i++){
            LinkedList<Request> q = new LinkedList<>();
            queueList.add(i,q);
        }
    }
    public Request getNextRequest(){
        synchronized (queueList) {
            for (int i = 0; i < prioritiesNum; i++) {
                Request req = queueList.get(i).poll();
                if(req != null){
                    return req;
                }
            }
        }
        return null;
    }
    public boolean addRequest(Request request){
        int priority = request.priority;
        synchronized (queueList) {
            LinkedList<Request> q = queueList.get(priority);
            return q.offer(request);
        }
    }
}
