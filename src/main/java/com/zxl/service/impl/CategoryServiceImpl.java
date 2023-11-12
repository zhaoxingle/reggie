package com.zxl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.common.CustomException;
import com.zxl.entity.Category;
import com.zxl.entity.Dish;
import com.zxl.entity.Setmeal;
import com.zxl.mapper.CategoryMapper;
import com.zxl.service.CategoryService;
import com.zxl.service.DishService;
import com.zxl.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    DishService dishService;

    @Autowired
    SetmealService setmealService;
    @Override
    public void remove(Long id){
        //查看删除前是否关联菜品
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(queryWrapper);
        if(count>0){
            //抛出业务异常
            throw new CustomException("当前分类下关联了菜品，无法删除");
        }

        LambdaQueryWrapper<Setmeal> queryWrapper2=new LambdaQueryWrapper<>();
        queryWrapper2.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(queryWrapper2);
        if(count2>0){
            //抛出业务异常
        }
    }
}
