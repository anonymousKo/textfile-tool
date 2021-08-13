package com.ke.textfiletool.controller;

import com.ke.textfiletool.util.FileOperationUtil;
import com.ke.textfiletool.util.PdfUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PdfController {
    List<File> FileList = new ArrayList<>();
    FileOperationUtil fileOperationUtil =new FileOperationUtil();
    PdfUtil pdfUtil =new PdfUtil();

    private void findALlPdf(String path){
        FileList = fileOperationUtil.getFiles(path, file1 -> {
            // only find .pdf file
            return (file1.getPath().toLowerCase().lastIndexOf(".pdf") == file1.getPath().length() - 4);
        });
        log.info("find pdf file -> {}", FileList);
    }

    public void extractPdfHighlight(String path) {
        findALlPdf(path);
        FileList.forEach(file ->
                pdfUtil.extractHighlight(file));
    }
    public void removeEncryption(String path){
        findALlPdf(path);
        FileList.forEach(file ->
                pdfUtil.removeEncryption(file));
    }
}
