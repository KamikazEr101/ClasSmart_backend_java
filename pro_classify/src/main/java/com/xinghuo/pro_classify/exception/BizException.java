package com.xinghuo.pro_classify.exception;

import lombok.Getter;

/**
 * 业务异常类
 */
@Getter
public class BizException extends RuntimeException {
    private final Integer code;
    private final String msg;

    public BizException(BizExceptionEnum bizExceptionEnum) {
        super(bizExceptionEnum.getMsg());
        this.code = bizExceptionEnum.getCode();
        this.msg = bizExceptionEnum.getMsg();
    }
}
