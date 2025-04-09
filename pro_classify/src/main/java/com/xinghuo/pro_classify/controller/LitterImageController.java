package com.xinghuo.pro_classify.controller;

import com.xinghuo.pro_classify.common.Result;
import com.xinghuo.pro_classify.service.LitterImageService;
import com.xinghuo.pro_classify.dto.request.FeedbackRequestDTO;
import com.xinghuo.pro_classify.dto.response.PredictedLabelResponseDTO;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
    private final LitterImageService litterImageService;

    public LitterImageController(LitterImageService litterImageService) {
        this.litterImageService = litterImageService;
    }

    @Operation(summary = "上传垃圾图片得到分类信息")
    @PostMapping("/upload")
    public Result<PredictedLabelResponseDTO> upload(@RequestBody MultipartFile litterImage) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        log.info("upload image...");

        PredictedLabelResponseDTO predictedLabelResponseDTO = litterImageService.uploadLitterImage(litterImage);

        log.info("upload image complete.");

        return Result.ok(predictedLabelResponseDTO);
    }


    @Operation(summary = "用户反馈")
    @PostMapping("/feedback")
    public Result<?> feedback(@RequestBody @Valid FeedbackRequestDTO feedbackRequestDTO) {
        log.info("update feedback to image {} ...", feedbackRequestDTO.getImageId());

        litterImageService.addFeedbackToImage(feedbackRequestDTO.getFeedbackLabel(), feedbackRequestDTO.getImageId());

        log.info("update feedback to image {} complete.", feedbackRequestDTO.getImageId());

        return Result.ok();
    }
}
