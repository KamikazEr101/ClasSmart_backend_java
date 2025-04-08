package com.xinghuo.pro_classify.controller;

import com.xinghuo.pro_classify.common.result.Result;
import com.xinghuo.pro_classify.service.LitterImageService;
import com.xinghuo.pro_classify.vo.PredictedLabelVO;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Tag(name = "垃圾分类管理", description = "垃圾图片分类与反馈")
@Slf4j
@RestController
@RequestMapping("/api/litter")
public class LitterImageController {
    @Autowired
    private LitterImageService litterImageService;

    @Operation(summary = "上传垃圾图片得到分类信息")
    @Parameters({
            @Parameter(name = "litterImage", description = "图片文件", required = true)
    })
    @PostMapping("/upload")
    public Result<PredictedLabelVO> upload(@RequestBody MultipartFile litterImage) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        log.info("upload image...");

        PredictedLabelVO predictedLabelVo = litterImageService.uploadLitterImage(litterImage);

        log.info("upload image complete.");

        return Result.ok(predictedLabelVo);
    }

    @Operation(summary = "用户反馈")
    @Parameters({
            @Parameter(name = "imageId", description = "反馈的图片id", required = true),
            @Parameter(name = "feedbackLabel", description = "反馈的分类信息", required = true),
    })
    @GetMapping("/feedback")
    public Result<?> feedback(@RequestParam Long imageId, @RequestParam String feedbackLabel) {
        log.info("update feedback to image {} ...", imageId);

        litterImageService.addFeedbackToImage(feedbackLabel, imageId);

        log.info("update feedback to image {} complete.", imageId);

        return Result.ok();
    }
}
