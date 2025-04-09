package com.xinghuo.pro_classify.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "调用python接口的相应实体")
public class ImageClassifyResponseDTO {
    @Schema(description = "分类")
    private String type;
    @Schema(description = "是否错误")
    private Integer error;
    @Schema(description = "总体类型")
    private Integer hugeType;
}
