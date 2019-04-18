package com.xmcc.springboot_vxzf.exception;

import com.xmcc.springboot_vxzf.common.ResultEnums;


public class CustomException extends RuntimeException {

    private int code;

    public CustomException(int code,String message){
        super(message);
        this.code=code;
    }


    public CustomException(String message){
        this(ResultEnums.FAIL.getCode(),message);
    }
}
