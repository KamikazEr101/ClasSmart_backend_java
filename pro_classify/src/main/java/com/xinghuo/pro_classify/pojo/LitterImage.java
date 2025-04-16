package com.xinghuo.pro_classify.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 垃圾实体类
 */
@Schema(description = "垃圾实体类")
@Data
@Builder
public class LitterImage implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "垃圾id")
    private Long id;
    @Schema(description = "垃圾图片文件类型")
    private String contentType;
    @Schema(description = "垃圾图片存储url")
    private String url;
    @Schema(description = "模型预测结果, 小类型")
    private String predictedLabel;
    @Schema(description = "用户反馈结果")
    private String feedbackLabel;
    @Schema(description = "上传时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime uploadTime;
    @Schema(description = "模型预测结果, 大类型, 如可回收垃圾, ")
    private Integer hugeType;
}
