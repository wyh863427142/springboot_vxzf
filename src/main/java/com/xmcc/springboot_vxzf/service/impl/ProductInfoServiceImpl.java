package com.xmcc.springboot_vxzf.service.impl;

import com.xmcc.springboot_vxzf.common.ResultEnums;
import com.xmcc.springboot_vxzf.common.ResultResponse;
import com.xmcc.springboot_vxzf.dto.ProductCategoryDto;
import com.xmcc.springboot_vxzf.dto.ProductInfoDto;
import com.xmcc.springboot_vxzf.entity.ProductInfo;
import com.xmcc.springboot_vxzf.repository.ProductInfoRepository;
import com.xmcc.springboot_vxzf.service.ProductCategoryService;
import com.xmcc.springboot_vxzf.service.ProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class ProductInfoServiceImpl implements ProductInfoService {

    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ProductInfoRepository productInfoRepository;

    @Override
    public ResultResponse queryList() {

    //查询所有类
        ResultResponse<List<ProductCategoryDto>> categoryServiceAll = productCategoryService.findAll();
        List<ProductCategoryDto> categoryDtoList = categoryServiceAll.getData();
        if (CollectionUtils.isEmpty(categoryDtoList)){
            return categoryServiceAll;
        }

        //获得类目编号集合
        List<Integer> typeList= categoryDtoList.stream().map(productCategoryDto ->
                productCategoryDto.getCategoryType()).collect(Collectors.toList());
        //查询商品列表  这里商品上下架可以用枚举 方便扩展
        List<ProductInfo> productInfoList=productInfoRepository.findByProductStatusAndCategoryTypeIn
                (ResultEnums.PRODUCT_UP.getCode(),typeList );
        //多线程遍历 取出每个商品类目编号对应的 商品列表 设置进入类目中
        List<ProductCategoryDto> finalResultList=categoryDtoList.parallelStream().map(productCategoryDto ->
        {productCategoryDto.setProductInfoDtoList(productInfoList.stream().filter(productInfo ->
                productInfo.getCategoryType()==productCategoryDto.getCategoryType()).map(productInfo ->
                ProductInfoDto.build(productInfo)).collect(Collectors.toList()));
        return productCategoryDto; }).collect(Collectors.toList());
        return ResultResponse.success(finalResultList);
    }
}
