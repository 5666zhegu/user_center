package com.geek.usercenter.controller;

import com.geek.usercenter.entity.User;
import com.geek.usercenter.requestDTO.UserLoginDTO;
import com.geek.usercenter.requestDTO.UserRegisterDTO;
import com.geek.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterDTO userRegisterDTO){
        if(userRegisterDTO == null){
            return null;
        }
        String userAccount = userRegisterDTO.getUserAccount();
        String userPassword = userRegisterDTO.getUserPassword();
        String checkPassword = userRegisterDTO.getCheckPassword();
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)){
            return null;
        }

        long id = userService.userRegister(userAccount, userPassword, checkPassword);
        return id;
    }

    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginDTO userLoginDTO,HttpServletRequest request){
        if(userLoginDTO == null){
            return null;
        }
        String userAccount = userLoginDTO.getUserAccount();
        String userPassword = userLoginDTO.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount, userPassword)){
            return null;
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return user;
    }
}
