package com.xinghuo.pro_classify.service.impl;

import com.xinghuo.pro_classify.client.PytorchModelClient;
import com.xinghuo.pro_classify.dto.response.ImageClassifyResponseDTO;
import com.xinghuo.pro_classify.service.ImageProcessingService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageProcessingServiceImpl implements ImageProcessingService {
    private final PytorchModelClient pytorchModelClient;

    public ImageProcessingServiceImpl(PytorchModelClient pytorchModelClient) {
        this.pytorchModelClient = pytorchModelClient;
    }

    /**
     * 调用python后端得到图片分类信息
     * @param file 图片文件
     * @return 图片分类响应实体
     */
    @Override
    public ImageClassifyResponseDTO getImageClassify(MultipartFile file) throws IOException {
        return pytorchModelClient.analyzeImage(file);
    }
}
