package com.xinghuo.pro_classify.utils;

import com.xinghuo.pro_classify.constants.ContentTypeConstants;
import com.xinghuo.pro_classify.constants.FileConstants;
import com.xinghuo.pro_classify.pojo.LitterImage;
import com.xinghuo.pro_classify.service.FileService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    public static File zipToDataset(List<LitterImage> feedbackImageList, FileService fileService) throws Exception{
        Map<String, List<LitterImage>> groupedByLabel = feedbackImageList.stream()
                .collect(Collectors.groupingBy(LitterImage::getFeedbackLabel));

        // 打包为zip
        File zipFile = new File(System.getProperty("java.io.tmpdir"), FileConstants.ZIP_DATASET_NAME);
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (Map.Entry<String, List<LitterImage>> entry : groupedByLabel.entrySet()) {
                String feedbackLabel = entry.getKey();
                List<LitterImage> images = entry.getValue();

                ZipEntry folderEntry = new ZipEntry(feedbackLabel + "/");
                zipOutputStream.putNextEntry(folderEntry);
                zipOutputStream.closeEntry();

                for (int i = 0; i < images.size(); i++) {
                    LitterImage litterImage = images.get(i);
                    String url = litterImage.getUrl();
                    String fileSuffix = "." + litterImage.getContentType().substring(ContentTypeConstants.CONTENT_TYPE_PREFIX.length());
                    String fileName = (i + 1) + fileSuffix; // 文件名从1开始，例如1.jpg
                    String zipEntryName = feedbackLabel + "/" + fileName; // ZIP中的完整路径

                    ZipEntry zipEntry = new ZipEntry(zipEntryName);
                    zipOutputStream.putNextEntry(zipEntry);

                    try (InputStream inputStream = fileService.downloadByUrl(url)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            zipOutputStream.write(buffer, 0, bytesRead);
                        }
                    }

                    zipOutputStream.closeEntry();
                }
            }
            zipOutputStream.finish();
        }
        return zipFile;
    }
}
