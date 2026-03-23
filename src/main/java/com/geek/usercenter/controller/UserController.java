package com.geek.usercenter.controller;
import com.geek.usercenter.common.BaseResponse;
import com.geek.usercenter.common.ErrorCode;
import com.geek.usercenter.common.ResultUtils;
import com.geek.usercenter.entity.User;
import com.geek.usercenter.exception.BusinessException;
import com.geek.usercenter.requestDTO.UserLoginDTO;
import com.geek.usercenter.requestDTO.UserRegisterDTO;
import com.geek.usercenter.requestDTO.UserUpdateDTO;
import com.geek.usercenter.requestDTO.UserUpdatePasswordDTO;
import com.geek.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import static constant.UserConstant.USER_LOGIN_STATE;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    /**
    * 用户注册
    * @param userRegisterDTO
    * @return
    */
   @PostMapping("/register")
   public BaseResponse<Long> userRegister(@RequestBody UserRegisterDTO userRegisterDTO){
       if(userRegisterDTO == null){
           throw new BusinessException(ErrorCode.NULL_ERROR,"传入的注册信息为空");
       }
       String userAccount = userRegisterDTO.getUserAccount();
       String userPassword = userRegisterDTO.getUserPassword();
       String checkPassword = userRegisterDTO.getCheckPassword();
       String planetCode = userRegisterDTO.getPlanetCode();
       if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)){
           throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入的数据为空");
       }
       Long id = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
       return ResultUtils.success(id);
   }


   /**
    * 获得用户的登陆状态
    * @param request
    * @return
    */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if(user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未查询到登陆状态，登陆状态为空");
        }
        Long id = user.getId();
        Integer userStatus = user.getUserStatus();
        if(userStatus != 0){
            throw new BusinessException(ErrorCode.STATUS_ERROR,"");
        }
        User newUser = userService.getById( id);
        User safeUser = userService.getSafeUser(newUser);
        return ResultUtils.success(safeUser);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request) {
        if (userLoginDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "传入的登录信息为空");
        }
        String userAccount = userLoginDTO.getUserAccount();
        String userPassword = userLoginDTO.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "传入的数据为空");
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        return ResultUtils.success(userService.logout(request));

    }






    @PostMapping("/update")
    public BaseResponse<Boolean> userUpdate(@RequestBody UserUpdateDTO userUpdateDTO,HttpServletRequest request){
        if(userUpdateDTO == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"传入的更新信息为空");
        }
        boolean update = userService.userUpdate(userUpdateDTO, request);
        return ResultUtils.success(update);

    }

    @PostMapping("/updatePassword")
    public BaseResponse<Boolean> updatePassword(@RequestBody UserUpdatePasswordDTO userUpdatePasswordDTO,HttpServletRequest request){
        if(userUpdatePasswordDTO == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"传入的更新信息为空");
        }
        Boolean updatePassword = userService.userUpdatePassword(userUpdatePasswordDTO, request);
        return ResultUtils.success(updatePassword);

    }

    @PostMapping("/delete")
    public BaseResponse<Integer> deleteUser(HttpServletRequest request){

        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) attribute;
        if(user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未查询到登陆状态，登陆状态为空");
        }
        Long id = user.getId();
        Integer result = userService.deleteUser(id);
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return ResultUtils.success(result);
    }
}
