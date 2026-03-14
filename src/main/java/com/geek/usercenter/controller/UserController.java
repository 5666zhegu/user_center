package com.geek.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.geek.usercenter.common.BaseResponse;
import com.geek.usercenter.common.ErrorCode;
import com.geek.usercenter.common.ResultUtils;
import com.geek.usercenter.entity.User;
import com.geek.usercenter.exception.BusinessException;
import com.geek.usercenter.requestDTO.UserLoginDTO;
import com.geek.usercenter.requestDTO.UserRegisterDTO;
import com.geek.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterDTO userRegisterDTO){
        if(userRegisterDTO == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入数据为空");
        }
        String userAccount = userRegisterDTO.getUserAccount();
        String userPassword = userRegisterDTO.getUserPassword();
        String checkPassword = userRegisterDTO.getCheckPassword();
        String planetCode = userRegisterDTO.getPlanetCode();
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword,planetCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入数据为空");
        }

        long id = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        return ResultUtils.success(id);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrent(HttpServletRequest request){
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) attribute;
        if(currentUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"没有找到登陆状态");
        }
        Long id = currentUser.getId();
        //TODO 检查该账号的状态是否为异常
        User user = userService.getById(id);
        User safeUser = userService.getSafeUser(user);
        return ResultUtils.success(safeUser);

    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginDTO userLoginDTO,HttpServletRequest request){
        if(userLoginDTO == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入数据为空");
        }
        String userAccount = userLoginDTO.getUserAccount();
        String userPassword = userLoginDTO.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入数据为空");
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("logout")
    public BaseResponse<Integer> logout(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"传入请求为空");
        }
        int logout = userService.logout(request);
        return ResultUtils.success(logout);
    }

    @PostMapping("/search")
    public BaseResponse<List<User>> searchUsers(String userName, HttpServletRequest request){
        boolean admin = isAdmin(request);
        if(!admin){
            throw new BusinessException(ErrorCode.NO_AUTH,"未授权，不能执行");
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
        return ResultUtils.success(safeList);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request){
        boolean admin = isAdmin(request);
        if(!admin){
            throw new BusinessException(ErrorCode.NO_AUTH,"未授权，不能执行");
        }
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入数据异常");
        }
        return ResultUtils.success(userService.removeById(id));
    }


    private boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null || user.getUserRole() != ADMIN_ROLE) {
            throw new BusinessException(ErrorCode.NO_AUTH,"未授权，不能执行");
        }
        return true;
    }
}
