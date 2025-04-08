package com.xinghuo.pro_classify.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户反馈的模型
 */
@Data
@Schema(description = "用户反馈的模型")
public class FeedbackVO {
    @Schema(description = "反馈信息")
    @NotBlank
    private String feedbackLabel;
    @Schema(description = "对应的垃圾图片id")
    @NotNull
    private Long imageId;
}
