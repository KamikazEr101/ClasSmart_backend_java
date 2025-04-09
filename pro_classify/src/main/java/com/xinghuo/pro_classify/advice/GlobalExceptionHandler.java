package com.xinghuo.pro_classify.advice;

import com.xinghuo.pro_classify.common.Result;
import com.xinghuo.pro_classify.exception.BizException;
import com.xinghuo.pro_classify.exception.BizExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handlerValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        Map<String, String> errorsMap = new HashMap<>();
        for (FieldError fieldError : fieldErrors) {
            String field = fieldError.getField();
            String message = fieldError.getDefaultMessage();
            errorsMap.put(field,message);
        }
        return Result.fail(599,"数据校验未通过", errorsMap);
    }
}
