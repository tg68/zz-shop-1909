package com.qf.cart;

import com.qf.conmon.dto.ResultBean;

/**
 * @author gaotao
 * @version 1.0
 * @date 2020/3/12 16:29
 */
public interface MyCartService {

    ResultBean addProduct(String id, Long productId, int count);

    ResultBean clean(String uuid);

    ResultBean update(String uuid, Long productId, int count);

    ResultBean showCart(String id);

    ResultBean merge(String uuid, String userId);

}
