package com.zxl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.common.BaseContext;
import com.zxl.entity.*;
import com.zxl.mapper.OrdersMapper;
import com.zxl.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private UserService userService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private AddressBookService addressBook;

    @Autowired
    private OrderDetailService orderDetailService;
    @Override
    public void submit(Orders orders) {
        //{remark: "", payMethod: 1, addressBookId: "1723219622649421826"}
        //根据BaseContext获得此时用户id
        Long currentId = BaseContext.getCurrentId();
        //讲数据写到Orders表里
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId,currentId);
        User user = userService.getOne(queryWrapper);

        AddressBook addressBook1 = addressBook.getById(orders.getAddressBookId());
        //需要额外计算amount
        //遍历dish表里面有价格，根据ShoppingCart表里查找有哪些菜和数量
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(shoppingCartLambdaQueryWrapper);

        long orderId = IdWorker.getId();//订单号
        AtomicInteger amount = new AtomicInteger(0);
        //讲数据写到OrderDetail表里
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
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

        orderDetailService.saveBatch(orderDetails);

        //讲数据写到Orders表里
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook1.getConsignee());
        orders.setPhone(addressBook1.getPhone());
        orders.setAddress((addressBook1.getProvinceName() == null ? "" : addressBook1.getProvinceName())
                + (addressBook1.getCityName() == null ? "" : addressBook1.getCityName())
                + (addressBook1.getDistrictName() == null ? "" : addressBook1.getDistrictName())
                + (addressBook1.getDetail() == null ? "" : addressBook1.getDetail()));

        this.save(orders);

        //清空购物车
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
    }
}
