package com.xinghuo.pro_classify.exception;

import lombok.Getter;

/**
 * 业务异常枚举
 */
@Getter
public enum BizExceptionEnum {
    SERVER_INTERNAL_ERROR(500, "服务器内部错误")
    ;

    private final Integer code;
    private final String msg;
    BizExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
