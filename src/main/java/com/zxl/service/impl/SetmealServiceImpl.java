package com.zxl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.common.CustomException;
import com.zxl.dto.SetmealDto;
import com.zxl.entity.DishFlavor;
import com.zxl.entity.Setmeal;
import com.zxl.entity.SetmealDish;
import com.zxl.mapper.SetmealMapper;
import com.zxl.service.SetmealDishService;
import com.zxl.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private  SetmealService setmealService;
    /**
     * 根据setmealdto里的两部分数据分别保存到两个表中
     * @param setmealDto
     */
    @Transactional
    public void saveWithdish(SetmealDto setmealDto) {
        //先保存到setmeal表中
        this.save(setmealDto);

        //在保存setmealDish表中
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //但是前端返回的数据里面这里面不包含setmealId这个属性，因此要收到赋值在写入表中
        List<SetmealDish> collect = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(collect);


    }

    /**
     * 删除两张表
     * @param ids
     */
    @Transactional
    @Override
    public void deleteWithDish(List<Long> ids) {
        //1.先判断这个表是否是在售状态
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(!ids.isEmpty(),Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        if (count>0){
            //不能删除
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //2.删除setmeal表
        this.removeByIds(ids);

        //3，删除setmealDish表
        //由于这个ids和删除setmealDish表里的setmealId对应，和Id对应不上。因此不能直接删除
        //delete * from setmealDish where setmealId in ids
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);

    }

    @Override
    public void updateWithdish(SetmealDto setmealDto) {
        //更新setmeal表
        this.updateById(setmealDto);

        //在更新setmeal_dish表里的字段
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //前端发送的数据没有setmealid
        List<SetmealDish> collect = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(collect);
    }


}
