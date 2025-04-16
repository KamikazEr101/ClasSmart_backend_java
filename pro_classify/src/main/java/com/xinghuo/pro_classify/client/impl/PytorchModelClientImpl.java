package com.xinghuo.pro_classify.client.impl;

import com.xinghuo.pro_classify.client.PytorchModelClient;
import com.xinghuo.pro_classify.dto.response.ImageClassifyResponseDTO;
import com.xinghuo.pro_classify.dto.response.RetrainingResponseDTO;
import com.xinghuo.pro_classify.properties.PythonBackendsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class PytorchModelClientImpl implements PytorchModelClient {
    private final RestTemplate restTemplate;
    private final PythonBackendsProperties properties;

    @Autowired
    public PytorchModelClientImpl(RestTemplate restTemplate, PythonBackendsProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public ImageClassifyResponseDTO analyzeImage(MultipartFile file) throws IOException {
        log.info("向python服务器上传图片");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<ImageClassifyResponseDTO> response =
                restTemplate.postForEntity(
                        properties.getEndPoint() + properties.getClassifyPath(),
                        requestEntity,
                        ImageClassifyResponseDTO.class
                );

        log.info("成功向python服务器上传图片, 成功得到响应");
        return response.getBody();
    }

    @Override
    public RetrainingResponseDTO sendRetrainingDataset(File file, Integer count) throws IOException {
        log.info("向python服务器上传复训练训练集");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));
        body.add("count", count);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<RetrainingResponseDTO> response =
                restTemplate.postForEntity(
                        properties.getEndPoint() + properties.getRetrainingPath(),
                        requestEntity,
                        RetrainingResponseDTO.class
                );

        log.info("成功向python服务器上传复训练训练集, 成功得到响应");

        return response.getBody();
    }
}