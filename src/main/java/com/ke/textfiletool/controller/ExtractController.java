package com.ke.textfiletool.controller;

import com.ke.textfiletool.util.FileOperationUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExtractController {
    List<File> FileList = new ArrayList<>();
    FileOperationUtil fileOperationUtil =new FileOperationUtil();

    public void ExtractPdfHighlight(String path) {
        FileList = fileOperationUtil.getFiles(path, file1 -> {
            return (file1.getPath().toLowerCase().contains(".pdf") && !file1.getPath().toLowerCase().contains(".pdf.txt"));        //ignore the backup files
        });
        log.info("find files: {}", FileList);
        FileList.forEach(file -> fileOperationUtil.extractHighlight(file));
    }
}
