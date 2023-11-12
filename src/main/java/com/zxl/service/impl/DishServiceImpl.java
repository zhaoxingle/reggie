package com.zxl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.dto.DishDto;
import com.zxl.entity.Dish;
import com.zxl.entity.DishFlavor;
import com.zxl.mapper.DishMapper;
import com.zxl.service.DishFlavorService;
import com.zxl.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品，需要控制两张表，同时保存flavor
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto); //只能保存部分

        Long id = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors=flavors.stream().map((items) ->{
            items.setDishId(id);
            return items;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查找dish表还有dishflavor表
     * @param id
     * @return
     */
    @Override
    public DishDto getByIDWithFlavor(Long id) {
        //查找dish表
        Dish dish = this.getById(id);
        DishDto dishDto=new DishDto();

        BeanUtils.copyProperties(dish,dishDto);

        //查找dishflavor表
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(dishFlavorList);

        return dishDto;
    }
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表
        this.updateById(dishDto);

        //在更行dish_falvor表里的字段
        //1.先删除原先表里的flavor数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //2.在加入dishDto的flavor数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        //由于flavors里面只有flavors，没有id
        flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }


}
