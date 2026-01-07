package com.chunchun.chunpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chunchun.chunpicturebackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.chunchun.chunpicturebackend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.chunchun.chunpicturebackend.model.entity.SpaceUser;
import com.chunchun.chunpicturebackend.model.vo.SpaceUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 24557
 * @description 针对表【space_user(空间用户关联)】的数据库操作Service
 * @createDate 2026-01-02 11:46:29
 */
public interface SpaceUserService extends IService<SpaceUser> {

    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    void validSpaceUser(SpaceUser spaceUser, boolean add);

    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);

    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);
}
