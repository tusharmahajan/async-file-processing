package com.async.files.dtos;

import lombok.Data;

import java.util.List;

@Data
public class FileRequest {

    private List<String> filePaths;
}
