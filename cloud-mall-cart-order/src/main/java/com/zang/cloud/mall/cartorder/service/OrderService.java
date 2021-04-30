package com.zang.cloud.mall.cartorder.service;

import com.github.pagehelper.PageInfo;
import com.zang.cloud.mall.cartorder.quest.CreateOrderReq;
import com.zang.cloud.mall.cartorder.vo.OrderVO;

public interface OrderService {
    String create(CreateOrderReq createOrderReq);

    OrderVO detail(String orderNo);

    PageInfo list(Integer pageNum, Integer pageSize);

    void cancel(String orderNo);

    String qrcode(String orderNo);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    void pay(String orderNo);

    void delivered(String orderNo);

    void finish(String orderNo);
}
