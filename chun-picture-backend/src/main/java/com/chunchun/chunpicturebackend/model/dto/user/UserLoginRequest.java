package com.chunchun.chunpicturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3782072342243996773L;

    private String userAccount;

    private String userPassword;
}
