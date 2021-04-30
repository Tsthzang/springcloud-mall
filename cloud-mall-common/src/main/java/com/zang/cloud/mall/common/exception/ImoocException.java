package com.zang.cloud.mall.common.exception;

public class ImoocException extends RuntimeException{
    private final Integer code;
    private final String message;

    public ImoocException(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public ImoocException(ImoocMallExceptionEnum exceptionEnum) {
        this(exceptionEnum.getCode(), exceptionEnum.getMsg());
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
