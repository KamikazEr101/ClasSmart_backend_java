package com.xinghuo.pro_classify;

import com.xinghuo.pro_classify.mapper.LitterImageMapper;
import com.xinghuo.pro_classify.pojo.LitterImage;
import com.xinghuo.pro_classify.service.FileService;
import com.xinghuo.pro_classify.service.LitterImageService;
import io.minio.errors.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ProClassifyApplicationTests {
    @Autowired
    private LitterImageMapper litterImageMapper;
    @Autowired
    private FileService fileService;
    @Autowired
    private LitterImageService litterImageService;

    @Test
    void contextLoads() {
    }


    @Test
    void testCRUD() {
        litterImageMapper.getLitterImageList().forEach(System.out::println);
    }

    @Test
    void testBatchDelete() {
        List<LitterImage> litterImages = new ArrayList<>();
        for (int i=0;i<10;i++) {
            litterImages.add(
                    LitterImage.builder().id(77L+i).build()
            );
        }

        litterImageMapper.deleteBatchLitterImage(litterImages);
    }

    @Test
    void testGetUselessLitterImageList() {
        litterImageMapper.getUselessLitterImageList(2L).forEach(System.out::println);
    }

    @Test
    void testDeleteFromMinio() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        fileService.deleteByUrl("http://192.168.230.101:9000/litter/2025-04-08/0c28b3c5-fddd-4338-b28f-a8984e672edd-011.jpg");
    }

    @Test
    void testDeleteUseless() throws Exception {
        litterImageService.removeUselessImage();
    }

}
