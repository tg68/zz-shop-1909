package com.qf.cart.impl;

import com.qf.cart.MyCartService;
import com.qf.conmon.constant.RedisConstant;
import com.qf.conmon.dto.ResultBean;
import com.qf.conmon.util.StringUtil;
import com.qf.mapper.TProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author gaotao
 * @version 1.0
 * @date 2020/3/12 20:44
 */



@Service
@Component
public class MyCartServiceImpl implements MyCartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TProductMapper productMapper;

    @Override
    public ResultBean addProduct(String id, Long productId, int count) {

        String redisKey = StringUtil.getRedisKey(RedisConstant.USER_CART_PRE, id);


        return null;
    }

    @Override
    public ResultBean clean(String uuid) {
        return null;
    }

    @Override
    public ResultBean update(String uuid, Long productId, int count) {
        return null;
    }

    @Override
    public ResultBean showCart(String id) {
        return null;
    }

    @Override
    public ResultBean merge(String uuid, String userId) {
        return null;
    }
}
