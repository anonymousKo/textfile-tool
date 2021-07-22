import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class FileOperationUtil {
    public  void replace(String path,String oldStr,String newStr) {
        List<File> FileList = new ArrayList<>();
        if (fileCheck(path)) {
            FileList = new ArrayList<>(FileUtil.loopFiles(path, file1 -> {
                return !(file1.getPath().toLowerCase().contains("_bak"));        //ignore the backup files
            })
            );
        }
        log.info("find files: {}",FileList);
        for (File file : FileList) {
            replaceByLine(file, oldStr, newStr);
        }
    }

    private  boolean fileCheck(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            return Objects.requireNonNull(file.listFiles()).length > 0;
        }
        return false;
    }

    private void replaceByLine(File file,String oldstr,String newstr){
        List<String> list;
        boolean isChange = false;
        try {
            list = FileUtils.readLines(file, "UTF-8");
            for (int i = 0; i < list.size(); i++) {
                if(list.get(i).contains(oldstr)){
                    isChange = true;
                    String temp = list.get(i).replaceAll(oldstr, newstr);
                    log.info("File: {}, line: {}, before replace：{}，after replace：{}",file,i+1,list.get(i),temp);
                    list.remove(i);
                    list.add(i, temp);
                }
            }
            if(isChange) {backup(file);}
            FileUtils.writeLines(file, "UTF-8", list, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void backup(File srcFile) throws IOException {
        String srcPath = srcFile.getPath();
        String destName,destPath;
        String fileName = srcFile.getName();
        int extension = fileName.lastIndexOf(".");
        if(extension != -1){
             destName = "bak\\" + fileName.substring(0,extension) + "_bak" + fileName.substring(extension);
        }
        else{
            destName = "bak\\" + srcPath + "_bak";
        }
        destPath = srcPath.replace(fileName,destName);
        FileUtils.copyFile(srcFile, new File(destPath));
        log.info("Change the file: {}, create the bakFile : {}",srcPath,destPath);
    }
}
