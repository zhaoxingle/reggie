package com.zxl.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理类
 */
@Slf4j
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody //讲return的java对象转换为json
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //用来处理新增用户失败,新增的用户名可能已经存在。
        log.error(ex.getMessage());
        String message = ex.getMessage(); //可以debug看一下message
        if (message.contains("Duplicate entry")) {
            String name=message.split(" ")[2];
            return R.error("name:"+name+"已存在！");
        }
        return R.error("未知错误！");
    }

    /**
     * 全局处理异常，用来处理删除菜品时，可能有关联，无法删除
     * @param ex
     * @return
     */

    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){

        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }
}
