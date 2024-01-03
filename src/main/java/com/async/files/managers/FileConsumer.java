package com.async.files.managers;

import com.async.files.models.FilesResult;
import com.async.files.models.ProcessStatus;
import com.async.files.repository.FilesRepo;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;

public class FileConsumer implements Callable<FilesResult> {

    private final FilesResult filesResult;
    private final Queue<String> queue;

    public FileConsumer(FilesResult filesResult, Queue<String> queue) {
        this.filesResult = filesResult;
        this.queue = queue;
    }

    @Override
    public FilesResult call() throws Exception {

        while(true) {
            synchronized (queue) {
                while (queue.isEmpty() && FilesResult.totalFiles > 0) {
                    System.out.println("Queue is empty, cant be consumed: " + Thread.currentThread().getName());
                    queue.wait();
                }

                if (FilesResult.totalFiles == 0) {
                    System.out.println("Consumer thread released " + Thread.currentThread().getName());
                    filesResult.setProcessStatus(ProcessStatus.COMPLETED);
                    queue.notifyAll();
                    break;
                }
                String fileContent = queue.poll();

                System.out.println("Consumer " + Thread.currentThread().getName() +
                        " reading file content from queue: " + fileContent);

                synchronized (filesResult) {
                    for(int i = 0; i < fileContent.length(); i++) {
                        char x = fileContent.charAt(i);
                        if (x == ' ') continue;
                        filesResult.incrementCharFreq(x, 1);
                    }
                    FilesResult.totalFiles--;
                }
                queue.notifyAll();
            }


        }
        return filesResult;
    }
}
