package com.zang.cloud.mall.categoryproduct.service;

import com.github.pagehelper.PageInfo;
import com.zang.cloud.mall.categoryproduct.model.pojo.Category;
import com.zang.cloud.mall.categoryproduct.request.AddCategoryReq;
import com.zang.cloud.mall.categoryproduct.vo.CategoryVo;


import java.util.List;

public interface CategoryService {
    void add(AddCategoryReq addCategoryReq);

    void update(Category updateCategory);

    void delete(Integer id);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    List<CategoryVo> listForCustomer(Integer parentId);
}
