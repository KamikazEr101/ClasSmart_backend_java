package com.xinghuo.pro_classify.client;

import com.xinghuo.pro_classify.dto.response.ImageClassifyResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PytorchModelClient {
    ImageClassifyResponseDTO analyzeImage(MultipartFile file) throws IOException;
}
