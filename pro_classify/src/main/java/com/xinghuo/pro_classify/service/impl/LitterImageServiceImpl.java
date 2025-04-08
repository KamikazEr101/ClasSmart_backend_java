package com.xinghuo.pro_classify.service.impl;

import com.xinghuo.pro_classify.mapper.LitterImageMapper;
import com.xinghuo.pro_classify.pojo.LitterImage;
import com.xinghuo.pro_classify.service.FileService;
import com.xinghuo.pro_classify.service.LitterImageService;
import com.xinghuo.pro_classify.vo.PredictedLabelVO;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class LitterImageServiceImpl implements LitterImageService {
    @Autowired
    private LitterImageMapper litterImageMapper;
    @Autowired
    private FileService fileService;

    /**
     *
     * @param file 图片文件
     * @return 模型预测结果
     */
    @Override
    public PredictedLabelVO uploadLitterImage(MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String imageUrl = fileService.upload(file);

        LitterImage litterImage = new LitterImage();
        litterImage.setContentType(file.getContentType());
        litterImage.setUrl(imageUrl);
        //TODO 调用大模型
        // litterImage.setPredictedLabel();
        litterImage.setPredictedLabel("电池");

        // 存入数据库
        litterImageMapper.saveLitterImage(litterImage);

        assert litterImage.getId() != null;

        return PredictedLabelVO.builder().
                imageId(litterImage.getId()).
                predictedLabel(litterImage.getPredictedLabel()).
                build();
    }

    @Override
    public void addFeedbackToImage(String feedback, Long imageId) {
        LitterImage litterImageById = litterImageMapper.getLitterImageById(imageId);
        litterImageById.setFeedbackLabel(feedback);
        litterImageMapper.updateLitterImage(litterImageById);
    }
}
