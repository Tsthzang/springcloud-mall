package com.zang.cloud.mall.cartorder.controller;

import com.zang.cloud.mall.cartorder.fegin.UserFeginClient;
import com.zang.cloud.mall.cartorder.service.CartService;
import com.zang.cloud.mall.cartorder.vo.CartVO;
import com.zang.cloud.mall.common.common.ApiRestResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Resource
    CartService cartService;

    @Resource
    UserFeginClient userFeginClient;

    /**
     * 返回购物车列表接口
     * @return
     */
    @PostMapping("/list")
    public ApiRestResponse list(){
        //内部获取用户，防止横向越权
        List<CartVO> cartList = cartService.list(userFeginClient.getUser().getId());
        return ApiRestResponse.success(cartList);
    }

    /**
     * 添加商品到购物车接口
     * （返回购物车列表）
     * @param productId
     * @param count
     * @return
     */
    @PostMapping("/add")
    public ApiRestResponse add(@RequestParam Integer productId, @RequestParam Integer count){
        List<CartVO> cartList = cartService.add(userFeginClient.getUser().getId(),productId,count);
        return ApiRestResponse.success(cartList);
    }

    /**
     * 更新购物车商品接口
     * @param productId
     * @param count
     * @return
     */
    @PostMapping("/update")
    public ApiRestResponse update(@RequestParam Integer productId, @RequestParam Integer count){
        List<CartVO> cartList = cartService.update(userFeginClient.getUser().getId(),productId,count);
        return ApiRestResponse.success(cartList);
    }

    /**
     * 删除购物车商品接口
     * @param productId
     * @return
     */
    @PostMapping("/delete")
    public ApiRestResponse delete(@RequestParam Integer productId){
        //不能传入userID和cartId，否则可以恶意更改别人的购物车
        List<CartVO> cartList = cartService.delete(userFeginClient.getUser().getId(),productId);
        return ApiRestResponse.success(cartList);
    }

    /**
     * 选择/取消选择购物车商品接口
     * @param productId
     * @param selected
     * @return
     */
    @PostMapping("/select")
    public ApiRestResponse select(@RequestParam Integer productId, @RequestParam Integer selected){
        //不能传入userID和cartId，否则可以恶意更改别人的购物车
        List<CartVO> cartList = cartService.selectOrNot(userFeginClient.getUser().getId(),productId,selected);
        return ApiRestResponse.success(cartList);
    }

    /**
     * 全选/全不选购物车商品接口
     * @param selected
     * @return
     */
    @PostMapping("/selectAll")
    public ApiRestResponse selectAll(@RequestParam Integer selected){
        //不能传入userID和cartId，否则可以恶意更改别人的购物车
        List<CartVO> cartList = cartService.selectAllOrNot(userFeginClient.getUser().getId(),selected);
        return ApiRestResponse.success(cartList);
    }
}
