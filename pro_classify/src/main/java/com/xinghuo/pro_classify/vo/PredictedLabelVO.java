package com.xinghuo.pro_classify.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "模型预测结果的视图对象")
@Builder
@Data
public class PredictedLabelVO {
    @Schema(description = "预测结果")
    private String predictedLabel;
    @Schema(description = "分类的图片id")
    private Long imageId;
}
