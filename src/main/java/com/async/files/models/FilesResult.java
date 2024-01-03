package com.async.files.models;

import com.async.files.repository.FilesRepo;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Data
public class FilesResult {

    private int processId;
    private ProcessStatus processStatus;
    private Map<Character, Integer> charFreq;
    public static int totalFiles;

    public FilesResult() {
        Random random = new Random();
        int number;
        while (true){
            number = random.nextInt(1, 1000);
            boolean exist = FilesRepo.checkProcessId(number);
            if(!exist){
                break;
            }
        }
        processId = number;
        processStatus = ProcessStatus.IN_PROCESS;
        charFreq = new HashMap<>();
    }

    public void incrementCharFreq(char c, Integer value){
        int currentFreq = charFreq.getOrDefault(c, 0);
        charFreq.put(c, currentFreq+value);
    }

}
