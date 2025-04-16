package com.xinghuo.pro_classify.service.impl;

import com.xinghuo.pro_classify.client.PytorchModelClient;
import com.xinghuo.pro_classify.constants.ContentTypeConstants;
import com.xinghuo.pro_classify.constants.RedisConstants;
import com.xinghuo.pro_classify.dto.response.ImageClassifyResponseDTO;
import com.xinghuo.pro_classify.dto.response.PredictedLabelResponseDTO;
import com.xinghuo.pro_classify.dto.response.RetrainingResponseDTO;
import com.xinghuo.pro_classify.exception.BizException;
import com.xinghuo.pro_classify.exception.BizExceptionEnum;
import com.xinghuo.pro_classify.mapper.LitterImageMapper;
import com.xinghuo.pro_classify.pojo.LitterImage;
import com.xinghuo.pro_classify.service.FileService;
import com.xinghuo.pro_classify.service.ImageProcessingService;
import com.xinghuo.pro_classify.service.LitterImageService;
import com.xinghuo.pro_classify.utils.FileHashUtil;
import com.xinghuo.pro_classify.utils.ZipUtil;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
@Service
public class LitterImageServiceImpl implements LitterImageService {
    private final LitterImageMapper litterImageMapper;
    private final ImageProcessingService imageProcessingService;
    private final FileService fileService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PytorchModelClient pytorchModelClient;

    public LitterImageServiceImpl(LitterImageMapper litterImageMapper, ImageProcessingService imageProcessingService, FileService fileService, RedisTemplate<String, Object> redisTemplate, PytorchModelClient pytorchModelClient) {
        this.litterImageMapper = litterImageMapper;
        this.imageProcessingService = imageProcessingService;
        this.fileService = fileService;
        this.redisTemplate = redisTemplate;
        this.pytorchModelClient = pytorchModelClient;
    }

    /**
     * @param file 图片文件
     * @return 模型预测结果
     */
    @Transactional
    @Override
    public PredictedLabelResponseDTO uploadLitterImage(MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (file == null || file.isEmpty()) throw new BizException(BizExceptionEnum.FILE_IS_EMPTY);
        if (file.getContentType() == null || !file.getContentType().startsWith(ContentTypeConstants.CONTENT_TYPE_PREFIX)) {
            throw new BizException(BizExceptionEnum.FILE_IS_ILLEGAL);
        }

        long fileSize = file.getSize();
        if (fileSize > 1024L * 1024L * 50) {
            throw new BizException(BizExceptionEnum.MAX_SIZE_OVER_LIMIT);
        }

        // 计算md5哈希值
        String fileHash = FileHashUtil.calculateMD5(file);

        // 检查redis缓存
        String redisKey = RedisConstants.LITTER_CLASSIFY_KEY_PREFIX + fileHash;
        PredictedLabelResponseDTO cacheResult = (PredictedLabelResponseDTO)redisTemplate.opsForValue().get(redisKey);
        if (cacheResult != null) {
            // 命中缓存, 直接返回
            log.info("图片成功命中缓存!");
            return cacheResult;
        }

        ImageClassifyResponseDTO responseDTO = imageProcessingService.getImageClassify(file);
        if (responseDTO.getError() == 1) {
            throw new BizException(BizExceptionEnum.MODEL_PROCESS_ERROR);
        }
        log.info("正在将图片存入Minio...");
        // 存入minio
        // 恶意图片, 不用于二次训练, 即不存入minio
        String imageUrl = "";
        if (responseDTO.getError() == 0) {
            imageUrl = fileService.upload(file);
        }
        log.info("成功将图片存入Minio");

        LitterImage litterImage = LitterImage.builder().
                contentType(file.getContentType())
                .url(imageUrl)
                .predictedLabel(responseDTO.getType())
                .hugeType(responseDTO.getHugeType())
                .build();

        log.info("正在将图片存入数据库...");
        // 存入数据库
        // 恶意图片, 不用于二次训练, 即不存入数据库
        if (responseDTO.getError() == 0) {
            litterImageMapper.saveLitterImage(litterImage);
        } else {
            litterImage.setId(null);
        }
        log.info("成功将图片存入数据库");

        PredictedLabelResponseDTO result = PredictedLabelResponseDTO.builder().
                imageId(litterImage.getId()).
                predictedLabel(litterImage.getPredictedLabel()).
                hugeType(litterImage.getHugeType()).
                build();

        // 存入redis
        log.info("将图片上传至redis");
        redisTemplate.opsForValue().set(redisKey, result, RedisConstants.TTL_VALUE, RedisConstants.TTL_UNIT);
        log.info("上传图片成功");
        return result;
    }

    /**
     * 将反馈存入数据库中对应图片的字段中
     *
     * @param feedback 反馈值
     * @param imageId  图片id
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void addFeedbackToImage(String feedback, Long imageId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 恶意图片无主键回显, id为null, 故直接返回
        if (imageId == null) {
            return;
        }
        LitterImage litterImageById = litterImageMapper.getLitterImageById(imageId);
        litterImageById.setFeedbackLabel(feedback);
        // 从缓存中删除预测结果与反馈结果不一致的图片
        if (!feedback.equals(litterImageById.getPredictedLabel())) {
            InputStream inputStream = fileService.downloadByUrl(litterImageById.getUrl());
            String fileHash = FileHashUtil.calculateMD5ByStream(inputStream);
            redisTemplate.delete(RedisConstants.LITTER_CLASSIFY_KEY_PREFIX + fileHash);
        }
        litterImageMapper.updateLitterImage(litterImageById);
    }

    /**
     * 删除无用垃圾图片,
     * 如无predictedLabel,
     * 或者 predictedLabel = feedbackLabel
     * 或者 feedbackLabel = null 并超时(存储时间)
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void removeUselessImage() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<LitterImage> uselessLitterImageList = litterImageMapper.getUselessLitterImageList(3L);
        litterImageMapper.deleteBatchLitterImageByIds(uselessLitterImageList);
        for (LitterImage litterImage : uselessLitterImageList) {
            fileService.deleteByUrl(litterImage.getUrl());
        }
    }

    /**
     * 将数据库中的具有反馈的图片压缩为数据集传给python后端
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void zipDatasetAndRetrain() throws Exception {
        this.removeUselessImage();

        List<LitterImage> feedbackImageList = litterImageMapper.getLitterImageListWithFeedback();
        if (CollectionUtils.isEmpty(feedbackImageList)) {
            return;
        }

        if (feedbackImageList.size() > 100) {
            feedbackImageList = feedbackImageList.subList(0, 100);
        }

        // 统计训练集大小, 用于确定训练epoch
        Integer count = feedbackImageList.size();

        File zipFile = ZipUtil.zipToDataset(feedbackImageList, fileService);

        boolean success = sendDatasetWithRetry(zipFile, count);

        if (success) {
            zipFile.delete();

            litterImageMapper.deleteBatchLitterImageByIds(feedbackImageList);
            for (LitterImage litterImage : feedbackImageList) {
                fileService.deleteByUrl(litterImage.getUrl());
            }
        }
    }

    private boolean sendDatasetWithRetry(File zipFile, Integer count) {

        try {
            RetrainingResponseDTO responseDTO = pytorchModelClient.sendRetrainingDataset(zipFile, count);
            if (responseDTO.getCode() == 200) {
                log.info("成功发送数据集到Python服务");
                return true;
            } else {
                log.warn("发送数据集失败，状态码: {})", responseDTO.getCode());
            }
        } catch (Exception e) {
            log.warn("发送数据集时发生异常: {})", e.getMessage());
        }

        return false;
    }
}
