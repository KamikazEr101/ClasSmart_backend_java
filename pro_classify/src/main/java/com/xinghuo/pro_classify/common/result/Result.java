package com.xinghuo.pro_classify.common.result;

import com.xinghuo.pro_classify.exception.BizExceptionEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "相应结果对象")
@Data
public class Result<T> {
    @Schema(description = "相应状态码")
    private Integer code;
    @Schema(description = "响应信息")
    private String msg;
    @Schema(description = "响应数据")
    private T data;

    private Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    private Result(ResultCodeEnum resultCodeEnum, T data) {
        this.code = resultCodeEnum.getCode();
        this.msg = resultCodeEnum.getMessage();
        this.data = data;
    }

    public static <T> Result<T> ok() {
        return new Result<T>(ResultCodeEnum.SUCCESS, null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(ResultCodeEnum.SUCCESS, data);
    }

    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<T>(code, msg, null);
    }

    public static <T> Result<T> fail(Integer code, String msg, T data) {
        return new Result<T>(code, msg, data);
    }

    public static <T> Result<T> fail(BizExceptionEnum bizExceptionEnum) {
        return fail(bizExceptionEnum.getCode(), bizExceptionEnum.getMsg());
    }

}
