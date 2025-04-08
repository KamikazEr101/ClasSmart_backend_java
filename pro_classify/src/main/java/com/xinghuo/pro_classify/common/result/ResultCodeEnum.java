package com.xinghuo.pro_classify.common.result;

import lombok.Getter;

/**
 * HTTP响应码与响应信息组合枚举
 */
@Getter
public enum ResultCodeEnum {
    SUCCESS(200, "success"),
    ;

    private final Integer code;

    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
