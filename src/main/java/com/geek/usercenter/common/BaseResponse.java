package com.geek.usercenter.common;

import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private String message;

    private T data;

    private String description;

    public BaseResponse(int code, T data, String message,String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code,String message,String description) {
        this(code, null, message, description);
    }

    public BaseResponse(ErrorCode errorCode,String description){
        this(errorCode.getCode(), null, errorCode.getMessage(), description);
    }


}
