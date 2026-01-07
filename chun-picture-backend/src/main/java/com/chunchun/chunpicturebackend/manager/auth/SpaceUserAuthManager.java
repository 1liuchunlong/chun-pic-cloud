package com.chunchun.chunpicturebackend.manager.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.chunchun.chunpicturebackend.manager.auth.model.SpaceUserAuthConfig;
import com.chunchun.chunpicturebackend.manager.auth.model.SpaceUserRole;
import com.chunchun.chunpicturebackend.service.SpaceUserService;
import com.chunchun.chunpicturebackend.service.UserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class SpaceUserAuthManager {

    @Resource
    private SpaceUserService spaceUserService;

    @Resource
    private UserService userService;

    public static final SpaceUserAuthConfig SPACE_USER_AUTH_CONFIG;

//   静态代码块只加载一次 类初始化阶段运行
    static {
        String json = ResourceUtil.readUtf8Str("biz/spaceUserAuthConfig.json");
        SPACE_USER_AUTH_CONFIG = JSONUtil.toBean(json, SpaceUserAuthConfig.class);
    }

    /**
     * 根据角色获取权限列表
     */
    public List<String> getPermissionsByRole(String spaceUserRole) {
        // 判空
        if (StrUtil.isBlank(spaceUserRole)) {
            return null;
        }
        // 通过string 拿到配置类
        SpaceUserRole role = SPACE_USER_AUTH_CONFIG.getRoles()
                .stream()
                .filter(r -> spaceUserRole.equals(r.getKey()))
                .findFirst()
                .orElse(null);

        if (role == null) {
            return new ArrayList<>();
        }
        return role.getPermissions();

    }
}
