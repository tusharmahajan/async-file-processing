package com.async.files.controller;

import com.async.files.dtos.FileRequest;
import com.async.files.managers.FileManager;
import com.async.files.models.FileResponse;
import com.async.files.models.FilesResult;
import com.async.files.repository.FilesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileManager fileManager;

    @PostMapping("/process")
    public FileResponse processFiles(@RequestBody FileRequest fileRequest){
       return fileManager.startReadingFiles(fileRequest.getFilePaths());
    }

    @GetMapping("/result")
    public FilesResult processFiles(@RequestParam Integer processId){
        return FilesRepo.getFilesResult(processId);
    }
}
