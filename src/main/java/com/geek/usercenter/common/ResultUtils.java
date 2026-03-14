package com.geek.usercenter.common;

public class ResultUtils {

    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(1, data,"ok","Code run success");
    }

    public static BaseResponse success(){
        return new BaseResponse(1,"ok","Code run success");
    }

    public static BaseResponse error(ErrorCode errorCode,String description){
        return new BaseResponse(errorCode,description);
    }

    public static BaseResponse error(ErrorCode errorCode,String message, String description){
        return new BaseResponse(errorCode.getCode(),message,description);
    }

    public static BaseResponse error(int code,String message,String description ){
        return new BaseResponse(code,message,description);
    }
}
