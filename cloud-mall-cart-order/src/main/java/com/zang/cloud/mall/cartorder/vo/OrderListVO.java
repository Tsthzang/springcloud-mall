package com.zang.cloud.mall.cartorder.vo;

import java.util.List;

public class OrderListVO {
    private Integer total;

    private List<OrderVO> orderVOList;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<OrderVO> getOrderVOList() {
        return orderVOList;
    }

    public void setOrderVOList(List<OrderVO> orderVOList) {
        this.orderVOList = orderVOList;
    }
}
