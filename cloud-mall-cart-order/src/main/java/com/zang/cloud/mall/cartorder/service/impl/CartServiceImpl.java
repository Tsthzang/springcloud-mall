package com.zang.cloud.mall.cartorder.service.impl;


import com.zang.cloud.mall.cartorder.fegin.ProductFeginClient;
import com.zang.cloud.mall.cartorder.model.dao.CartMapper;
import com.zang.cloud.mall.cartorder.model.pojo.Cart;
import com.zang.cloud.mall.cartorder.service.CartService;
import com.zang.cloud.mall.cartorder.vo.CartVO;
import com.zang.cloud.mall.categoryproduct.model.pojo.Product;
import com.zang.cloud.mall.common.common.Constant;
import com.zang.cloud.mall.common.exception.ImoocException;
import com.zang.cloud.mall.common.exception.ImoocMallExceptionEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Resource
    CartMapper cartMapper;

    @Resource
    ProductFeginClient productFeginClient;

    /**
     * 返回购物车列表
     * @param userId
     * @return
     */
    @Override
    public List<CartVO> list(Integer userId) {
        List<CartVO> cartVOS = cartMapper.selectList(userId);
        for (int i = 0; i < cartVOS.size(); i++){
            CartVO cartVO = cartVOS.get(i);
            cartVO.setTotalPrice(cartVO.getPrice() * cartVO.getQuantity());
        }
        return cartVOS;
    }

    /**
     * 购物车添加商品
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    @Override
    public List<CartVO> add(Integer userId, Integer productId, Integer count) {
        validProduct(productId, count);
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
//            商品之前未添加到购物车
            cart = new Cart();
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setQuantity(count);
            cart.setSelected(Constant.Cart.CHECKED);
            cartMapper.insertSelective(cart);
        }else {
//            购物车已存在该商品
            count = cart.getQuantity() + count;
            Cart cartNew = new Cart();
            cartNew.setId(cart.getId());
            cartNew.setProductId(cart.getProductId());
            cartNew.setUserId(cart.getUserId());
            cartNew.setQuantity(count);
            cartNew.setSelected(Constant.Cart.CHECKED);
            cartMapper.updateByPrimaryKeySelective(cartNew);
        }
        return this.list(userId);
    }

    //校验商品信息
    private void validProduct(Integer productId, Integer count) {
        Product product = productFeginClient.detailForFegin(productId);
        //判断商品是否存在，上下架状态
        if(product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)){
            throw new ImoocException(ImoocMallExceptionEnum.NOT_SALE);
        }
        //判断商品库存
        if(count > product.getStock()){
            throw new ImoocException(ImoocMallExceptionEnum.NOT_ENOUGF);
        }
    }

    /**
     * 更新购物车商品
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    @Override
    public List<CartVO> update(Integer userId, Integer productId, Integer count) {
        validProduct(productId, count);
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
//            商品之前未添加到购物车
            throw new ImoocException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }else {
//            购物车已存在该商品,更新数量
            Cart cartNew = new Cart();
            cartNew.setId(cart.getId());
            cartNew.setProductId(cart.getProductId());
            cartNew.setUserId(cart.getUserId());
            cartNew.setQuantity(count);
            cartNew.setSelected(Constant.Cart.CHECKED);
            cartMapper.updateByPrimaryKeySelective(cartNew);
        }
        return this.list(userId);
    }

    /**
     * 删除购物车商品
     * @param userId
     * @param productId
     * @return
     */
    @Override
    public List<CartVO> delete(Integer userId, Integer productId) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
//            商品之前未添加到购物车
            throw new ImoocException(ImoocMallExceptionEnum.DELETE_FAILED);
        }else {
//            购物车已存在该商品,可以删除
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
        return this.list(userId);
    }

    /**
     * 选中/取消选中购物车商品
     * @param userId
     * @param productId
     * @param selected
     * @return
     */
    @Override
    public List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
//            商品之前未添加到购物车
            throw new ImoocException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }else {
//            购物车已存在该商品,可以选中/取消选中
            cartMapper.selectOrNot(userId, productId, selected);
        }
        return this.list(userId);
    }

    /**
     * 全选/全不选购物车商品
     * @param userId
     * @param selected
     * @return
     */
    @Override
    public List<CartVO> selectAllOrNot(Integer userId, Integer selected) {
        cartMapper.selectOrNot(userId, null, selected);
        return this.list(userId);
    }

}
