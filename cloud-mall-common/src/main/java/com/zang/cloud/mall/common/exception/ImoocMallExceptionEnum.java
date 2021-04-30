package com.zang.cloud.mall.common.exception;

public enum ImoocMallExceptionEnum {
    NEED_USER_NAME(10001,"用户名不能为空"),
    NEED_PASSWORD(10002, "密码不能为空"),
    PASSWORD_TOO_SHORT(10003, "密码小于8位"),
    NAME_EXISTED(10004, "不允许重名"),
    INSERT_FAILED(10005, "插入失败，请重试"),
    WRONG_PASSWORD(10006, "密码错误"),
    NEED_LOGIN(10007, "需要登陆"),
    UPDATE_FAILED(10008, "更新错误"),
    NEED_ADMIN(10009, "需要管理员登录"),
    PARA_NOT_NULL(10010, "参数不能为空"),
    CREATE_FAILED(10011, "新增失败"),
    REQUEST_PARAM_ERROR(10012, "添加参数异常"),
    DELETE_FAILED(10013, "删除失败"),
    MKDIR_FAILED(10014, "创建文件夹失败"),
    UPLOAD_FAILED(10015, "图片上传失败"),
    NOT_SALE(10016, "商品状态不可销售"),
    NOT_ENOUGF(10017, "商品库存不足"),
    CART_EMPTY(10018, "还没有选择要下单的商品"),
    NO_ENUM(10019, "未找到对应枚举"),
    NO_ORDER(10020, "订单不存在"),
    NOT_YOUR_ORDER(10021, "订单不属于你"),
    WRONG_ORDER_STATUS(10022, "订单此时状态已经不能取消"),
    SYSTEM_ERROR(2000,"系统错误，请重试");
    Integer code;
    String msg;

    ImoocMallExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
