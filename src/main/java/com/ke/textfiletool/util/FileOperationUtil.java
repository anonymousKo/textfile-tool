package com.ke.textfiletool.util;

import cn.hutool.core.io.FileUtil;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class FileOperationUtil {
    public static List<File> getFiles(String path, FileFilter fileFilter) {
        if (fileCheck(path)) {
            return new ArrayList<>(FileUtil.loopFiles(path, fileFilter));
        } else {
            return null;
        }
    }

    private static boolean fileCheck(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            return Objects.requireNonNull(file.listFiles()).length > 0;
        }
        return false;
    }

    public static void replaceByLine(File file, String oldstr, String newstr) {
        List<String> list;
        boolean isChange = false;
        try {
            list = FileUtils.readLines(file, "UTF-8");
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).contains(oldstr)) {
                    isChange = true;
                    String temp = list.get(i).replaceAll(oldstr, newstr);
                    log.info("File: {}, line: {}, before replace：{}，after replace：{}", file, i + 1, list.get(i), temp);
                    list.remove(i);
                    list.add(i, temp);
                }
            }
            if (isChange) {
                backup(file);
            }
            FileUtils.writeLines(file, "UTF-8", list, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void backup(File srcFile) throws IOException {
        String srcPath = srcFile.getPath();
        String destName, destPath;
        String fileName = srcFile.getName();
        int extension = fileName.lastIndexOf(".");
        if (extension != -1) {
            destName = "bak\\" + fileName.substring(0, extension) + "_bak" + fileName.substring(extension);
        } else {
            destName = "bak\\" + srcPath + "_bak";
        }
        destPath = srcPath.replace(fileName, destName);
        FileUtils.copyFile(srcFile, new File(destPath));
        log.info("Change the file: {}, create the bakFile : {}", srcPath, destPath);
    }

    public static void mdToHtml(File file) throws IOException {
        String md = FileUtils.readFileToString(file, "utf-8");
        File htmlFile = new File(parseParentPath(file) +
                "0html\\" + file.getName().replace(".md", ".html"));
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // You can re-use parser and renderer instances
        Node document = parser.parse(md);
        String htmlText = renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n"
        FileUtils.writeStringToFile(htmlFile, htmlText, "utf-8");
        log.info("convert file {} success", htmlFile);
    }

    public static String parseParentPath(File file){
        String srcPath = file.getPath();
        return srcPath.substring(0, srcPath.lastIndexOf("\\")) + "\\";
    }



    public static String formatDate(String originDate) throws ParseException {
        Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(originDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy",Locale.ENGLISH);
        return simpleDateFormat.format(date);
    }

    public static List<File> findAllBySuffix(String path, String suffix){
        String extension = "." + suffix;
        List<File> fileList = getFiles(path, file1 -> {
            // only find .pdf file
            return (file1.getPath().toLowerCase().lastIndexOf(extension) == file1.getPath().length() - extension.length());
        });
        log.info("find " + suffix + " file -> {}", fileList);
        return fileList;
    }
}
