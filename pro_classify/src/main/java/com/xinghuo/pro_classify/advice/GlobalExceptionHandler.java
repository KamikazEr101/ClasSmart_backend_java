package com.xinghuo.pro_classify.advice;

import com.xinghuo.pro_classify.common.result.Result;
import com.xinghuo.pro_classify.exception.BizException;
import com.xinghuo.pro_classify.exception.BizExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Throwable.class)
    public Result<?> handleThrowable(Throwable e) {
        log.error(e.getMessage(), e);
        return Result.fail(BizExceptionEnum.SERVER_INTERNAL_ERROR);
    }

    @ExceptionHandler(BizException.class)
    public Result<?> handleBizException(BizException e) {
        log.error("{}{}", e.getCode().toString(), e.getMsg(), e);
        return Result.fail(e.getCode(), e.getMsg());
    }
}
