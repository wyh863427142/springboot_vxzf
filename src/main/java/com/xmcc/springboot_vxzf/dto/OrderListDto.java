package com.xmcc.springboot_vxzf.dto;

import com.xmcc.springboot_vxzf.common.OrderEnums;
import com.xmcc.springboot_vxzf.common.PayEnums;
import com.xmcc.springboot_vxzf.entity.OrderDetail;
import com.xmcc.springboot_vxzf.entity.OrderMaster;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;


import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("订单参数实体类")
public class OrderListDto implements Serializable {

    /** 订单id. */
    @Id
    private String orderId;

    /** 买家名字. */
    private String buyerName;

    /** 买家手机号. */
    private String buyerPhone;

    /** 买家地址. */
    private String buyerAddress;

    /** 买家微信Openid. */
    private String buyerOpenid;

    /** 订单总金额. */
    private BigDecimal orderAmount;

    /** 订单状态, 默认为0新下单. */
    private Integer orderStatus = OrderEnums.NEW.getCode();

    /** 支付状态, 默认为0未支付. */
    private Integer payStatus = PayEnums.WAIT.getCode();

    /** 创建时间. */
    private Date createTime;

    /** 更新时间. */
    private Date updateTime;


    /** 详情列表*/

    private List<OrderDetail> orderDetailList;


    public static OrderListDto build(OrderMaster orderMaster){
        OrderListDto orderListDto = new OrderListDto();
        BeanUtils.copyProperties(orderMaster,orderListDto );
        return orderListDto;
    }




}