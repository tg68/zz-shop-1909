package com.qf.cart;

import com.qf.conmon.dto.ResultBean;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShopCartApplicationTests {

    @Autowired
    private MyCartService myCartService;

    @Test
    void contextLoads() {
    }

    @Test
    public void test1(){

        ResultBean resultBean = myCartService.addProduct("120", 3L, 12);
        System.out.println(resultBean);
    }
    @Test
    public void test2(){
        ResultBean clean = myCartService.clean("000");
        System.out.println(clean);

    }

}
