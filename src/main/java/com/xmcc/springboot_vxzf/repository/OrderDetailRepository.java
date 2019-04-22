package com.xmcc.springboot_vxzf.repository;

import com.xmcc.springboot_vxzf.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail,String> {
    List<OrderDetail> findAllByDetailId(String orderId);

}
