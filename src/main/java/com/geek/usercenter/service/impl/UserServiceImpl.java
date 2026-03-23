package com.geek.usercenter.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geek.usercenter.common.ErrorCode;
import com.geek.usercenter.entity.User;
import com.geek.usercenter.exception.BusinessException;
import com.geek.usercenter.requestDTO.UserUpdateDTO;
import com.geek.usercenter.requestDTO.UserUpdatePasswordDTO;
import com.geek.usercenter.service.UserService;
import com.geek.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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
    public Long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入账号长度过短");
        }
        String vaildPattern = "^[0-9a-zA-Z]+$";
        if(!userAccount.matches(vaildPattern)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入账号参数含有特殊字符");
        }
        if(userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入密码过短");
        }
        if(!checkPassword.equals(userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入的密码不一致");
        }
        if(planetCode.length() > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入的星球编号过长");
        }
        if(query().eq("userAccount", userAccount).eq("isDelete", 0).count() > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"该账号已存在");
        }
        if(query().eq("planetCode", planetCode).eq("isDelete", 0).count() > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"该星球编号已存在");
        }
        String newUserPassword = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(newUserPassword);
        user.setPlanetCode(planetCode);
        boolean save = this.save(user);
        if(!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"注册用户失败，数据库插入异常");
    }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入账号长度过短");
        }
        String vaildPattern = "^[0-9a-zA-Z]+$";
        if(!userAccount.matches(vaildPattern)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入账号参数含有特殊字符");
        }
        if(userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入密码过短");
        }
        String newUserPassword = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());
        User user = query().eq("userAccount",userAccount).eq("userPassword",newUserPassword).one();
        if(user == null){
            log.info("user login failed,userAccount can not match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号或密码错误");
        }
        if(user.getUserStatus() != 0){
            throw new BusinessException(ErrorCode.STATUS_ERROR,"用户被封禁");
        }
        if(user.getIsDelete() != 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"用户被删除");
        }
        User safeUser = this.getSafeUser(user);
        request.getSession().setAttribute(USER_LOGIN_STATE,safeUser);
        return safeUser;
    }


    /**
     * 获取脱敏用户
     * @param user
     * @return
     */
    @Override
    public User getSafeUser(User user){
        if(user == null){
            return null;
        }
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUserName(user.getUserName());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setGender(user.getGender());
        safeUser.setAvatarUrl(user.getAvatarUrl());
        safeUser.setEmail(user.getEmail());
        safeUser.setPhone(user.getPhone());
        safeUser.setUserStatus(user.getUserStatus());
        safeUser.setUserRole(user.getUserRole());
        safeUser.setCreateTime(user.getCreateTime());
        safeUser.setPlanetCode(user.getPlanetCode());
        return safeUser;
    }

    @Override
    public int logout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public boolean userUpdate(UserUpdateDTO userUpdateDTO,HttpServletRequest request) {
        User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"用户未登录" );
        }
        Long id = loginUser.getId();
        String userName = userUpdateDTO.getUserName();
        if(userName.length() < 2 || userName.length() > 32){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户昵称长度不符合要求");
        }
        String email = userUpdateDTO.getEmail();
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"邮箱格式错误");
        }
        String phone = userUpdateDTO.getPhone();
        if (!phone.matches("^\\+?[1-9]\\d{6,14}$")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"手机号格式错误");
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateDTO, user);
        user.setId(id);
        boolean update = this.updateById(user);
        if(!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新用户信息失败");
        }
        return update;
    }

    @Override
    public Boolean userUpdatePassword(UserUpdatePasswordDTO userUpdatePasswordDTO, HttpServletRequest request) {
        String oldPassword = userUpdatePasswordDTO.getOldPassword();
        String newPassword = userUpdatePasswordDTO.getNewPassword();
        String checkPassword = userUpdatePasswordDTO.getCheckPassword();
        if (StringUtils.isAnyBlank(oldPassword, newPassword, checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入参数存在空值");
        }
        if(oldPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入旧密码过短");
        }
        if(newPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入新密码过短");
        }
        if(!newPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入的密码不一致");
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if(user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"用户未登录");
        }
        Long id = user.getId();
        String MD5OldPassword = DigestUtils.md5DigestAsHex((oldPassword + SALT).getBytes());
        User user1 = query().eq("id", id).eq("userPassword", MD5OldPassword).one();
        if (user1 == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"原密码错误");
        }
        String MD5NewPassword = DigestUtils.md5DigestAsHex((newPassword + SALT).getBytes());
        boolean update = update().set("userPassword", MD5NewPassword).eq("id", id).update();
        if(!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新用户密码失败");
        }
        return update;
    }

    @Override
    public Integer deleteUser(Long id) {
        User user = query().eq("id", id).one();
        if(user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
            boolean delete = this.removeById(id);
        if(!delete){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除用户失败");
        }
        return id.intValue();
    }
}