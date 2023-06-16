package com.kurtlike.cluster.local;


import com.kurtlike.cluster.local.queues.Request;

public class ScriptBuilder {
    public static String buildScript(Request request, String remoteIp, String remoteDirPath){
        return "scp " + buildFilePath(request) + " " + buildAddress(remoteIp, remoteDirPath);
    }
    public static String buildFilePath(Request req){
        return "requestsData/" + req.userName + "/" + req.id + "." + req.dataFormat;
    }
    public static String buildAddress(String remoteIp, String remoteDirPath){
        return "rock@" + remoteIp + ":" + remoteDirPath;
    }
}
