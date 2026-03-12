package com.geek.usercenter.requestDTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterDTO implements Serializable {


    private static final long serialVersionUID = -881138471957541626L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
