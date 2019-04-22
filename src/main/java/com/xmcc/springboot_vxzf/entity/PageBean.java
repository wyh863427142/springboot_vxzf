package com.xmcc.springboot_vxzf.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Entity;

import java.io.Serializable;


@Entity
@DynamicUpdate
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PageBean implements Serializable {


    private  String openId;

    private Integer page=0;

    private Integer size=10;

}


