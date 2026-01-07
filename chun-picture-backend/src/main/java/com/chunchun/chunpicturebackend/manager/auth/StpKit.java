package com.chunchun.chunpicturebackend.manager.auth;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;

public class StpKit {

    public static final String SPACE_TYPE = "space";

    /**
     * 默认原生会话对象 未用到
     * */
    public static final StpLogic DEFAULT = StpUtil.stpLogic;

    /**
     * Space会话对象 管理 Space表 的所有账号的登录和权限认证
     */
    public static final StpLogic SPACE = new StpLogic(SPACE_TYPE);
}

