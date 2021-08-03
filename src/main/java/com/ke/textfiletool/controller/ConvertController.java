package com.ke.textfiletool.controller;

import com.ke.textfiletool.service.FileOperationServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ConvertController {
    List<File> FileList = new ArrayList<>();
    FileOperationServiceImpl fileOperationServiceImpl =new FileOperationServiceImpl();

    public void BatchMdToHtml(String path) {
        FileList = fileOperationServiceImpl.getFiles(path, file1 -> {
            return (file1.getPath().toLowerCase().contains(".md") && !file1.getPath().toLowerCase().contains("_bak"));        //ignore the backup files
        });
        log.info("find files: {}", FileList);
        FileList.forEach(file -> {
            try {
                fileOperationServiceImpl.mdToHtml(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
