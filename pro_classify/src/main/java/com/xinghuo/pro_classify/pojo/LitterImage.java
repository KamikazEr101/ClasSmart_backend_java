package com.xinghuo.pro_classify.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "垃圾实体类")
@Data
public class LitterImage implements Serializable {
    @Schema(description = "垃圾id")
    private Long id;
    @Schema(description = "垃圾图片文件类型")
    private String contentType;
    @Schema(description = "垃圾图片存储url")
    private String url;
    @Schema(description = "模型预测结果")
    private String predictedLabel;
    @Schema(description = "用户反馈结果")
    private String feedbackLabel;
}
