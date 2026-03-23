package com.geek.usercenter.requestDTO;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserUpdateDTO implements Serializable {

    private static final long serialVersionUID = 2267187641766104002L;

    private String phone;
    private String userName;
    private String email;
    private Integer gender;
    private String avatarUrl;
}
