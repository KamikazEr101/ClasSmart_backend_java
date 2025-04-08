package com.xinghuo.pro_classify.controller;

import com.xinghuo.pro_classify.common.result.Result;
import com.xinghuo.pro_classify.service.LitterImageService;
import com.xinghuo.pro_classify.vo.FeedbackVO;
import com.xinghuo.pro_classify.vo.PredictedLabelVO;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 垃圾分类管理Controller层
 */
@Tag(name = "垃圾分类管理", description = "垃圾图片分类与反馈")
@Slf4j
@RestController
@RequestMapping("/api/litter")
public class LitterImageController {
    @Autowired
    private LitterImageService litterImageService;

    @Operation(summary = "上传垃圾图片得到分类信息")
    @PostMapping("/upload")
    public Result<PredictedLabelVO> upload(@RequestBody MultipartFile litterImage) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        log.info("upload image...");

        PredictedLabelVO predictedLabelVo = litterImageService.uploadLitterImage(litterImage);

        log.info("upload image complete.");

        return Result.ok(predictedLabelVo);
    }


    @Operation(summary = "用户反馈")
    @PostMapping("/feedback")
    public Result<?> feedback(@RequestBody @Valid FeedbackVO feedbackVO) {
        log.info("update feedback to image {} ...", feedbackVO.getImageId());

        litterImageService.addFeedbackToImage(feedbackVO.getFeedbackLabel(), feedbackVO.getImageId());

        log.info("update feedback to image {} complete.", feedbackVO.getImageId());

        return Result.ok();
    }
}
