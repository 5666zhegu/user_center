package com.geek.usercenter.requestDTO;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserUpdatePasswordDTO implements Serializable {

    private static final long serialVersionUID = -4531756930979306284L;

    private String oldPassword;
    private String newPassword;
    private String checkPassword;
}
