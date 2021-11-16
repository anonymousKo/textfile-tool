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
    PdfUtil pdfUtil =new PdfUtil();

    private void findAllPdf(String path){
        FileList = FileOperationUtil.findAllBySuffix(path, "pdf");
    }

    public void extractPdfHighlight(String path) {
        findAllPdf(path);
        FileList.forEach(file ->
                pdfUtil.extractHighlight(file));
    }
    public void removeEncryption(String path){
        findAllPdf(path);
        FileList.forEach(file ->
                pdfUtil.removeEncryption(file));
    }
}
