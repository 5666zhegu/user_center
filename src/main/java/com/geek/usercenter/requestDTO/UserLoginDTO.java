package com.geek.usercenter.requestDTO;

import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

@Data
public class UserLoginDTO implements Serializable {

    private static final long serialVersionUID = -6429212048980737107L;

    private String userAccount;

    private String userPassword;

}
