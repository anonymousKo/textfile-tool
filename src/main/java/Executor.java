
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class Executor {
    public static void main(String[] args) {
        String path = System.getProperty("user.dir");
        String filePath = path.substring(0,path.lastIndexOf("\\"));
        try {
            if (args.length != 1){
                throw new Exception("No input param");
            }
            if (args[0].equals(OperateType.MdToHtml.getCode())) {
                log.info("Current path :{}", filePath);
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
                FileOperationUtil fileOperationUtil = new FileOperationUtil();
                fileOperationUtil.replace(filePath, oldStr, newStr);
            } else if (args[0].equals(OperateType.Replace.getCode())) {
                log.info("Current path :{}", filePath);
                FileOperationUtil fileOperationUtil = new FileOperationUtil();
                fileOperationUtil.BatchMdToHtml(filePath);
            } else {
                throw new Exception("Invalid operateType");
            }
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }



}
