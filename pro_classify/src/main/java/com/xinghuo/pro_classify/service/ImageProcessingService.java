package com.xinghuo.pro_classify.service;

import com.xinghuo.pro_classify.dto.response.ImageClassifyResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageProcessingService {
    ImageClassifyResponseDTO getImageClassify(MultipartFile file) throws IOException;
}
