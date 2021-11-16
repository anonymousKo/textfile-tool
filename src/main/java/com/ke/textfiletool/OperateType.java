package com.ke.textfiletool;

import java.util.Arrays;

public enum OperateType {
    MdToHtml("1"),
    Replace("2"),
    PdfExtract("3"),
    PdfUnlock("4");

    private String code;

    OperateType(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static String getAll(){
        StringBuffer stringBuffer = new StringBuffer();
        Arrays.asList(OperateType.values()).forEach(p -> {
            stringBuffer.append(OperateType.valueOf(p.toString()).getCode()).append(":").append(p.toString()).append(" ");
        });
        return stringBuffer.toString();
    }
}
