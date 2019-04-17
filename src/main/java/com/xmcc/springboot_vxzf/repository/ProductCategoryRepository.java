package com.xmcc.springboot_vxzf.repository;

import com.xmcc.springboot_vxzf.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

//第一个参数 是实体类名称  第二个参数是主键类型
public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Integer> {
}
