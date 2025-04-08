package com.xinghuo.pro_classify.mapper;

import com.xinghuo.pro_classify.pojo.LitterImage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 垃圾图像Mapper层
 */
public interface LitterImageMapper {
    public LitterImage getLitterImageById(@Param("id") Long id);
    public List<LitterImage> getLitterImageList();
    public void deleteLitterImageById(@Param("id") Long id);
    public void saveLitterImage(@Param("litterImage") LitterImage litterImage);
    public void updateLitterImage(@Param("litterImage") LitterImage litterImage);
}
