package com.xmcc.springboot_vxzf.service.impl;

import com.xmcc.springboot_vxzf.dao.AbstractBatchDao;
import com.xmcc.springboot_vxzf.entity.OrderDetail;
import com.xmcc.springboot_vxzf.service.OrderDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderDetailServiceImpl extends AbstractBatchDao<OrderDetail> implements OrderDetailService {

    @Override
    @Transactional
    public void batchInsert(List<OrderDetail> list) {
        super.batchInsert(list);
    }
}
