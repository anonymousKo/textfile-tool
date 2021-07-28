
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Executor {
    public static void main(String[] args) {
        String path = System.getProperty("user.dir");
        String filePath = path.substring(0,path.lastIndexOf("\\"));
        try {
            if (args == null || args.length <1){
                throw new Exception("No input param");
            }
            if (Integer.parseInt(args[0]) != 2 && Integer.parseInt(args[0]) != 1) {
                log.info("InvalidOperationNum !");
            } else if (Integer.parseInt(args[0]) == 1) {
                log.info("Current path :{}", filePath);
                if (args.length != 3) {
                    throw new Exception("Cannot resolve replacement");
                } else {
                    String oldStr = args[1];
                    String newStr = args[2];
                    FileOperationUtil fileOperationUtil = new FileOperationUtil();
                    fileOperationUtil.replace(filePath, oldStr, newStr);
                }
            } else if (Integer.parseInt(args[0]) == 2) {
                log.info("Current path :{}", filePath);
                FileOperationUtil fileOperationUtil = new FileOperationUtil();
                fileOperationUtil.BatchMdToHtml(filePath);
            }
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }



}
