package com.xmcc.springboot_vxzf.service;

import com.xmcc.springboot_vxzf.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {
    void batchInsert(List<OrderDetail> orderDetailList);

    List<OrderDetail> findAllByDetailId(String orderId);
}


