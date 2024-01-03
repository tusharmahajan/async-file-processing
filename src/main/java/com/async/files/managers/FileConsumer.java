package com.async.files.managers;

import com.async.files.models.FilesResult;
import com.async.files.models.ProcessStatus;
import com.async.files.repository.FilesRepo;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;

public class FileConsumer implements Callable<FilesResult> {

    private final int processId;
    private final Queue<String> queue;
    private final int totalFiles;

    public FileConsumer(int processId, Queue<String> queue, int totalFiles) {
        this.processId = processId;
        this.queue = queue;
        this.totalFiles = totalFiles;
    }

    @Override
    public FilesResult call() throws Exception {

        FilesResult filesResult = FilesRepo.getFilesResult(processId);
        Map<Character, Integer> charFrequency = new HashMap<>();

        synchronized (queue){
            while(queue.isEmpty() && FilesResult.totalFiles > 0){
                System.out.println("Queue is empty, cant be consumed: " + Thread.currentThread().getName());
                queue.wait();
            }
            if(queue.isEmpty() || FilesResult.totalFiles == 0){
                System.out.println("Consumer thread released " + Thread.currentThread().getName());
                queue.notifyAll();
                return filesResult;
            }
            String fileContent = queue.poll();

            System.out.println("Consumer " + Thread.currentThread().getName() +
                    " reading file content from queue: " + fileContent);
            for(int i = 0;i<fileContent.length();i++){
                char x = fileContent.charAt(i);
                if(x == ' ') continue;
                int freq = charFrequency.getOrDefault(x, 0);
                charFrequency.put(x, freq+1);
            }
            queue.notifyAll();
        }

        synchronized (filesResult){
            for(Map.Entry<Character, Integer> map: charFrequency.entrySet()){
                filesResult.incrementCharFreq(map.getKey(), map.getValue());
            }
            FilesResult.totalFiles--;
        }
        if(FilesResult.totalFiles == 0){
            filesResult.setProcessStatus(ProcessStatus.COMPLETED);
        }
        return filesResult;
    }
}
