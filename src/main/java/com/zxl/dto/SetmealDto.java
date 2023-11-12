package com.zxl.dto;

import com.zxl.entity.Setmeal;
import com.zxl.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
