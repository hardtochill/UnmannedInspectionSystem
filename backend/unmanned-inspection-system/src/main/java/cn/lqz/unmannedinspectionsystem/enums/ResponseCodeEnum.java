package cn.lqz.unmannedinspectionsystem.enums;

public enum ResponseCodeEnum {
    CODE_200(200,"请求成功"),
    CODE_400(400,"错误"),
    CODE_401(401,"登陆超时"),
    CODE_404(404,"请求地址不存在"),
    CODE_500(500,"服务器返回错误，请联系管理员"),
    CODE_600(600,"请求参数错误");
    private Integer code;
    private String msg;

    ResponseCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
