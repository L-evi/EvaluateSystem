package com.project.evaluate.service;

import com.project.evaluate.entity.file.FileChunkDto;

public interface FileService {
    Boolean uploadFile(FileChunkDto fileChunkDto);
}
