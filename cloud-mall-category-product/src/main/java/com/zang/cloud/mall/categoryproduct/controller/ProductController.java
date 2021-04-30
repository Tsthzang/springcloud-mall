package com.zang.cloud.mall.categoryproduct.controller;

import com.github.pagehelper.PageInfo;

import com.zang.cloud.mall.categoryproduct.model.pojo.Product;
import com.zang.cloud.mall.categoryproduct.request.ProductReq;
import com.zang.cloud.mall.categoryproduct.service.ProductService;
import com.zang.cloud.mall.common.common.ApiRestResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 前台商品Controller
 */
@RestController
public class ProductController {

    @Resource
    ProductService productService;

    @ApiOperation("商品详情")
    @GetMapping("product/detail")
    public ApiRestResponse detail(@RequestParam Integer id) {
        Product product = productService.detail(id);
        return ApiRestResponse.success(product);
    }

    @ApiOperation("前台搜索列表")
    @GetMapping("product/list")
    public ApiRestResponse list(ProductReq productReq) {
        PageInfo list = productService.list(productReq);
        return ApiRestResponse.success(list);
    }

    @ApiOperation("供购物车商品模块调用接口")
    @GetMapping("product/detailForFegin")
    public Product detailForFegin(@RequestParam Integer productId) {
        Product product = productService.detail(productId);
        //Product product = productMapper.selectByPrimaryKey(id)
        return product;
    }

    @ApiOperation("供购物车商品模块调用接口")
    @PostMapping("product/updateStockforFegin")
    public void updateStockforFegin(@RequestParam Integer productId, @RequestParam Integer stock) {
        productService.updateStock(productId, stock);
    }
}
