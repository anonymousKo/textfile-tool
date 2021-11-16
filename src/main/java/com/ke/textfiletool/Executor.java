package com.ke.textfiletool;

import com.ke.textfiletool.controller.ConvertController;
import com.ke.textfiletool.controller.PdfController;
import com.ke.textfiletool.controller.ReplaceController;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Scanner;

@Slf4j
public class Executor {
    public static void main(String[] args) {
        String parentPath = System.getProperty("user.dir");
//        parentPath = parentPath.substring(0,parentPath.lastIndexOf("\\"));
        Scanner s = new Scanner(System.in);
        System.out.print("input the param (" + OperateType.getAll() + "): ");
        String input = s.nextLine();
        try {
            if (input.isEmpty()){
                throw new Exception("No input param");
            }
            if (input.equals(OperateType.Replace.getCode())) {
                ReplaceController replaceController =new ReplaceController();
                log.info("Current path :{}", parentPath);
                replaceController.replace(parentPath);
            } else if (input.equals(OperateType.MdToHtml.getCode())) {
                ConvertController convertController =new ConvertController();
                log.info("Current path :{}", parentPath);
                convertController.BatchMdToHtml(parentPath);
            }else if (input.equals(OperateType.PdfExtract.getCode())){
                log.info("Current path :{}", parentPath);
                PdfController pdfController =new PdfController();
                pdfController.extractPdfHighlight(parentPath);
            }else if (input.equals(OperateType.PdfUnlock.getCode())){
                log.info("Current path :{}", parentPath);
                PdfController pdfController =new PdfController();
                pdfController.removeEncryption(parentPath);
            } else {
                throw new Exception("Invalid operateType");
            }
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }



}
