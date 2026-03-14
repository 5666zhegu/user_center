package com.geek.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.geek.usercenter.entity.User;
import com.geek.usercenter.requestDTO.UserLoginDTO;
import com.geek.usercenter.requestDTO.UserRegisterDTO;
import com.geek.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static constant.UserConstant.ADMIN_ROLE;
import static constant.UserConstant.USER_LOGIN_STATE;

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

    @GetMapping("/current")
    public User getCurrent(HttpServletRequest request){
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) attribute;
        if(currentUser == null){
            return null;
        }
        Long id = currentUser.getId();
        //TODO 检查该账号的状态是否为异常
        User user = userService.getById(id);
        return userService.getSafeUser(user);

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

    @PostMapping("/search")
    public List<User> searchUsers(String userName, HttpServletRequest request){
        boolean admin = isAdmin(request);
        if(!admin){
            return new ArrayList<>();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(userName)){
            queryWrapper.like("userName", userName);
        }
        List<User> list = userService.list(queryWrapper);
        List<User> safeList = list.stream().map(user -> {
                    return userService.getSafeUser(user);
                }
        ).collect(Collectors.toList());
        return safeList;
    }

    @PostMapping("/delete")
    public boolean deleteUser(@RequestBody long id,HttpServletRequest request){
        boolean admin = isAdmin(request);
        if(!admin){
            return false;
        }
        if(id <= 0){
            return false;
        }
        return userService.removeById(id);
    }


    private boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null || user.getUserRole() != ADMIN_ROLE) {
            return false;
        }
        return true;
    }
}
