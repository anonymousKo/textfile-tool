public class Replace {
    public static void main(String[] args) {
        String path ="D:\\test";
        String oldStr = "2";
        String newStr = "1";
        FileOperationUtil fileOperationUtil =new FileOperationUtil();
        fileOperationUtil.replace(path,oldStr,newStr);
    }
}
