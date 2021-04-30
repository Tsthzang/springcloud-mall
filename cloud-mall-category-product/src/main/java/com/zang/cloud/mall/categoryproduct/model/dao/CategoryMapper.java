package com.zang.cloud.mall.categoryproduct.model.dao;


import com.zang.cloud.mall.categoryproduct.model.pojo.Category;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    Category selecteByName(String name);

    List<Category> selectList();

    List<Category> selectByParentId(Integer parentId);
}