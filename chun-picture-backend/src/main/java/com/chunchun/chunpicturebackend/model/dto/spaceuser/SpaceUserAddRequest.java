package com.chunchun.chunpicturebackend.model.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

/**
 * 添加成员 请求类
 */
@Data
public class SpaceUserAddRequest implements Serializable {

    /**
     * 空间id
     */
    private Long spaceId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 空间角色 viewer/editor/admin
     */
    private String spaceRole;

    private static final long serialVersionUID = 1L;
}
