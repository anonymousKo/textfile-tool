package com.ke.textfiletool.controller;

import com.ke.textfiletool.service.FileOperationServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ReplaceController {
    FileOperationServiceImpl fileOperationServiceImpl =new FileOperationServiceImpl();
    List<File> FileList = new ArrayList<>();

    public void replace(String path, String oldStr, String newStr) {
        FileList = fileOperationServiceImpl.getFiles(path, file1 -> {
            return (file1.getPath().toLowerCase().contains(".md") && !file1.getPath().toLowerCase().contains("_bak"));        //ignore the backup files
        });
        log.info("find files: {}", FileList);
        FileList.forEach(file -> fileOperationServiceImpl.replaceByLine(file, oldStr, newStr));
    }
}
