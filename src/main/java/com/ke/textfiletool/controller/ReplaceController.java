package com.ke.textfiletool.controller;

import com.ke.textfiletool.util.FileOperationUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class ReplaceController {
    List<File> FileList = new ArrayList<>();
    String oldStr = null;
    String newStr = "";

    public void replace(String path) {
        FileList = FileOperationUtil.findAllBySuffix(path, "md");
        FileList.removeIf(file ->  file.getPath().toLowerCase().contains("_bak"));
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
        FileList.forEach(file -> FileOperationUtil.replaceByLine(file, oldStr, newStr));
    }
}
