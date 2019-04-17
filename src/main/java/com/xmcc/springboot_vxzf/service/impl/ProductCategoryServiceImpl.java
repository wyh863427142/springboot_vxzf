package com.xmcc.springboot_vxzf.service.impl;

import com.xmcc.springboot_vxzf.common.ResultResponse;
import com.xmcc.springboot_vxzf.dto.ProductCategoryDto;
import com.xmcc.springboot_vxzf.entity.ProductCategory;
import com.xmcc.springboot_vxzf.repository.ProductCategoryRepository;
import com.xmcc.springboot_vxzf.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {


    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Override
    public ResultResponse<List<ProductCategoryDto>> findAll() {
        List<ProductCategory> productCategoryList = productCategoryRepository.findAll();
        //利用流转换为dto集合
        return ResultResponse.success(productCategoryList.stream().
                map(productCategory -> ProductCategoryDto.build(productCategory)
                ).collect(Collectors.toList()));
    }
}
