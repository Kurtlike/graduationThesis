package org.example;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class Client implements Runnable{
    private int req;

    public Client(int req) {
        this.req = req;
    }

    private static HashMap<Integer, File> files;
    @Override
    public void run() {
        openImagesFiles();
        long executionTime;
        double sum = 0;
        for (int i = 0; i < req; i++) {
            HttpClient httpclient = HttpClients.createDefault();
            int priority = getRandomNumber(0, 3);
            File file = files.get(getRandomNumber(0, 10));
            HttpPost post = new HttpPost("http://127.0.0.1:8080/addRequest");
            post.setHeader("Accept", "application/json");

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("username", "kurt");
            builder.addTextBody("priority", Integer.toString(priority));
            builder.addPart("file", new FileBody(file));
            builder.addTextBody("dataType", "png");
            post.setEntity(builder.build());
            executionTime = System.currentTimeMillis();
            HttpResponse response = null;
            try {
                response = httpclient.execute(post);
/*
                response.getStatusLine().getStatusCode();
                String responseMsg = EntityUtils.toString(response.getEntity(), "UTF-8");
                HttpPost postRes = new HttpPost("http://127.0.0.1:8080/getAnswer");
                postRes.setHeader("Accept", "application/json");
                MultipartEntityBuilder builderRes = MultipartEntityBuilder.create();
                builderRes.addTextBody("id", responseMsg);
                postRes.setEntity(builderRes.build());
                boolean isReady = false;
                do {
                    response = httpclient.execute(postRes);
                    String res = EntityUtils.toString(response.getEntity(), "UTF-8");
                    JSONObject jsonObject = new JSONObject(res);
                    isReady = jsonObject.getBoolean("ready");
                } while (!isReady);
                if(i!= 0) sum+= System.currentTimeMillis() - executionTime;
                */
                //sleep((long) (Math.random() * 1000));
                //sleep(100);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(sum/req);
    }
    private static void openImagesFiles() {
        files = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            files.put(i, new File("files/" + i + ".png"));
        }
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}

