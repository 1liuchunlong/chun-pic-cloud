package com.chunchun.chunpicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PictureUploadByBatchRequest implements Serializable {

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 名称前缀
     */
    private String namePrefix;

    /**
     * 抓取数量
     */
    private Integer count = 10;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签列表
     */
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}
