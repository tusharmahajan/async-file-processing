package com.async.files.managers;

import com.async.files.models.FilesResult;
import com.async.files.repository.FilesRepo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.Queue;
import java.util.concurrent.Callable;

public class FileWorker implements Callable<FilesResult> {

    private final String path;
    private final Queue<String> queue;
    private final int capacity;
    private final int processId;

    public FileWorker(String path, Queue<String> queue, int capacity, int processId) {
        this.path = path;
        this.queue = queue;
        this.capacity = capacity;
        this.processId = processId;
    }

    @Override
    public FilesResult call() throws Exception {

        System.out.println("Producer processed file: " + path.substring(path.lastIndexOf("/")+1));
        String fileContent = readAndProcessFile();

        FilesResult filesResult = FilesRepo.getFilesResult(processId);

        synchronized (queue){
            while(queue.size() == capacity){
                System.out.println("Producer in waiting state as queue is full");
                queue.wait();
            }
            queue.add(fileContent);
            queue.notifyAll();
        }

        return filesResult;
    }

    private String readAndProcessFile() {

        StringBuilder stringBuilder = new StringBuilder();
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(this.path))) {
            String lines;
            while ((lines = bufferedReader.readLine()) != null){
                stringBuilder.append(lines);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
