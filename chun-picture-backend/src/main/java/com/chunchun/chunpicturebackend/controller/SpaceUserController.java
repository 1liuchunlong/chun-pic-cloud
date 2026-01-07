package com.chunchun.chunpicturebackend.controller;

import cn.hutool.core.util.ObjUtil;


import com.chunchun.chunpicturebackend.common.BaseResponse;
import com.chunchun.chunpicturebackend.common.DeleteRequest;
import com.chunchun.chunpicturebackend.common.ResultUtils;
import com.chunchun.chunpicturebackend.exception.ErrorCode;
import com.chunchun.chunpicturebackend.exception.ThrowUtils;
import com.chunchun.chunpicturebackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.chunchun.chunpicturebackend.model.dto.spaceuser.SpaceUserEditRequest;
import com.chunchun.chunpicturebackend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.chunchun.chunpicturebackend.model.entity.SpaceUser;
import com.chunchun.chunpicturebackend.model.entity.User;
import com.chunchun.chunpicturebackend.model.vo.SpaceUserVO;
import com.chunchun.chunpicturebackend.service.SpaceUserService;
import com.chunchun.chunpicturebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RequestMapping("/spaceUser")
@RestController
public class SpaceUserController {

    @Resource
    private SpaceUserService spaceUserService;

    @Resource
    private UserService userService;

    /**
     * 添加成员到空间
     *
     * @param spaceUserAddRequest 请求类
     * @param request             request
     * @return id
     */
    @PostMapping("/add")
    public BaseResponse<Long> addSpaceUser(@RequestBody SpaceUserAddRequest spaceUserAddRequest,
                                           HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUserAddRequest == null,
                ErrorCode.OPERATION_ERROR);
        long id = spaceUserService.addSpaceUser(spaceUserAddRequest);
        return ResultUtils.success(id);
    }

    /**
     * 从空间中移除成员
     *
     * @param deleteRequest 删除请求类
     * @param request       请求
     * @return boolean
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSpaceUser(@RequestBody DeleteRequest deleteRequest,
                                                 HttpServletRequest request) {
        // 参数校验
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);

        long id = deleteRequest.getId();
        // 判断是否存在
        SpaceUser oldSpaceUser = spaceUserService.getById(id);
        ThrowUtils.throwIf(oldSpaceUser == null, ErrorCode.NOT_FOUND_ERROR);

        // 操作数据库
        boolean result = spaceUserService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 查询某个成员在某个空间的信息
     *
     * @param spaceUserQueryRequest 请求类
     * @return 空间成员类
     */
    @PostMapping("/get")
    public BaseResponse<SpaceUser> getSpaceUser(@RequestBody SpaceUserQueryRequest spaceUserQueryRequest) {
            // 参数校验
            ThrowUtils.throwIf(spaceUserQueryRequest == null,
                            ErrorCode.PARAMS_ERROR);
            Long spaceId = spaceUserQueryRequest.getSpaceId();
            Long userId = spaceUserQueryRequest.getUserId();
            ThrowUtils.throwIf(ObjUtil.hasEmpty(spaceId, userId),
                            ErrorCode.PARAMS_ERROR);

            // 查询数据库
            SpaceUser spaceUser = spaceUserService.getOne(spaceUserService.getQueryWrapper(spaceUserQueryRequest));
            ThrowUtils.throwIf(spaceUser == null, ErrorCode.NOT_FOUND_ERROR);
            return ResultUtils.success(spaceUser);
    }
    /**
     * 查询空间成员列表
     * @param spaceUserQueryRequest 请求类
     * @param request               request
     * @return 空间成员列表 VO 类
     */

    @PostMapping("/list")
    public BaseResponse<List<SpaceUserVO>> listSpaceUser(@RequestBody SpaceUserQueryRequest spaceUserQueryRequest,
                    HttpServletRequest request) {
            ThrowUtils.throwIf(spaceUserQueryRequest == null, ErrorCode.PARAMS_ERROR);
            List<SpaceUser> spaceUserList = spaceUserService
                            .list(spaceUserService.getQueryWrapper(spaceUserQueryRequest));
            return ResultUtils.success(spaceUserService.getSpaceUserVOList(spaceUserList));
    }
    
    /**
     * 编辑空间成员
     * @param spaceUserEditRequest 请求类
     * @param request              request
     * @return boolean
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editSpaceUser(@RequestBody SpaceUserEditRequest spaceUserEditRequest,
                    HttpServletRequest request) {

            ThrowUtils.throwIf(spaceUserEditRequest == null || spaceUserEditRequest.getId() <= 0,
                            ErrorCode.PARAMS_ERROR);
            // 将实体类和 DTO 进行转换
            SpaceUser spaceUser = new SpaceUser();
            BeanUtils.copyProperties(spaceUserEditRequest, spaceUser);
            // 数据校验
            spaceUserService.validSpaceUser(spaceUser, false);
            // 判断是否存在
            long id = spaceUserEditRequest.getId();
            SpaceUser oldSpaceUser = spaceUserService.getById(id);
            ThrowUtils.throwIf(oldSpaceUser == null, ErrorCode.NOT_FOUND_ERROR);
            // 操作数据库
            boolean result = spaceUserService.updateById(spaceUser);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            return ResultUtils.success(true);
    }
    
    /**
     * 查询我的团队空间成员列表
     * @param request request
     * @return 空间成员列表 VO 类
     */
    @PostMapping("/list/my")
    public BaseResponse<List<SpaceUserVO>> listMyTeamSpaceUsers(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        SpaceUserQueryRequest spaceUserQueryRequest = new SpaceUserQueryRequest();
        spaceUserQueryRequest.setUserId(loginUser.getId());
        List<SpaceUser> spaceUserList = spaceUserService.list(spaceUserService.getQueryWrapper(spaceUserQueryRequest));
        return ResultUtils.success(spaceUserService.getSpaceUserVOList(spaceUserList));
    }

}
