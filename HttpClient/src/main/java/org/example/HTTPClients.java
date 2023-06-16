package org.example;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * Hello world!
 *
 */
public class HTTPClients {


    public static void main(String[] args) {
        ThreadPoolExecutor executor =
                (ThreadPoolExecutor) Executors.newFixedThreadPool(8);
        for(int i = 0;i < 1; i++){
            executor.execute(new Client(1000));
        }
    }
}
