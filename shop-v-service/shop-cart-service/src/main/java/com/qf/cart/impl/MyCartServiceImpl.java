package com.qf.cart.impl;

import com.qf.cart.MyCartService;
import com.qf.conmon.constant.RedisConstant;
import com.qf.conmon.dto.ResultBean;
import com.qf.conmon.dto.TProductCartDTO;
import com.qf.conmon.util.StringUtil;
import com.qf.conmon.vo.CartItem;
import com.qf.entity.TProduct;
import com.qf.mapper.TProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

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


    /**
     * 添加购物车
     *
     * @param id        用户id
     * @param productId 商品id
     * @param count     商品数量
     * @return 返回ResultBean类型
     */
    @Override
    public ResultBean addProduct(String id, Long productId, int count) {

        String redisKey = StringUtil.getRedisKey(RedisConstant.USER_CART_PRE, id);

        Object o = redisTemplate.opsForValue().get(redisKey);
        if (o == null) {
            /*
            当前用户没有购物车
            封装购物车商品对象
            * */
            CartItem cartItem = new CartItem();
            cartItem.setUpdateTime(new Date());
            cartItem.setCount(count);
            cartItem.setProductId(productId);

            /*添加购物车中*/
            List<CartItem> carts = new ArrayList<>();
            carts.add(cartItem);
            /*放到redis里*/
            redisTemplate.opsForValue().set(redisKey, carts);
            return ResultBean.success(carts, "添加购物车成功！");
        }
        /*有购物车有商品*/
        List<CartItem> carts = (List<CartItem>) o;
        for (CartItem cartItem : carts) {
            if (cartItem.getProductId().longValue() == productId.longValue()) {

                cartItem.setCount(cartItem.getCount() + count);

                cartItem.setUpdateTime(new Date());

                redisTemplate.opsForValue().set(redisKey, carts);
                return ResultBean.success(carts, "添加购物车成功！");
            }

        }
        CartItem cartItem = new CartItem();
        cartItem.setProductId(productId);
        cartItem.setUpdateTime(new Date());
        cartItem.setCount(count);
        carts.add(cartItem);
        redisTemplate.opsForValue().set(redisKey, carts);
        return ResultBean.success(carts, "添加购物车成功！");
    }

    /**
     * 删除redis里的购物车
     *
     * @param uuid 用户id
     * @return 返回ResultBean类型
     */
    @Override
    public ResultBean clean(String uuid) {

        String redisKey = StringUtil.getRedisKey(RedisConstant.USER_CART_PRE, uuid);
        redisTemplate.delete(redisKey);

        return ResultBean.success("删除购物车成功！");
    }

    /**
     * 更新购物车
     *
     * @param uuid      用户id
     * @param productId 商品id
     * @param count     数量
     * @return 返回ResultBean类型
     */
    @Override
    public ResultBean update(String uuid, Long productId, int count) {
        if (uuid != null && !"".equals(uuid)) {
            String redisKey = StringUtil.getRedisKey(RedisConstant.USER_CART_PRE, uuid);
            Object o = redisTemplate.opsForValue().get(redisKey);
            if (o != null) {
                List<CartItem> carts = (List<CartItem>) o;
                for (CartItem cartItem : carts) {
                    if (cartItem.getProductId() == productId.longValue()) {
                        cartItem.setCount(count);
                        cartItem.setUpdateTime(new Date());
                        return ResultBean.success(carts, "更新成功");
                    }

                }

            }

        }

        return ResultBean.success("没有购物车！");
    }

    /**
     * 查看购物车
     *
     * @param id 用户id
     * @return ResultBean类
     */
    @Override
    public ResultBean showCart(String id) {

        if (id != null && !"".equals(id)) {
            //获取redis数据
            String redisKey = StringUtil.getRedisKey(RedisConstant.USER_CART_PRE, id);
            Object o = redisTemplate.opsForValue().get(redisKey);
            if (o != null) {
                List<CartItem> carts = (List<CartItem>) o;
                List<TProductCartDTO> tProductCartDTOS = new ArrayList<>();
                for (CartItem cartItem : carts) {
                    String productKey = StringUtil.getRedisKey(RedisConstant.PRODUCT_PRE, cartItem.getProductId().toString());
                    System.out.println(productKey);
                    TProduct pro = (TProduct) redisTemplate.opsForValue().get(productKey);
                    if (pro == null) {
                        //去数据库取数据
                        TProduct tProduct = productMapper.selectByPrimaryKey(cartItem.getProductId());
                        //存redis里
                        redisTemplate.opsForValue().set(productKey, tProduct);
                    }
                    TProductCartDTO tProductCartDTO = new TProductCartDTO();
                    tProductCartDTO.setProduct(pro);
                    tProductCartDTO.setCount(cartItem.getCount());
                    tProductCartDTO.setUpdateTime(cartItem.getUpdateTime());

                    tProductCartDTOS.add(tProductCartDTO);
                }
                //对集合进行排序。
                Collections.sort(tProductCartDTOS, new Comparator<TProductCartDTO>() {
                    @Override
                    public int compare(TProductCartDTO o1, TProductCartDTO o2) {
                        return (int) (o1.getUpdateTime().getTime() - o2.getUpdateTime().getTime());
                    }
                });
                return ResultBean.success(tProductCartDTOS);
            }


        }

        return ResultBean.error("没有购物车！");
    }

    /**
     * 合并购物车
     *
     * @param uuid   未登录状态id
     * @param userId 登录状态id
     * @return 返回ResultBean类
     */
    @Override
    public ResultBean merge(String uuid, String userId) {
         /*
        合并
            1.未登录状态下没有购物车==》合并成功
            2.未登录状态下有购物车，但已登录状态下没有购物车==》把未登录的变成已登录的
            3.未登录状态下有购物车，但已登录状态下也有购物车，而且购物车中的商品有重复==》难点！
         */
        //获得两种状态下的购物车
        String noLoginkey = StringUtil.getRedisKey(RedisConstant.USER_CART_PRE, uuid);
        String loginkey = StringUtil.getRedisKey(RedisConstant.USER_CART_PRE, userId);
        Object noLoginO = redisTemplate.opsForValue().get(noLoginkey);
        Object loginO = redisTemplate.opsForValue().get(loginkey);

        //未登录状态没有购物车
        if (noLoginO == null) {
            return ResultBean.success("未登录状态没有购物车！");
        }
        //登录状态没有购物车
        if (loginO == null) {
            //登录状态，没有购物车。未登录有。把未登录状态的购物车变成登录状态的。
            redisTemplate.opsForValue().set(loginkey,noLoginO);
            //删除redis里的未登录数据库
            redisTemplate.delete(noLoginO);
            return ResultBean.success("和并成功！");
        }

        List<CartItem> noLoginCarts = (List<CartItem>) noLoginO;
        List<CartItem> loginCarts = (List<CartItem>) loginO;

        HashMap<Long, CartItem> mapCart = new HashMap<>();
        for (CartItem noLoginCart : noLoginCarts){
            mapCart.put(noLoginCart.getProductId(),noLoginCart);
        }
        //此时map中就有所有的未登录状态下的购物车的商品
        //存入已登录状态下购物车的商品
        for (CartItem loginCart : loginCarts){
            //检查下map中该商品是否已存在
            CartItem cartItem = mapCart.get(loginCart.getProductId());
            if (cartItem != null){
                cartItem.setCount(cartItem.getCount()+loginCart.getCount());
            }else {
                mapCart.put(loginCart.getProductId(),loginCart);
            }

        }
        //此时Map中存放的数据就是合并之后的购物车
        //删除未登录状态下的购物车
        redisTemplate.delete(noLoginCarts);

        Collection<CartItem> values = mapCart.values();
        System.out.println("合并："+ values);
        List<CartItem> newCart = new ArrayList<>(values);
        redisTemplate.opsForValue().set(loginkey,newCart);
        return ResultBean.success(newCart,"合并成功！");
    }
}
