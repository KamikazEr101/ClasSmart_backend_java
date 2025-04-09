package com.xinghuo.pro_classify.service.impl;

import com.xinghuo.pro_classify.dto.response.ImageClassifyResponseDTO;
import com.xinghuo.pro_classify.dto.response.PredictedLabelResponseDTO;
import com.xinghuo.pro_classify.exception.BizException;
import com.xinghuo.pro_classify.exception.BizExceptionEnum;
import com.xinghuo.pro_classify.mapper.LitterImageMapper;
import com.xinghuo.pro_classify.pojo.LitterImage;
import com.xinghuo.pro_classify.service.FileService;
import com.xinghuo.pro_classify.service.ImageProcessingService;
import com.xinghuo.pro_classify.service.LitterImageService;
import io.minio.errors.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class LitterImageServiceImpl implements LitterImageService {
    private final LitterImageMapper litterImageMapper;
    private final ImageProcessingService imageProcessingService;
    private final FileService fileService;

    public LitterImageServiceImpl(LitterImageMapper litterImageMapper, ImageProcessingService imageProcessingService, FileService fileService) {
        this.litterImageMapper = litterImageMapper;
        this.imageProcessingService = imageProcessingService;
        this.fileService = fileService;
    }

    /**
     * @param file 图片文件
     * @return 模型预测结果
     */
    @Transactional
    @Override
    public PredictedLabelResponseDTO uploadLitterImage(MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        ImageClassifyResponseDTO responseDTO = imageProcessingService.getImageClassify(file);
        if (responseDTO.getError() == 1) {
            throw new BizException(BizExceptionEnum.MODEL_PROCESS_ERROR);
        }

        // 存入minio
        String imageUrl = fileService.upload(file);

        LitterImage litterImage = LitterImage.builder().
                contentType(file.getContentType())
                .url(imageUrl)
                .predictedLabel(responseDTO.getType())
                .hugeType(responseDTO.getHugeType())
                .build();

        // 存入数据库
        litterImageMapper.saveLitterImage(litterImage);

        return PredictedLabelResponseDTO.builder().
                imageId(litterImage.getId()).
                predictedLabel(litterImage.getPredictedLabel()).
                hugeType(litterImage.getHugeType()).
                build();
    }

    /**
     * 将反馈存入数据库中对应图片的字段中
     * @param feedback 反馈值
     * @param imageId 图片id
     */
    @Transactional
    @Override
    public void addFeedbackToImage(String feedback, Long imageId) {
        LitterImage litterImageById = litterImageMapper.getLitterImageById(imageId);
        litterImageById.setFeedbackLabel(feedback);
        litterImageMapper.updateLitterImage(litterImageById);
    }

    /**
     * 删除无用垃圾图片,
     * 如无predictedLabel,
     * 或者 predictedLabel = feedbackLabel
     * 或者 feedbackLabel = null 并超时(存储时间)
     */
    @Transactional
    @Override
    public void removeUselessImage() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<LitterImage> uselessLitterImageList = litterImageMapper.getUselessLitterImageList(3L);
        litterImageMapper.deleteBatchLitterImage(uselessLitterImageList);
        for (LitterImage litterImage : uselessLitterImageList) {
            fileService.deleteByUrl(litterImage.getUrl());
        }
    }
}
