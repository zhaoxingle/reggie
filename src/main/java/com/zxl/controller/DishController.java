package com.zxl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxl.common.R;
import com.zxl.dto.DishDto;
import com.zxl.entity.Category;
import com.zxl.entity.Dish;
import com.zxl.entity.DishFlavor;
import com.zxl.entity.Setmeal;
import com.zxl.service.CategoryService;
import com.zxl.service.DishFlavorService;
import com.zxl.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    /**
     * 新增菜品
     * @param dish
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("dishdto:"+dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("保存dishdto成功");

    }

    @GetMapping("/page")
    public R<Page> list(int page,int pageSize,String name){
        Page<Dish> dishPage = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage=new Page<>(page,pageSize);

        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getCreateTime);
        dishService.page(dishPage,queryWrapper);

        //由于一些属性对不上。category_id 无法和想要显示的中文对应上
        //公共属性赋值
        //records记录的就是我们想要修改的数据。因此忽略赋值
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");

        List<Dish> records = dishPage.getRecords();
        List<DishDto> list=records.stream().map((items) -> {
            DishDto dishDto= new DishDto();
            BeanUtils.copyProperties(items,dishDto);
            Long categoryId = items.getCategoryId();
            //根据id查询分类对象
            Category serviceById = categoryService.getById(categoryId);
            if(serviceById!=null){
                String name1 = serviceById.getName();
                dishDto.setCategoryName(name1);
            }
            return dishDto;

        }).collect(Collectors.toList());


        dishDtoPage.setRecords(list);

        return  R.success(dishDtoPage);
    }

    /**
     * 根据id回显修改菜品，
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){

        DishDto dishDto = dishService.getByIDWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品，需要改两个表
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功");

    }

    /**
     * 根据category_id 查找dish表，返回所有数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish)
//    {
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        queryWrapper.eq(Dish::getStatus,1);
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getCreateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//    }
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish)
    {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getCreateTime);

        List<Dish> list = dishService.list(queryWrapper);

        //但是里面没有flavor
        List<DishDto> collect = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryid = item.getCategoryId();

            Category category = categoryService.getById(categoryid);
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(collect);
    }

    @PostMapping("/status/{status}")
    public R<String> changeStatus(@RequestParam Long ids,@PathVariable Integer status){
        Dish dish = new Dish();
        dish.setStatus(status);
        UpdateWrapper<Dish> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(ids!=null,"id",ids).set("status",status);
        dishService.update(dish,updateWrapper);
        return R.success("删除成功！");
    }
    @DeleteMapping
    public R<String> deletedish(@RequestParam Long ids){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getId,ids);
        dishService.remove(queryWrapper);
        return R.success("删除菜品成功");
    }
}
