package com.zxl.dto;

import com.zxl.entity.Dish;
import com.zxl.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
/**
 * DTO(Data Transfer Object) 即数据传输对象,是一种设计模式。  相当于对dish的拓展。
 * DTO的作用是在进程间传递数据,它具有以下特征:
 * DTO是一个简单的传输类,用来封装数据,通常只包含字段与getter/setter方法。
 * DTO不包含任何业务逻辑,只关注数据的传输。
 * 一个DTO可以通过网络传输到远程进程,或不同层之间传递。
 * 不同的DTO可以传输不同的数据。
 */