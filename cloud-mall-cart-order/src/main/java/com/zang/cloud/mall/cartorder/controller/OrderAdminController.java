package com.zang.cloud.mall.cartorder.controller;

import com.github.pagehelper.PageInfo;

import com.zang.cloud.mall.cartorder.model.dao.OrderMapper;
import com.zang.cloud.mall.cartorder.model.pojo.Order;
import com.zang.cloud.mall.cartorder.service.OrderService;
import com.zang.cloud.mall.common.common.ApiRestResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/order")
public class OrderAdminController {

    @Resource
    OrderService orderService;

    @Resource
    OrderMapper orderMapper;

    /**
     * 后台订单列表接口
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping("/list")
    public ApiRestResponse list(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = orderService.listForAdmin(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectByAll();
        Map<Integer, PageInfo> map = new HashMap<>();
        map.put(orderList.size(),pageInfo);
        return ApiRestResponse.success(map);
    }

    /**
     * 管理员发货接口
     * @param orderNo
     * @return
     */
    @PostMapping("/delivered")
    public ApiRestResponse delivered(@RequestParam String orderNo) {
        orderService.delivered(orderNo);
        return ApiRestResponse.success();
    }
}
