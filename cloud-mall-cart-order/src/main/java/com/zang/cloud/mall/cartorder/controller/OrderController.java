package com.zang.cloud.mall.cartorder.controller;

import com.github.pagehelper.PageInfo;

import com.zang.cloud.mall.cartorder.fegin.UserFeginClient;
import com.zang.cloud.mall.cartorder.model.dao.OrderMapper;
import com.zang.cloud.mall.cartorder.model.pojo.Order;
import com.zang.cloud.mall.cartorder.quest.CreateOrderReq;
import com.zang.cloud.mall.cartorder.service.OrderService;
import com.zang.cloud.mall.cartorder.vo.OrderVO;
import com.zang.cloud.mall.common.common.ApiRestResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单Controller
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    OrderService orderService;

    @Resource
    OrderMapper orderMapper;

    @Resource
    UserFeginClient userFeginClient;

    /**
     * 创建订单接口
     * @param createOrderReq
     * @return
     */
    @PostMapping("/create")
    public ApiRestResponse create(@RequestBody @Valid CreateOrderReq createOrderReq) {
        String orderNo = orderService.create(createOrderReq);
        return ApiRestResponse.success(orderNo);
    }

    /**
     * 订单详情接口
     * @param orderNo
     * @return
     */
    @PostMapping("/detail")
    public ApiRestResponse detail(@RequestParam String orderNo) {
        OrderVO orderVO = orderService.detail(orderNo);
        return ApiRestResponse.success(orderVO);
    }

    /**
     * 前台订单列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping("/list")
    public ApiRestResponse list(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = orderService.list(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectByUserId(userFeginClient.getUser().getId());
        Map<Integer, PageInfo> map = new HashMap<>();
        map.put(orderList.size(),pageInfo);
        return ApiRestResponse.success(map);
    }

    /**
     * 取消订单接口
     * @param orderNo
     * @return
     */
    @PostMapping("/cancel")
    public ApiRestResponse cancel(@RequestParam String orderNo) {
        orderService.cancel(orderNo);
        return ApiRestResponse.success();
    }

    /**
     * 生成支付二维码接口
     * @param orderNo
     * @return
     */
    @PostMapping("/qrcode")
    public ApiRestResponse qrcode(@RequestParam String orderNo) {
        String pngAddress = orderService.qrcode(orderNo);
        return ApiRestResponse.success(pngAddress);
    }

    @PostMapping("/pay")
    public ApiRestResponse pay(@RequestParam String orderNo) {
        orderService.pay(orderNo);
        return ApiRestResponse.success();
    }

    /**
     * 完结订单接口
     * @param orderNo
     * @return
     */
    @PostMapping("/finish")
    public ApiRestResponse finish(@RequestParam String orderNo) {
        orderService.finish(orderNo);
        return ApiRestResponse.success();
    }
}
