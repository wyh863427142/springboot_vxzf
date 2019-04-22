package com.xmcc.springboot_vxzf.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "wechat")
@Data
public class WeiXinProperties {

    private String appid;
    private String secret;
}
