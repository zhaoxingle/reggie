package com.zxl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.dto.SetmealDto;
import com.zxl.entity.Setmeal;
import org.springframework.stereotype.Service;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {

    /**
     * 操作两个表
     * @param setmealDto
     */
    public void saveWithdish(SetmealDto setmealDto);

    /**
     * 删除setmeal表和setmealdish表
     */
    public void deleteWithDish(List<Long> ids);

    public void updateWithdish(SetmealDto setmealDto);
}
