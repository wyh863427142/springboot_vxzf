package com.xmcc.springboot_vxzf.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.xml.internal.bind.v2.TODO;
import com.xmcc.springboot_vxzf.common.OrderEnums;
import com.xmcc.springboot_vxzf.common.PayEnums;
import com.xmcc.springboot_vxzf.common.ResultEnums;
import com.xmcc.springboot_vxzf.common.ResultResponse;
import com.xmcc.springboot_vxzf.dto.OrderDetailDto;
import com.xmcc.springboot_vxzf.dto.OrderListDto;
import com.xmcc.springboot_vxzf.dto.OrderMasterDto;
import com.xmcc.springboot_vxzf.entity.*;
import com.xmcc.springboot_vxzf.exception.CustomException;
import com.xmcc.springboot_vxzf.repository.OrderMasterRepository;
import com.xmcc.springboot_vxzf.service.OrderDetailService;
import com.xmcc.springboot_vxzf.service.OrderMasterService;
import com.xmcc.springboot_vxzf.service.ProductInfoService;
import com.xmcc.springboot_vxzf.utils.BigDecimalUtil;
import com.xmcc.springboot_vxzf.utils.IDUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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



    @Override
    public ResultResponse findPageBean(PageBean pageBean) {
        if (StringUtils.isBlank(pageBean.getOpenId())){
            return ResultResponse.fail(OrderEnums.OPENID_ERROR.getMeg());
        }
        Pageable pageable = new PageRequest(pageBean.getPage(), pageBean.getSize());

        List<OrderMaster> masterList = orderMasterRepository.findByBuyerOpenid(pageBean.getOpenId(), pageable);

        if (CollectionUtils.isEmpty(masterList)){
            return ResultResponse.fail(OrderEnums.ORDER_NOT_EXITS.getMeg());
        }
        //转为dto
        List<OrderListDto> orderListDtoList=masterList.stream().map(orderMaster ->
                OrderListDto.build(orderMaster)).collect(Collectors.toList());


            return ResultResponse.success(orderListDtoList);
    }


    //订单详情
    @Override
    public ResultResponse findDetailList(String openid, String orderId) {
        if (StringUtils.isBlank(openid)||StringUtils.isBlank(orderId)){
            return  ResultResponse.fail(ResultEnums.PAPAM_ERROR.getMsg());
        }

        OrderMaster master = orderMasterRepository.findByBuyerOpenidAndOrderId(openid, orderId);
        if (master==null){
            return ResultResponse.fail(OrderEnums.ORDER_NOT_EXITS.getMeg());
        }

        OrderListDto build = OrderListDto.build(master);

        List<OrderDetail> allByDetailList= orderDetailService.findAllByDetailId(orderId);

        build.setOrderDetailList(allByDetailList);

        return ResultResponse.success(build);
    }


    //取消订单
    @Override
    @Transactional
    public ResultResponse CancelOrder(DetailId detailId){
        if (StringUtils.isBlank(detailId.getOpenId())||StringUtils.isBlank(detailId.getOrderId())){
            return  ResultResponse.fail(ResultEnums.PAPAM_ERROR.getMsg());
        }
        OrderMaster order = orderMasterRepository.findByBuyerOpenidAndOrderId(detailId.getOpenId(), detailId.getOrderId());
       //判断订单是否存在
        if (order==null){
            return ResultResponse.fail(OrderEnums.ORDER_NOT_EXITS.getMeg());
        }
        //判断订单的状态
        if(order.getOrderStatus().equals(OrderEnums.CANCEL.getCode())||order.getOrderStatus().
        equals(OrderEnums.FINSH.getCode())){
            return ResultResponse.fail(OrderEnums.FINSH_CANCEL.getMeg());
        }

        //判断是否支付
        if (order.getPayStatus().equals(PayEnums.FINISH)){
            //TODO:支付后，取消订单，返回买家money；
        }
        order.setOrderStatus(OrderEnums.CANCEL.getCode());
        orderMasterRepository.save(order);
        //获取订单商品的数量
        List<OrderDetail> allByDetailList = orderDetailService.findAllByDetailId(detailId.getOrderId());

        for (OrderDetail orderDetail:allByDetailList
             ) {
            Integer rebacke=orderDetail.getProductQuantity();
            //设置库存数据回滚
            ResultResponse<ProductInfo> resultResponse = productInfoService.queryById(orderDetail.getProductId());
            //说明该商品未查询到 生成订单失败，因为这儿涉及到事务 需要抛出异常 事务机制是遇到异常才会回滚
            if (resultResponse.getCode()== ResultEnums.FAIL.getCode()){
                throw  new CustomException(resultResponse.getMsg());
            }
            ProductInfo data = resultResponse.getData();
            data.setProductStock(data.getProductStock()+rebacke);
            productInfoService.updateProduct(data);
        }
        return ResultResponse.success(null);
    }

}
