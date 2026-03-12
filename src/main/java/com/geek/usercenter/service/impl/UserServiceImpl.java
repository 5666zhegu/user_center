package com.geek.usercenter.service.impl;
import java.util.Date;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geek.usercenter.entity.User;
import com.geek.usercenter.service.UserService;
import com.geek.usercenter.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.regex.Pattern;

/**
 * @author Prosper
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2026-03-11 21:25:50
 */

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService{



    

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


        String SALT = "geek";
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
}