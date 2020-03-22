package com.qf.conmon;

import com.qf.conmon.util.StringUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author gaotao
 * @version 1.0
 * @date 2020/3/14 0:21
 */

public class StringTest {
    public static void main(String[] args) {
        testString();
    }

    public static void testString(){

        String redisKey = StringUtil.getRedisKey("123", "456");
        System.out.println(redisKey);
    }
}
