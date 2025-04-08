package com.xinghuo.pro_classify.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PredictedLabelVO {
    private String predictedLabel;
    private Long imageId;
}
