package com.async.files.repository;

import com.async.files.models.FilesResult;

import java.util.HashMap;
import java.util.Map;

public class FilesRepo {

    private static final Map<Integer, FilesResult> processMap = new HashMap<>();

    private FilesRepo(){

    }

    public static void storeProcessId(FilesResult filesResult){
        processMap.put(filesResult.getProcessId(), filesResult);
    }

    public static FilesResult getFilesResult(int processId){
        return processMap.get(processId);
    }

    public static boolean checkProcessId(int number) {
        return processMap.containsKey(number);
    }
}
