package com.geek.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.geek.usercenter.common.BaseResponse;
import com.geek.usercenter.common.ErrorCode;
import com.geek.usercenter.common.ResultUtils;
import com.geek.usercenter.entity.User;
import com.geek.usercenter.exception.BusinessException;
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

@RequestMapping("/admin/user")
@RestController
@Slf4j
public class AdminUserController {
    @Resource
    private UserService userService;
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String userName, HttpServletRequest request){
        boolean admin = isAdmin(request);
        if(!admin){
            throw new BusinessException(ErrorCode.NO_AUTH,"未经授权，不能执行");
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
    public BaseResponse<Boolean> deleteUser(@RequestParam Long id, HttpServletRequest request){
        boolean admin = isAdmin(request);
        if(!admin){
            throw new BusinessException(ErrorCode.NO_AUTH,"未授权，不能执行");
        }
        if(id <= 0 || id == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入数据异常");
        }
        return ResultUtils.success(userService.removeById(id));
    }

    private boolean isAdmin(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if(user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未查询到登陆状态，登陆状态为空");
        }
        Long id = user.getId();
        if(userService.query().eq("id", id).eq("userStatus",1).count() > 0){
            throw new BusinessException(ErrorCode.STATUS_ERROR,"该管理员被封禁");
        }
        Integer userRole = user.getUserRole();
        if(userRole != ADMIN_ROLE){
            return false;
        }
        return true;

    }
}
