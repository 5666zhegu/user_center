package com.geek.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.geek.usercenter.entity.User;
import com.geek.usercenter.requestDTO.UserUpdateDTO;
import com.geek.usercenter.requestDTO.UserUpdatePasswordDTO;

import javax.servlet.http.HttpServletRequest;

/**
* @author Prosper
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2026-03-11 21:25:50
*/
public interface UserService extends IService<User> {
    /**
     * 用户登录态的key
     */

    /**
     * 用户注册
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @param planetCode
     * @return
     */
    Long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @return
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取脱敏用户
     * @param originUser
     * @return
     */
    User getSafeUser(User originUser);


    /**
     * 用户注销
     * @param request
     */
    int logout(HttpServletRequest request);

    /**
     * 修改用户信息
     * @param userUpdateDTO
     * @return
     */
    boolean userUpdate(UserUpdateDTO userUpdateDTO,HttpServletRequest request);

    /**
     * 修改用户密码
     * @param userUpdatePasswordDTO
     * @return
     */
    Boolean userUpdatePassword(UserUpdatePasswordDTO userUpdatePasswordDTO, HttpServletRequest request);

    /**
     * 删除用户
     * @param id
     * @return
     */
}
