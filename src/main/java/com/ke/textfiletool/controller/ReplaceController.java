package com.ke.textfiletool.controller;

import com.ke.textfiletool.util.FileOperationUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class ReplaceController {
    FileOperationUtil fileOperationUtil =new FileOperationUtil();
    List<File> FileList = new ArrayList<>();
    String oldStr = null;
    String newStr = "";

    public void replace(String path) {
        FileList = fileOperationUtil.getFiles(path, file1 -> {
            return (file1.getPath().toLowerCase().contains(".md") && !file1.getPath().toLowerCase().contains("_bak"));        //ignore the backup files
        });
        log.info("find files: {}", FileList);
        Scanner s = new Scanner(System.in);
        System.out.print("input the oldStr: ");
        if(s.hasNext()){
            oldStr = s.nextLine();
        }
        System.out.print("input the newStr: ");
        if(s.hasNextLine()){
            newStr = s.nextLine();
        }
        FileList.forEach(file -> fileOperationUtil.replaceByLine(file, oldStr, newStr));
    }
}
