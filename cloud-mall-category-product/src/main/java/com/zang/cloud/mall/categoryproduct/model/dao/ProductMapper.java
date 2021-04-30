package com.zang.cloud.mall.categoryproduct.model.dao;

import com.zang.cloud.mall.categoryproduct.model.pojo.Product;
import com.zang.cloud.mall.categoryproduct.query.ProductListQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    Product selectName(String name);

    //批量上下架
    int batchUpdateSellStauts(@Param("ids") Integer[] ids, @Param("sellStauts") Integer sellStauts);

    //后台分页查询商品列表
    List<Product> selectListForAdmin();

    //前台搜索商品列表
    List<Product> selectList(@Param("query") ProductListQuery query);
}