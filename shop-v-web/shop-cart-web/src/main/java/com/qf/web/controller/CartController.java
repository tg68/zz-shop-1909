package com.qf.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.cart.MyCartService;
import com.qf.conmon.constant.CookieConstant;
import com.qf.conmon.dto.ResultBean;
import com.qf.entity.TUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author gaotao
 * @version 1.0
 * @date 2020/3/18 22:08
 */
@Controller
@RequestMapping("/Cart")
public class CartController {
    @Reference
    private MyCartService myCartService;

    /**
     * * 添加商品到购物车
     * <p>
     * 1）当前用户没有购物车
     * 新建购物车，把商品添加到购物车中，再把购物车存到redis中
     * 2）当前用户有购物车，但是购物车中没有该商品
     * 先从redis中获取该购物车，再把商品添加都购物车中，再存入到redis中。
     * 3）当前用户有购物车，且购物车中有该商品
     * 先从redis中获取该购物车，再获取该商品的数量，再让新的数量和老的数量相加，更新回购物车中，再更新回redis中。
     */

    @RequestMapping("/add")
    @ResponseBody
    public ResultBean addproduct(@CookieValue(name = CookieConstant.USER_CART, required = false) String uuid,
                                 Long productId,
                                 int count,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {


        Object user = request.getAttribute("user");
        if (user != null) {

            TUser tUser = (TUser) user;
            Long id = tUser.getId();
            return myCartService.addProduct(id.toString(), productId, count);
        }

        //未登录的购物车的状态下。
        if (uuid == null || "".equals(uuid)) {
            String s = UUID.randomUUID().toString();

            Cookie cookie = new Cookie(CookieConstant.USER_CART, s);
            cookie.setPath("/");
            response.addCookie(cookie);

        }
        ResultBean resultBean = myCartService.addProduct(uuid, productId, count);
        return resultBean;
    }

    /**
     * 清空购物车
     *
     * @param uuid     未登陆用户id
     * @param response 响应
     * @param request  请求
     * @return 返回ResultBean类型
     */
    @RequestMapping("/cleanCart")
    @ResponseBody
    public ResultBean cleanCart(@CookieValue(name = CookieConstant.USER_CART, required = false) String uuid,
                                HttpServletResponse response,
                                HttpServletRequest request) {
        Object user = request.getAttribute("user");

        if (user != null) {
            TUser tUser = (TUser) user;
            ResultBean clean = myCartService.clean(tUser.getId().toString());
            return clean;
        }

        if (uuid != null || !"".equals(uuid)) {
            Cookie cookie = new Cookie(CookieConstant.USER_CART, "");
            cookie.setPath("/");
            cookie.setMaxAge(0);

            response.addCookie(cookie);
            return myCartService.clean(uuid);
        }
        return ResultBean.error("当前用户没有购物车！");

    }

    /**
     * 更新购物车
     *
     * @param uuid      未登陆用户id
     * @param productId 商品id
     * @param count     商品数量
     * @param request   请求
     * @return 返回 ResultBean
     */
    @RequestMapping("/update")
    @ResponseBody
    public ResultBean updateCart(@CookieValue(name = CookieConstant.USER_CART, required = false) String uuid,
                                 Long productId,
                                 int count,
                                 HttpServletRequest request) {
        Object user = request.getAttribute("user");

        if (user != null) {
            TUser tUser = (TUser) user;
            return myCartService.update(tUser.getId().toString(), productId, count);
        }
        return myCartService.update(uuid, productId, count);
    }


    /**
     * 展示购物车
     *
     * @param uuid    未登陆 用户id
     * @param request 请求
     * @return 返回ResultBean
     */
    @RequestMapping("/show")
    @ResponseBody
    public ResultBean showCart(@CookieValue(name = CookieConstant.USER_CART, required = false) String uuid, HttpServletRequest request) {

        Object user = request.getAttribute("user");

        if (user != null) {
            TUser tUser = (TUser) user;
            Long id = tUser.getId();
            return myCartService.showCart(id.toString());
        }
        return myCartService.showCart(uuid);
    }

    /**
     * 合并购物车
     *
     * @param uuid     未登陆用户id
     * @param request  请求
     * @param response 响应
     * @return 返回ResultBean
     */
    @RequestMapping("/merge")
    @ResponseBody
    public ResultBean merge(@CookieValue(name = CookieConstant.USER_CART, required = false) String uuid,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        TUser user = (TUser) request.getAttribute("user");
        String userId = null;
        if (user != null) {
            userId = user.getId().toString();
        }
        Cookie cookie = new Cookie(CookieConstant.USER_CART, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return myCartService.merge(uuid, userId);
    }
}
