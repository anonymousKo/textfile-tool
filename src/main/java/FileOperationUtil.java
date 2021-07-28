import cn.hutool.core.io.FileUtil;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class FileOperationUtil {
    List<File> FileList = new ArrayList<>();
    public  void replace(String path, String oldStr, String newStr) {
        FileList = getFiles(path,file1 -> {
            return (file1.getPath().toLowerCase().contains(".md") && !file1.getPath().toLowerCase().contains("_bak"));        //ignore the backup files
        });
        log.info("find files: {}",FileList);
        FileList.forEach(file -> replaceByLine(file, oldStr, newStr));
    }
    public  void BatchMdToHtml(String path) {
        FileList = getFiles(path,file1 -> {
            return (file1.getPath().toLowerCase().contains(".md") && ! file1.getPath().toLowerCase().contains("_bak"));        //ignore the backup files
        });
        log.info("find files: {}",FileList);
        FileList.forEach(file -> {
            try {
                mdToHtml(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    private List<File> getFiles(String path,FileFilter fileFilter){
        if (fileCheck(path)) {
            return  new ArrayList<>(FileUtil.loopFiles(path,fileFilter));
        }
        else {
            return null;
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

    public void mdToHtml(File file) throws IOException {
        String md = FileUtils.readFileToString(file, "utf-8");
        String srcPath = file.getPath();
        File htmlFile = new File(srcPath.substring(0,srcPath.lastIndexOf("\\")) +
                "\\0html\\" + file.getName().replace(".md",".html"));
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // You can re-use parser and renderer instances
        Node document = parser.parse(md);
        String htmlText = renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n"
        FileUtils.writeStringToFile(htmlFile,htmlText,"utf-8");
        log.info("convert file {} success",htmlFile);


    }
}
