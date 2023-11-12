package com.zxl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxl.common.R;
import com.zxl.dto.DishDto;
import com.zxl.dto.SetmealDto;
import com.zxl.entity.Category;
import com.zxl.entity.Setmeal;
import com.zxl.entity.SetmealDish;
import com.zxl.service.CategoryService;
import com.zxl.service.SetmealDishService;
import com.zxl.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.PrivateKey;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;
    /**
     *
     * 保存到setmeal表和setmealdish表
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto)
    {
        setmealService.saveWithdish(setmealDto);
        return R.success("保存成功！");
    }

    @GetMapping("/page")
    public R<Page> list(int page,int pageSize,String name){
        Page<Setmeal>  pageInfo= new Page<>(page, pageSize);
        Page<SetmealDto>  pageInfoDto= new Page<>(page, pageSize);
        BeanUtils.copyProperties(pageInfo,pageInfoDto,"records");

        //先获取pageInfo信息
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name),Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,queryWrapper);
        //得到记录
        List<Setmeal> records = pageInfo.getRecords();


        List<SetmealDto> collect = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            //获得id
            Long categoryId = item.getCategoryId();
            //查询category表
            Category id = categoryService.getById(categoryId);
            String categoryName = id.getName();
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());
        //少了一个categoryname属性，因此需要创建SetmealDto
        pageInfoDto.setRecords(collect);

        return R.success(pageInfoDto);

    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        //因为需要删除两张表，所以单独封装一个方法到service中
        setmealService.deleteWithDish(ids);
        return R.success("删除成功！");
    }

    @PostMapping("/status/{status}")
    public R<String> changeStatus(@RequestParam Long ids,@PathVariable Integer status){
        //因为需要删除两张表，所以单独封装一个方法到service中
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        UpdateWrapper<Setmeal> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(ids!=null,"id",ids).set("status",status);
        setmealService.update(setmeal,updateWrapper);
        return R.success("删除成功！");
    }

    /**
     * 根据categoryId查询setmeal信息。
     * @param categoryId
     * @param status
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Long categoryId,int status){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId!=null,Setmeal::getCategoryId,categoryId);
        queryWrapper.eq(Setmeal::getStatus,status);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        return R.success(setmealList);

    }
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        //应为这里面没有菜品  需要菜品和categoryname
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = setmealService.getById(id);
        BeanUtils.copyProperties(setmeal,setmealDto);
        Long categoryId = setmeal.getCategoryId();
        Long setmealId = setmeal.getId();

        //先找名字
        Category category = categoryService.getById(categoryId);
        String name = category.getName();
        setmealDto.setName(name);
        //在找菜
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(list);
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> changeSetmeal(@RequestBody SetmealDto setmealDto)
    {
        setmealService.updateWithdish(setmealDto);
        return R.success("修改成功！");
    }
}
