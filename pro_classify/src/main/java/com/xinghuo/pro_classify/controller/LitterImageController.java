package com.xinghuo.pro_classify.controller;

import com.xinghuo.pro_classify.common.result.Result;
import com.xinghuo.pro_classify.service.LitterImageService;
import com.xinghuo.pro_classify.vo.PredictedLabelVO;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@RestController
@RequestMapping("/api/litter")
public class LitterImageController {
    @Autowired
    private LitterImageService litterImageService;

    @PostMapping("/upload")
    public Result<PredictedLabelVO> upload(@RequestBody MultipartFile litterImage) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        log.info("upload image...");

        PredictedLabelVO predictedLabelVo = litterImageService.uploadLitterImage(litterImage);

        log.info("upload image complete.");

        return Result.ok(predictedLabelVo);
    }

    @GetMapping("/feedback")
    public Result<?> feedback(@RequestParam Long imageId, @RequestParam String feedbackLabel) {
        log.info("update feedback to image {} ...", imageId);

        litterImageService.addFeedbackToImage(feedbackLabel, imageId);

        log.info("update feedback to image {} complete.", imageId);

        return Result.ok();
    }
}
