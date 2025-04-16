package com.xinghuo.pro_classify.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "向python后端发送复训练数据响应数据")
public class RetrainingResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "响应状态码, 200是ok, 500是error")
    private Integer code;
}
