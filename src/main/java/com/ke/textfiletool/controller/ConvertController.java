package com.ke.textfiletool.controller;

import com.ke.textfiletool.util.FileOperationUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ConvertController {
    List<File> FileList = new ArrayList<>();

    public void BatchMdToHtml(String path) {
        FileList = FileOperationUtil.findAllBySuffix(path, "md");
        FileList.removeIf(file ->  file.getPath().toLowerCase().contains("_bak"));
        log.info("find files: {}", FileList);
        FileList.forEach(file -> {
            try {
                FileOperationUtil.mdToHtml(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
