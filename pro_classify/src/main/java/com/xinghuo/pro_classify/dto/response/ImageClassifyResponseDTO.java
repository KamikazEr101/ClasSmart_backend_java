package com.xinghuo.pro_classify.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "调用python接口的相应实体")
public class ImageClassifyResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "分类的类型, 如battery, brick, can等等")
    private String type;
    @Schema(description = "是否错误 0为无错, 1为分类失败, 2为图片无意义(模型预测的几种概率都相接近, 没有确定答案, 即为无意义(与垃圾无关)的图片)")
    private Integer error;
    @Schema(description = "总体类型, 可回收物: 0, 有害垃圾:1, 厨余垃圾:2, 其他垃圾:3")
    private Integer hugeType;
}
