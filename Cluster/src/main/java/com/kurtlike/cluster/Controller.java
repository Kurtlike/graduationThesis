package com.kurtlike.cluster;

import com.kurtlike.cluster.local.queues.Answer;
import com.kurtlike.cluster.local.queues.Request;
import com.kurtlike.cluster.local.server.LocalServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

@RestController
public class Controller {

    @Autowired
    private LocalServer localServer;
    private long idQueue = 0;
    @PostMapping("/addRequest")
    public @ResponseBody String handleImgUpload(@RequestParam("username") String username, @RequestParam("priority") Integer priority, @RequestParam("file") MultipartFile file, @RequestParam("dataType") String datatype){
        if (!file.isEmpty()) {
            try {
                long id = generateId();
                File localFile = new File("requestsData/" +username + "/" + id + "." + datatype);
                localFile.getParentFile().mkdirs();
                localFile.createNewFile();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(localFile));
                stream.write(file.getBytes());
                stream.close();
                Request request = new Request(id, priority, username, file.getOriginalFilename(), datatype);
                localServer.addRequest(request);
                return Long.toString(id);
            } catch (Exception e) {
                return e.getMessage();
            }
        } else {
            return "File is empty";
        }
    }

    @PostMapping("/getAnswer")
    public @ResponseBody Answer getAnswer(@RequestParam("id") Long id){
       Request request = localServer.getReadyRequest(id);
       if(request != null) return request.getAnswer();
       return new Answer();
    }
    private long generateId(){
        idQueue++;
        return idQueue;
    }
}

