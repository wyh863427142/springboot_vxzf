package com.xmcc.springboot_vxzf.service;

import com.xmcc.springboot_vxzf.common.ResultResponse;
import com.xmcc.springboot_vxzf.dto.OrderMasterDto;



public interface OrderMasterService {

    ResultResponse insertOrder(OrderMasterDto orderMasterDto);
}
