package com.zang.cloud.mall.cartorder.model.dao;

import com.zang.cloud.mall.cartorder.model.pojo.Order;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByorderNo(String orderNo);

    List<Order> selectByUserId(Integer userId);

    List<Order> selectByAll();
}