package com.xmcc.springboot_vxzf.service;

import com.xmcc.springboot_vxzf.common.ResultResponse;
import com.xmcc.springboot_vxzf.entity.ProductInfo;

public interface ProductInfoService {
    ResultResponse queryList();

    ResultResponse<ProductInfo> queryById(String productId);

    void updateProduct(ProductInfo productInfo);
}
