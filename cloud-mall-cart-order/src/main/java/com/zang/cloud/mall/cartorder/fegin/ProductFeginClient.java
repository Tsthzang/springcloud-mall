package com.zang.cloud.mall.cartorder.fegin;

import com.zang.cloud.mall.categoryproduct.model.pojo.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 商品的FeginClient
 */
@FeignClient(value = "cloud-mall-category-product")
public interface ProductFeginClient {

    @GetMapping("product/detailForFegin")
    Product detailForFegin(@RequestParam Integer productId);

    @PostMapping("product/updateStockforFegin")
    void updateStockforFegin(@RequestParam Integer productId, @RequestParam Integer stock);
}
