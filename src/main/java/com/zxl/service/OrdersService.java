package com.zxl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.entity.Orders;

public interface OrdersService extends IService<Orders> {

    public void submit(Orders orders);
}
