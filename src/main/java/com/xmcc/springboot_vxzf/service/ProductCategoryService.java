package com.xmcc.springboot_vxzf.service;

import com.xmcc.springboot_vxzf.common.ResultResponse;
import com.xmcc.springboot_vxzf.dto.ProductCategoryDto;

import java.util.List;

public interface ProductCategoryService {

    ResultResponse<List<ProductCategoryDto>> findAll();
}
