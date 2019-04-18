package com.xmcc.springboot_vxzf.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xmcc.springboot_vxzf.common.OrderEnums;
import com.xmcc.springboot_vxzf.common.PayEnums;
import com.xmcc.springboot_vxzf.common.ResultEnums;
import com.xmcc.springboot_vxzf.common.ResultResponse;
import com.xmcc.springboot_vxzf.dto.OrderDetailDto;
import com.xmcc.springboot_vxzf.dto.OrderMasterDto;
import com.xmcc.springboot_vxzf.entity.OrderDetail;
import com.xmcc.springboot_vxzf.entity.OrderMaster;
import com.xmcc.springboot_vxzf.entity.ProductInfo;
import com.xmcc.springboot_vxzf.exception.CustomException;
import com.xmcc.springboot_vxzf.repository.OrderMasterRepository;
import com.xmcc.springboot_vxzf.service.OrderDetailService;
import com.xmcc.springboot_vxzf.service.OrderMasterService;
import com.xmcc.springboot_vxzf.service.ProductInfoService;
import com.xmcc.springboot_vxzf.utils.BigDecimalUtil;
import com.xmcc.springboot_vxzf.utils.IDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class OrderMasterServiceImpl implements OrderMasterService {


    @Autowired
    private ProductInfoService productInfoService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private OrderMasterRepository orderMasterRepository;


    @Transactional
    @Override
    public ResultResponse insertOrder(OrderMasterDto orderMasterDto) {
        //前面已经进行了参数校验 这儿不需要了  取出订单项即可
        List<OrderDetailDto> items=orderMasterDto.getItems();
        //创建订单detail 集合 将符合的放入其中 待会批量插入
         List<OrderDetail> orderDetailList=Lists.newArrayList();
        //创建订单总金额为0  涉及到钱的都用 高精度计算
        BigDecimal totalPrice = new BigDecimal("0");

        for (OrderDetailDto item: items){
            ResultResponse<ProductInfo> resultResponse = productInfoService.queryById(item.getProductId());
            //说明该商品未查询到 生成订单失败，因为这儿涉及到事务 需要抛出异常 事务机制是遇到异常才会回滚
             if (resultResponse.getCode()== ResultEnums.FAIL.getCode()){
                 throw  new CustomException(resultResponse.getMsg());
             }
            //获得查询的商品
            ProductInfo productInfo = resultResponse.getData();
            //说明该商品 库存不足 订单生成失败 直接抛出异常 事务才会回滚
            if (productInfo.getProductStock()<item.getProductQuantity()){
                throw new CustomException(ResultEnums.PRODUCT_NOT_ENOUGH.getMsg());
            }

            //将前台传入的订单项DTO与数据库查询到的 商品数据组装成OrderDetail 放入集合中  @builder
            OrderDetail orderDetail=
                    OrderDetail.builder().detailId(IDUtils.createIdbyUUID()).productIcon(productInfo.getProductIcon())
                            .productId(item.getProductId()).productName(productInfo.getProductName())
                            .productPrice(productInfo.getProductPrice()).productQuantity(item.getProductQuantity())
                            .build();
            orderDetailList.add(orderDetail);
            //减少商品库存
            productInfo.setProductStock(productInfo.getProductStock()-item.getProductQuantity());
            productInfoService.updateProduct(productInfo);

            //计算价格
            totalPrice=
                    BigDecimalUtil.add(totalPrice,BigDecimalUtil.multi(productInfo.getProductPrice(),item.getProductQuantity() ) );

        }

        //生成订单id
        String orderId=IDUtils.createIdbyUUID();
        //构建订单信息  日期等都用默认的即可
        OrderMaster orderMaster = OrderMaster.builder().buyerAddress(orderMasterDto.getAddress()).buyerName(orderMasterDto.getName())
                .buyerOpenid(orderMasterDto.getOpenid()).orderStatus(OrderEnums.NEW.getCode())
                .payStatus(PayEnums.WAIT.getCode()).buyerPhone(orderMasterDto.getPhone())
                .orderId(orderId).orderAmount(totalPrice).build();
        //将生成的订单id，设置到订单项中
        orderDetailList.stream().map(orderDetail -> {
            orderDetail.setOrderId(orderId);
            return orderDetail;
        }).collect(Collectors.toList());

        //插入订单项
        orderDetailService.batchInsert(orderDetailList);
        //插入订单
        orderMasterRepository.save(orderMaster);
        HashMap<String, String> map = Maps.newHashMap();
        //按照前台要求的数据结构传入
        map.put("orderId",orderId );

        return ResultResponse.success(map);
    }
}
