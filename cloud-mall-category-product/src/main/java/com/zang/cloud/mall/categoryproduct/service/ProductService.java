package com.zang.cloud.mall.categoryproduct.service;

import com.github.pagehelper.PageInfo;
import com.zang.cloud.mall.categoryproduct.model.pojo.Product;
import com.zang.cloud.mall.categoryproduct.request.AddProductReq;
import com.zang.cloud.mall.categoryproduct.request.ProductReq;


/**
 * 商品service
 */
public interface ProductService {
    void add(AddProductReq addProductReq);

    void update(Product updateProduct);

    void delete(Integer id);

    void batchUpdateSellStauts(Integer[] ids, Integer sellStauts);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    Product detail(Integer id);

    PageInfo list(ProductReq productReq);

    void updateStock(Integer productId, Integer stock);
}
