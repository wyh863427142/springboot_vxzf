package com.xmcc.springboot_vxzf.common;

import lombok.Getter;

@Getter
public enum ProductEnums {
    PRODUCT_NOT_ENOUGH(1,"商品库存不足");
    //。。。。。。
    private int code;
    private String meg;
    ProductEnums(int code,String msg){
        this.code=code;
        this.meg=msg;
    }
}
