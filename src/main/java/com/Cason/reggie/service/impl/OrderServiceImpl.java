package com.Cason.reggie.service.impl;

import com.Cason.reggie.common.BaseContext;
import com.Cason.reggie.common.CustomException;
import com.Cason.reggie.entity.*;
import com.Cason.reggie.mapper.OrderMapper;
import com.Cason.reggie.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders>implements OrderService {


    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Transactional
    public void submit(Orders orders) {
    //获得用户id
        Long userId = BaseContext.getCurrentId();

        //查询当前用户的购物车数据，集合
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);

        if(shoppingCarts == null || shoppingCarts.size() == 0){
            throw new CustomException("购物车为空");
        }
        //查询用户数据
       User user= userService.getById(userId);

        //查询地址信息
        Long addressId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressId);

        if(addressBook == null){
            throw new CustomException("地址有误，不能下单");
        }
       long orderId = IdWorker.getId();//订单号

        AtomicInteger amount = new AtomicInteger(0);
        //遍历购物车集合，将每个元素的信息注入到orderDetail中
       List<OrderDetail> list = shoppingCarts.stream().map((item)->{
            OrderDetail orderDetail = new OrderDetail();
           orderDetail.setOrderId(orderId);
           orderDetail.setNumber(item.getNumber());
           orderDetail.setDishFlavor(item.getDishFlavor());
           orderDetail.setDishId(item.getDishId());
           orderDetail.setSetmealId(item.getSetmealId());
           orderDetail.setName(item.getName());
           orderDetail.setImage(item.getImage());
           orderDetail.setAmount(item.getAmount());
           amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
           return orderDetail;
        }).collect(Collectors.toList());

        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);

        orderDetailService.saveBatch(list);

        //清除购物车数据
        shoppingCartService.remove(queryWrapper);
    }
}
