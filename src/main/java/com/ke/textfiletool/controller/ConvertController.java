package com.ke.textfiletool.controller;

import com.ke.textfiletool.util.FileOperationUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ConvertController {
    List<File> FileList = new ArrayList<>();
    FileOperationUtil fileOperationUtil =new FileOperationUtil();

    public void BatchMdToHtml(String path) {
        FileList = fileOperationUtil.getFiles(path, file1 -> {
            return (file1.getPath().toLowerCase().contains(".md") && !file1.getPath().toLowerCase().contains("_bak"));        //ignore the backup files
        });
        log.info("find files: {}", FileList);
        FileList.forEach(file -> {
            try {
                fileOperationUtil.mdToHtml(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
