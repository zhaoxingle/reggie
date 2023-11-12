package com.zxl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.dto.DishDto;
import com.zxl.entity.Dish;
import org.springframework.stereotype.Service;


public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIDWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);
}
