package com.xinghuo.pro_classify.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LitterImage implements Serializable {
    private Long id;
    private String contentType;
    private String url;
    private String predictedLabel;
    private String feedbackLabel;
}
