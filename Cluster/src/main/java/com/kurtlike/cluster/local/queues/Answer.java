package com.kurtlike.cluster.local.queues;

public class Answer {
    public boolean ready = false;
    public String result;

    public Answer(String result) {
        this.result = result;
        ready = true;
    }
    public Answer() {}
}
