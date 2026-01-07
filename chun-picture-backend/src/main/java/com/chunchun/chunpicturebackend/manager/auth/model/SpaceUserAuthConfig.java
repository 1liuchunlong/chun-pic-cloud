package com.chunchun.chunpicturebackend.manager.auth.model;

import java.util.List;

import lombok.Data;

@Data
public class SpaceUserAuthConfig {
    /**
     * 权限列表
     */
    private List<SpaceUserPermission> permissions;

    /**
     * 角色列表
     */
    private List<SpaceUserRole> roles;
}
