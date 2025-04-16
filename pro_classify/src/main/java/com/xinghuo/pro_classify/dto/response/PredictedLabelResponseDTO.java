package com.xinghuo.pro_classify.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "模型预测结果的视图对象")
@Builder
@Data
public class PredictedLabelResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "预测结果")
    @NotBlank
    private String predictedLabel;
    @Schema(description = "分类的图片id")
    @NotNull
    private Long imageId;
    @Schema(description = "预测的大类型结果")
    @NotNull
    private Integer hugeType;
}
