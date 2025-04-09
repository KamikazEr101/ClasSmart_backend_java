package com.xinghuo.pro_classify.mapper;

import com.xinghuo.pro_classify.pojo.LitterImage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 垃圾图像Mapper层
 */
public interface LitterImageMapper {
    LitterImage getLitterImageById(@Param("id") Long id);
    List<LitterImage> getLitterImageList();
    List<LitterImage> getUselessLitterImageList(@Param("hourCount") Long hourCount);
    void deleteLitterImageById(@Param("id") Long id);
    void deleteBatchLitterImage(@Param("litterImageList") List<LitterImage> litterImageList);
    void saveLitterImage(@Param("litterImage") LitterImage litterImage);
    void updateLitterImage(@Param("litterImage") LitterImage litterImage);
}
