package com.xmcc.springboot_vxzf;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
public class LogbackTest {

    private  final Logger logger=LoggerFactory.getLogger(LogbackTest.class);

    @Test
    public  void  test(){
        logger.debug("debug");
        logger.info("info");
        logger.error("error");
    }
}
