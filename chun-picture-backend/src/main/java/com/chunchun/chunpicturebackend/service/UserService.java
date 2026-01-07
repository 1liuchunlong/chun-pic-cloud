package com.chunchun.chunpicturebackend.service;

import cn.hutool.http.server.HttpServerRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chunchun.chunpicturebackend.model.dto.user.UserQueryRequest;
import com.chunchun.chunpicturebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chunchun.chunpicturebackend.model.vo.LoginUserVO;
import com.chunchun.chunpicturebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 24557
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-06-22 09:40:40
 */
public interface UserService extends IService<User> {

    long userRegister(String userAccount, String userPassword,
                      String checkPassword);

    String getEncryptPassword(String userPassword);

    /**
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request      需要session
     * @return 返回脱敏后的用户信息 LoginUserVO
     */
    LoginUserVO userLogin(String userAccount, String userPassword,
                          HttpServletRequest request);

    /**
     * 获得脱敏后登录用户信息
     *
     * @param user 用户完整信息
     * @return 脱敏后信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获得脱敏后用户信息
     *
     * @param user 用户完整信息
     * @return 脱敏后信息
     */
    UserVO getUserVO(User user);


    /**
     * 获得脱敏后用户信息
     *
     * @param userList 用户完整信息
     * @return 脱敏后信息
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 获取当前登录用户
     *
     * @param request 从session 中获取
     * @return 返回User
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request 从session 中移除当前的登录态即可
     * @return 返回注销结果
     */
    boolean userLogout(HttpServletRequest request);

    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 判断是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);


}
