package com.async.files.models;

import lombok.Data;

@Data
public class FileResponse {

    private final int fileId;
    private final ProcessStatus processStatus;

    public FileResponse(int fileId, ProcessStatus processStatus) {
        this.fileId = fileId;
        this.processStatus = processStatus;
    }
}
