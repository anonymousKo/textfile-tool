public enum OperateType {
    MdToHtml("1"),
    Replace("2");
    private String code;
    OperateType(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
