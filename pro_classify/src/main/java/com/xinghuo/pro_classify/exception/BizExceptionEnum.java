package com.xinghuo.pro_classify.exception;

import lombok.Getter;

/**
 * 业务异常枚举
 */
@Getter
public enum BizExceptionEnum {
    SERVER_INTERNAL_ERROR(500, "服务器内部错误"),
    MODEL_PROCESS_ERROR(700, "模型生成过程中出现了错误"),
    REQUEST_FREQUENCY_OVER_LIMIT(1001, "服务器繁忙, 请求频率超限"),
    IP_BLOCKED(1002, "检测到异常流量, 封禁该ip一段时间"),
    MAX_SIZE_OVER_LIMIT(1003, "文件大小超限"),
    FILE_IS_EMPTY(1004, "文件为空"),
    FILE_IS_ILLEGAL(1005, "非法文件")
    ;

    private final Integer code;
    private final String msg;
    BizExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
