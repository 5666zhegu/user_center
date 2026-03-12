package com.geek.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geek.usercenter.entity.User;
import com.geek.usercenter.service.UserService;
import com.geek.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

import static constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author Prosper
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2026-03-11 21:25:50
 */

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService{

    private static final String SALT = "geek";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)){
            return -1;
        }
        if(userAccount.length() < 4){
            return -1;
        }
        if(userPassword.length() < 8 || checkPassword.length() < 8){
            return -1;
        }

        if(!userPassword.equals(checkPassword)){
            return -1;
        }

        String validPattern = "^[a-zA-Z0-9]+$";
        if (!userAccount.matches(validPattern)) {
            return -1;
        }

        if(query().eq("userAccount", userAccount).count() > 0){
            return -1;
        }

        String newUserPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(newUserPassword);
        boolean save = this.save(user);
        if(!save){
            return -1;
        }
        return user.getId();



    }

   @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if(StringUtils.isAnyBlank(userAccount, userPassword)){
            return null;
        }
        if(userAccount.length() < 4){
            return null;
        }
        if(userPassword.length() < 8){
            return null;
        }

        String validPattern = "^[a-zA-Z0-9]+$";
        if (!userAccount.matches(validPattern)) {
            return null;
        }

        String newUserPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        User user = query().eq("userAccount", userAccount).eq("userPassword", newUserPassword).one();
        if(user == null){
            log.info("user login failed,userAccount can not match userPassword");
            return null ;
        }
       User safeUser = getSafeUser(user);


       request.getSession().setAttribute(USER_LOGIN_STATE, safeUser);
        return safeUser;
    }

     /**
     * 获取脱敏用户
     * @param user
     * @return
     */
    @Override
    public User getSafeUser(User user){
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUserName(user.getUserName());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setGender(user.getGender());
        safeUser.setAvatarUrl(user.getAvatarUrl());
        safeUser.setEmail(user.getEmail());
        safeUser.setPhone(user.getPhone());
        safeUser.setStatus(user.getStatus());
        safeUser.setUserRole(user.getUserRole());
        safeUser.setCreateTime(user.getCreateTime());
        return safeUser;
    }
}