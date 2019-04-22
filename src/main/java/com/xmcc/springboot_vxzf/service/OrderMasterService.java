package com.xmcc.springboot_vxzf.service;

import com.xmcc.springboot_vxzf.common.ResultResponse;
import com.xmcc.springboot_vxzf.dto.OrderMasterDto;
import com.xmcc.springboot_vxzf.entity.DetailId;
import com.xmcc.springboot_vxzf.entity.PageBean;


public interface OrderMasterService {

    ResultResponse insertOrder(OrderMasterDto orderMasterDto);


    ResultResponse findPageBean(PageBean pageBean);

    ResultResponse findDetailList(String openid,String orderId);

    ResultResponse CancelOrder(DetailId detailId);

}
