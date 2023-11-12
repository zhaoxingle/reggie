package com.zxl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxl.common.BaseContext;
import com.zxl.common.R;
import com.zxl.dto.OrdersDto;
import com.zxl.entity.OrderDetail;
import com.zxl.entity.Orders;
import com.zxl.entity.User;
import com.zxl.service.OrderDetailService;
import com.zxl.service.OrdersService;
import com.zxl.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    private UserService userService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private OrdersService ordersService;
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("保存成功");
    }

    @GetMapping("/userPage")
    public R<Page> list(int page,int pageSize){
        Page<Orders> ordersPage = new Page<>(page,pageSize);

        Page<OrdersDto> ordersDtoPage = new Page<>(page,pageSize);

        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,currentId);
        Page<Orders> page1 = ordersService.page(ordersPage, queryWrapper);

        BeanUtils.copyProperties(page1,ordersDtoPage,"records");
        List<Orders> records = page1.getRecords();

        List<OrdersDto> collect = records.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            LambdaQueryWrapper<OrderDetail> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(OrderDetail::getOrderId, item.getId());

            LambdaQueryWrapper<User> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(User::getId, item.getUserId());
            User user = userService.getOne(queryWrapper2);
            List<OrderDetail> list = orderDetailService.list(queryWrapper1);
            ordersDto.setOrderDetails(list);
            ordersDto.setConsignee(item.getConsignee());
            ordersDto.setAddress(item.getAddress());
            ordersDto.setPhone(item.getPhone());
            ordersDto.setUserName(user.getName());
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(collect);
        return R.success(ordersDtoPage);
    }

}
