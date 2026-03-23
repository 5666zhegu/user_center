package com.geek.usercenter.common;

public class ResultUtils {

    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0, data,"ok","Code run success");
    }

    public static <T> BaseResponse<T> success(){
        return new BaseResponse<>(0,"ok","Code run success");
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode,String description){
        return new BaseResponse<>(errorCode,description);
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode,String message, String description){
        return new BaseResponse<>(errorCode.getCode(),message,description);
    }

    public static <T> BaseResponse<T> error(int code,String message,String description ){
        return new BaseResponse<>(code,message,description);
    }
}
