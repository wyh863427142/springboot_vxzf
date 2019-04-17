package com.xmcc.springboot_vxzf.repository;


import lombok.Data;

@Data
public class ResultResponse<T> {

    private int code;

    private String msg;

    private T data;
}
