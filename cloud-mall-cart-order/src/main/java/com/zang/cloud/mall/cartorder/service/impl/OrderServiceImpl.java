package com.zang.cloud.mall.cartorder.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import com.zang.cloud.mall.cartorder.fegin.ProductFeginClient;
import com.zang.cloud.mall.cartorder.fegin.UserFeginClient;
import com.zang.cloud.mall.cartorder.model.dao.CartMapper;
import com.zang.cloud.mall.cartorder.model.dao.OrderItemMapper;
import com.zang.cloud.mall.cartorder.model.dao.OrderMapper;
import com.zang.cloud.mall.cartorder.model.pojo.Order;
import com.zang.cloud.mall.cartorder.model.pojo.OrderItem;
import com.zang.cloud.mall.cartorder.quest.CreateOrderReq;
import com.zang.cloud.mall.cartorder.service.CartService;
import com.zang.cloud.mall.cartorder.service.OrderService;

import com.zang.cloud.mall.cartorder.utils.OrderCodeFactory;
import com.zang.cloud.mall.cartorder.vo.CartVO;
import com.zang.cloud.mall.cartorder.vo.OrderItemVO;
import com.zang.cloud.mall.cartorder.vo.OrderVO;
import com.zang.cloud.mall.categoryproduct.common.ProductConstant;
import com.zang.cloud.mall.categoryproduct.model.pojo.Product;
import com.zang.cloud.mall.common.common.Constant;
import com.zang.cloud.mall.common.common.QRCodeGenerator;
import com.zang.cloud.mall.common.exception.ImoocException;
import com.zang.cloud.mall.common.exception.ImoocMallExceptionEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    CartService cartService;

    @Resource
    ProductFeginClient productFeginClient;

    @Resource
    CartMapper cartMapper;

    @Resource
    OrderMapper orderMapper;

    @Resource
    OrderItemMapper orderItemMapper;

    @Resource
    UserFeginClient userFeginClient;

    @Value("${file.upload.ip}")
    String ip;

    @Value("${file.upload.port}")
    Integer port;

    @Value("${file.upload.dir}")
    String FILE_UPLOAD_DIR;

    /**
     * ??????????????????
     * @param createOrderReq
     * @return
     */
    @Transactional(rollbackFor = Exception.class) //????????????????????????????????????????????????
    @Override
    public String create(CreateOrderReq createOrderReq) {
        //????????????ID
        Integer userId = userFeginClient.getUser().getId();

        //????????????????????????????????????
        List<CartVO> cartVOList = cartService.list(userId);
        ArrayList<CartVO> cartVOSListTemp = new ArrayList<>();
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            if (cartVO.getSelected().equals(Constant.Cart.CHECKED)) {
                cartVOSListTemp.add(cartVO);
            }
        }
        cartVOList = cartVOSListTemp;

        //??????????????????????????????
        if (CollectionUtils.isEmpty(cartVOList)) {
            throw new ImoocException(ImoocMallExceptionEnum.CART_EMPTY);
        }

        //???????????????????????????????????????????????????
        validSaleStatusStock(cartVOList);

        //?????????????????? ????????? ??????Item??????
        List<OrderItem> orderItemList = cartVOListToOrderItemList(cartVOList);

        //?????????
        for (int i = 0; i < orderItemList.size(); i++){
            OrderItem orderItem = orderItemList.get(i);
            Product product = productFeginClient.detailForFegin(orderItem.getProductId());
            int stock = product.getStock() - orderItem.getQuantity();
            if(stock < 0){
                throw new ImoocException(ImoocMallExceptionEnum.NOT_ENOUGF);
            }
            productFeginClient.updateStockforFegin(product.getId(), stock);
        }
        //????????????????????????????????????
        CleanCart(cartVOList);

        //????????????
        Order order = new Order();
        String orderNo = OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverName(createOrderReq.getReceiverName());
        order.setReceiverMobile(createOrderReq.getReceiverMobile());
        order.setReceiverAddress(createOrderReq.getReceiverAddress());
        order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAID.getCode());
        order.setPostage(0);
        order.setPaymentType(1);
        //?????????order???
        orderMapper.insertSelective(order);

        //?????????????????????orderItem???
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            orderItem.setOrderNo(order.getOrderNo());
            orderItemMapper.insertSelective(orderItem);
        }
        //????????????
        return orderNo;
    }

    //?????????????????????
    private Integer totalPrice(List<OrderItem> orderItemList) {
        Integer totalPrice = 0;
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    //????????????????????????????????????
    private void CleanCart(List<CartVO> cartVOList) {
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            cartMapper.deleteByPrimaryKey(cartVO.getId());
        }
    }

    //?????????????????? ????????? ??????Item??????
    private List<OrderItem> cartVOListToOrderItemList(List<CartVO> cartVOList) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartVO.getProductId());
            orderItem.setProductName(cartVO.getProductName());
            orderItem.setProductImg(cartVO.getProductImage());
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItem.setQuantity(cartVO.getQuantity());
            orderItem.setTotalPrice(cartVO.getTotalPrice());
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    //???????????????????????????????????????????????????
    private void validSaleStatusStock(List<CartVO> cartVOList) {

        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            Product product = productFeginClient.detailForFegin(cartVO.getProductId());
            //??????????????????????????????????????????
            if(product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)){
                throw new ImoocException(ImoocMallExceptionEnum.NOT_SALE);
            }
            //??????????????????
            if(cartVO.getQuantity() > product.getStock()){
                throw new ImoocException(ImoocMallExceptionEnum.NOT_ENOUGF);
            }
        }
    }

    /**
     * ????????????????????????
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO detail(String orderNo) {
        Order order = orderMapper.selectByorderNo(orderNo);
        //????????????????????????
        if (order == null) {
            throw  new ImoocException(ImoocMallExceptionEnum.NO_ORDER);
        }
        //????????????????????????????????????
        if (!order.getUserId().equals(userFeginClient.getUser().getId())) {
            throw  new ImoocException(ImoocMallExceptionEnum.NOT_YOUR_ORDER);
        }
        OrderVO orderVO = getOrderVO(order);
        return orderVO;
    }
    private OrderVO getOrderVO(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        //?????????????????????orderItemVOList
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItem, orderItemVO);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVOList(orderItemVOList);
        orderVO.setOrderStatusName(Constant.OrderStatusEnum.codeOf(orderVO.getOrderStatus()).getValue());
        return orderVO;
    }

    /**
     * ??????????????????
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo list(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize);

        List<Order> orderList = orderMapper.selectByUserId(userFeginClient.getUser().getId());

        return getPageInfo(orderList);
    }

    /**
     * ????????????
     * @param orderNo
     */
    @Override
    public void cancel(String orderNo){
        Order order = orderMapper.selectByorderNo(orderNo);
        if (order == null) {
            throw new ImoocException(ImoocMallExceptionEnum.NO_ORDER);
        }
        if (!order.getUserId().equals(userFeginClient.getUser().getId())) {
            throw  new ImoocException(ImoocMallExceptionEnum.NOT_YOUR_ORDER);
        }
        //????????????????????????????????????????????????????????????
        if(order.getOrderStatus().equals(Constant.OrderStatusEnum.DELIVERED.getCode()) ||
                order.getOrderStatus().equals(Constant.OrderStatusEnum.FINISHED.getCode())){
            throw  new ImoocException(ImoocMallExceptionEnum.WRONG_ORDER_STATUS);
        }
        order.setOrderStatus(Constant.OrderStatusEnum.CANCELED.getCode());
        order.setEndTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        if (count == 0) {
            if (order == null) {
                throw new ImoocException(ImoocMallExceptionEnum.UPDATE_FAILED);
            }
        }
    }

    /**
     * ?????????????????????
     * @param orderNo
     * @return
     */
    @Override
    public String qrcode(String orderNo) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String address = ip + ":" + port;
        String payUrl = "http://" + address + "/cart-order/pay?orderNo=" + orderNo;
        try {
            QRCodeGenerator
                    .generateQRCodeImage(payUrl, 350, 350,
                            FILE_UPLOAD_DIR + orderNo + ".png");
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String pngAddress = "http://" + address + "/cart-order/images/" + orderNo + ".png";
        return pngAddress;
    }

    /**
     * ??????????????????
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize);

        List<Order> orderList = orderMapper.selectByAll();
        return getPageInfo(orderList);
    }

    //?????????????????????????????????
    private PageInfo getPageInfo(List<Order> orderList) {
        List<OrderVO> orderVOList = new ArrayList<>();
        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            OrderVO orderVO = getOrderVO(order);
            orderVOList.add(orderVO);
        }
        PageInfo pageInfo = new PageInfo(orderVOList);
        return  pageInfo;
    }

    /**
     * ????????????
     * @param orderNo
     */
    @Override
    public void pay(String orderNo){
        Order order = orderMapper.selectByorderNo(orderNo);
        if (order == null) {
            throw new ImoocException(ImoocMallExceptionEnum.NO_ORDER);
        }
        if (order.getOrderStatus() == Constant.OrderStatusEnum.NOT_PAID.getCode()) {
            order.setOrderStatus(Constant.OrderStatusEnum.PAID.getCode());
            order.setPayTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw  new ImoocException(ImoocMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    /**
     * ???????????????
     * @param orderNo
     */
    @Override
    public void delivered(String orderNo) {
        Order order = orderMapper.selectByorderNo(orderNo);
        if (order == null) {
            throw new ImoocException(ImoocMallExceptionEnum.NO_ORDER);
        }
        if (order.getOrderStatus() == Constant.OrderStatusEnum.PAID.getCode()) {
            order.setOrderStatus(Constant.OrderStatusEnum.DELIVERED.getCode());
            order.setDeliveryTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw  new ImoocException(ImoocMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    /**
     * ????????????
     * @param orderNo
     */
    @Override
    public void finish(String orderNo) {
        Order order = orderMapper.selectByorderNo(orderNo);
        if (order == null) {
            throw new ImoocException(ImoocMallExceptionEnum.NO_ORDER);
        }
        //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        if (userFeginClient.getUser().getRole().equals(1)
                && !order.getUserId().equals(userFeginClient.getUser().getId())) {
            throw new ImoocException(ImoocMallExceptionEnum.NOT_YOUR_ORDER);
        }
        //???????????????????????????
        if (order.getOrderStatus() == Constant.OrderStatusEnum.DELIVERED.getCode()) {
            order.setOrderStatus(Constant.OrderStatusEnum.FINISHED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw  new ImoocException(ImoocMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }
}
