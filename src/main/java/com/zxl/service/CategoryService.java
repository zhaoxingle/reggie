package com.zxl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


public interface CategoryService extends IService<Category> {//这里代表这个service能够操控Category这张表。
    void remove(Long id);
}
