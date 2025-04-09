package com.xinghuo.pro_classify.service;

import com.xinghuo.pro_classify.dto.response.PredictedLabelResponseDTO;
import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface LitterImageService {
    PredictedLabelResponseDTO uploadLitterImage(MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
    void addFeedbackToImage(String feedback, Long imageId);
    void removeUselessImage() throws Exception;
}
