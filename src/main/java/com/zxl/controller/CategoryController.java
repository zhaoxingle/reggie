package com.zxl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxl.common.R;
import com.zxl.entity.Category;
import com.zxl.entity.Dish;
import com.zxl.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        boolean save = categoryService.save(category);
        return R.success("新增菜品成功！");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        Page<Category> page1 = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(page1,queryWrapper);
        return R.success(page1);
    }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam(value = "ids") Long id){
        log.info("id:{}",id);
        categoryService.removeById(id);

        //categoryService.removeById(id);
        return  R.success("delete successfully");

    }

    /**
     * 修改套餐是回显
     * @param category
     * @return
     */
    @GetMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改信息");
        categoryService.updateById(category);

        //categoryService.removeById(id);
        return  R.success("update successfully");

    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());

        queryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getCreateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);

    }
}
