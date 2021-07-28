
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Executor {
    public static void main(String[] args) {
        try {
            if (args == null || args.length <1){
                throw new Exception("No input param");
            }
            if (Integer.parseInt(args[0]) != 2 && Integer.parseInt(args[0]) != 1) {
                log.info("InvalidOperationNum !");
            } else if (Integer.parseInt(args[0]) == 1) {
                String path = System.getProperty("user.dir");
                log.info("Current path :{}", path);
                if (args.length != 3) {
                    throw new Exception("Cannot resolve replacement");
                } else {
                    String oldStr = args[1];
                    String newStr = args[2];
                    FileOperationUtil fileOperationUtil = new FileOperationUtil();
                    fileOperationUtil.replace(path, oldStr, newStr);
                }
            } else if (Integer.parseInt(args[0]) == 2) {
                String path = System.getProperty("user.dir");
                log.info("Current path :{}", path);
                FileOperationUtil fileOperationUtil = new FileOperationUtil();
                fileOperationUtil.BatchMdToHtml(path);
            }
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }



}
