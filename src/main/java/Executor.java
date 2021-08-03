
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class Executor {
    public static void main(String[] args) {
        String path = System.getProperty("user.dir");
        String parentPath = path.substring(0,path.lastIndexOf("\\"));
        FileOperationUtil fileOperationUtil = new FileOperationUtil();
        try {
            if (args.length != 1){
                throw new Exception("No input param");
            }
            if (args[0].equals(OperateType.Replace.getCode())) {
                log.info("Current path :{}", parentPath);
                Scanner s = new Scanner(System.in);
                String oldStr = null;
                String newStr = "";
                System.out.print("input the oldStr: ");
                if(s.hasNext()){
                    oldStr = s.nextLine();
                }
                System.out.print("input the newStr: ");
                if(s.hasNextLine()){
                    newStr = s.nextLine();
                }
                fileOperationUtil.replace(parentPath, oldStr, newStr);
            } else if (args[0].equals(OperateType.MdToHtml.getCode())) {
                log.info("Current path :{}", parentPath);
                fileOperationUtil.BatchMdToHtml(parentPath);
            }else if (args[0].equals(OperateType.PdfExtract.getCode())){
                fileOperationUtil.ExtractPdfHighlight(path);
            } else {
                throw new Exception("Invalid operateType");
            }
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }



}
