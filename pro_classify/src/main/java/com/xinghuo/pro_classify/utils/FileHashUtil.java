package com.xinghuo.pro_classify.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class FileHashUtil {
    public static String calculateMD5(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            return DigestUtils.md5Hex(inputStream);
        }
    }

    public static String calculateMD5ByStream(InputStream inputStream) throws IOException {
        return DigestUtils.md5Hex(inputStream);
    }
}
