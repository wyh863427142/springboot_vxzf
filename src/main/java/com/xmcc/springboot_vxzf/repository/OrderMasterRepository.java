package com.xmcc.springboot_vxzf.repository;

import com.xmcc.springboot_vxzf.entity.OrderMaster;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderMasterRepository extends JpaRepository<OrderMaster,String> {
    List<OrderMaster> findByBuyerOpenid(String openid, Pageable pageable);
    OrderMaster findByBuyerOpenidAndOrderId( String openid,String orderId);

}
