package com.zang.cloud.mall.categoryproduct.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.zang.cloud.mall.categoryproduct.model.dao.ProductMapper;
import com.zang.cloud.mall.categoryproduct.model.pojo.Product;
import com.zang.cloud.mall.categoryproduct.query.ProductListQuery;
import com.zang.cloud.mall.categoryproduct.request.AddProductReq;
import com.zang.cloud.mall.categoryproduct.request.ProductReq;
import com.zang.cloud.mall.categoryproduct.service.CategoryService;
import com.zang.cloud.mall.categoryproduct.service.ProductService;
import com.zang.cloud.mall.categoryproduct.vo.CategoryVo;
import com.zang.cloud.mall.common.common.Constant;
import com.zang.cloud.mall.common.exception.ImoocException;
import com.zang.cloud.mall.common.exception.ImoocMallExceptionEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    ProductMapper productMapper;

    @Resource
    CategoryService categoryService;

    /**
     * 添加商品
     * @param addProductReq
     */
    @Override
    public void add(AddProductReq addProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(addProductReq, product);
        Product productOld = productMapper.selectName(addProductReq.getName());
        if (productOld != null) {
            throw new ImoocException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        int count = productMapper.insertSelective(product);
        if (count == 0) {
            throw new ImoocException(ImoocMallExceptionEnum.CREATE_FAILED);
        }
    }

    /**
     * 更新商品
     * @param updateProduct
     */
    @Override
    public void update(Product updateProduct) {
        Product productOld = productMapper.selectName(updateProduct.getName());
        if (productOld != null && !productOld.getId().equals(updateProduct.getId())) {
            throw new ImoocException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        int count = productMapper.updateByPrimaryKeySelective(updateProduct);
        if (count == 0) {
            throw new ImoocException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
    }

    /**
     * 删除商品
     * @param id
     */
    @Override
    public void delete(Integer id) {
        Product productOld = productMapper.selectByPrimaryKey(id);
        if (productOld == null ) {
            throw new ImoocException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
        int count = productMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new ImoocException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
    }

    /**
     * 批量上下架
     * @param ids
     * @param sellStauts
     */
    @Override
    public void batchUpdateSellStauts(Integer[] ids, Integer sellStauts) {
        productMapper.batchUpdateSellStauts(ids, sellStauts);
    }

    /**
     * 后台商品列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectListForAdmin();
        PageInfo pageInfo = new PageInfo(products);
        return  pageInfo;
    }

    /**
     * 前台商品详情
     * @param id
     * @return
     */
    @Override
    public Product detail(Integer id) {
        Product product = productMapper.selectByPrimaryKey(id);
        return  product;
    }

    /**
     * 返回一个带分页信息的商品列表
     * @param productReq
     * @return
     */
    @Override
    public PageInfo list(ProductReq productReq) {

        ProductListQuery productListQuery = new ProductListQuery();

        //搜索处理
        if (!StringUtils.isEmpty(productReq.getKetword())) {
            String keyword = new StringBuilder().append("%").append(productReq.getKetword()).append("%").toString();
            productListQuery.setKeyword(keyword);
        }

        //目录处理：递归查询该商品下所有子商品的categoryId，最后拿到一个categoryId List
        if (productReq.getCategoryId() != null) {
            List<CategoryVo> categoryVoList = categoryService.listForCustomer(productReq.getCategoryId());
            ArrayList<Integer> categoryIds = new ArrayList<>();
            categoryIds.add(productReq.getCategoryId());
            getCateoryIds(categoryVoList, categoryIds);
            productListQuery.setCategoryIds(categoryIds);
        }

        //排序
        String orderBy = productReq.getOrderBy();
        if(Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
            PageHelper.startPage(productReq.getPageNum(), productReq.getPageSize(), orderBy);
        }else {
            PageHelper.startPage(productReq.getPageNum(), productReq.getPageSize());
        }
        List<Product> productList = productMapper.selectList(productListQuery);
        PageInfo pageInfo = new PageInfo(productList);
        return pageInfo;
    }

    private void getCateoryIds(List<CategoryVo> categoryVoList, ArrayList<Integer> categoryIds) {
        for (int i=0; i<categoryVoList.size(); i++) {
            CategoryVo categoryVo = categoryVoList.get(i);
            if (categoryVo != null) {
                categoryIds.add(categoryVo.getId());
                getCateoryIds(categoryVo.getChildCategory(), categoryIds);
            }
        }
    }

    /**
     * 更新商品库存
     * @param productId
     * @param stock
     */
    @Override
    public void updateStock(Integer productId, Integer stock){
        Product product = new Product();
        product.setId(productId);
        product.setStock(stock);
        productMapper.updateByPrimaryKeySelective(product);
    }
}
