package com.async.files.managers;

import com.async.files.models.FileResponse;
import com.async.files.models.FilesResult;
import com.async.files.repository.FilesRepo;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class FileManager {

    public FileResponse startReadingFiles(List<String> filePaths){
        int capacity = 50;
        Queue<String> queue = new ArrayDeque<>(capacity);
        FilesResult filesResult = new FilesResult();
        FilesResult.totalFiles = filePaths.size();
        FilesRepo.storeProcessId(filesResult);

        ExecutorService producerPool = Executors.newFixedThreadPool(2);
        ExecutorService consumerPool = Executors.newFixedThreadPool(5);

        for(String path: filePaths){
            producerPool.submit(new FileWorker(path, queue, capacity, filesResult.getProcessId()));
        }

        for(int i = 0;i<5;i++){
            consumerPool.submit(new FileConsumer(filesResult, queue));
        }
        producerPool.shutdown();
        consumerPool.shutdown();

        return new FileResponse(filesResult.getProcessId(), filesResult.getProcessStatus());
    }
}
