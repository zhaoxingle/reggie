package com.zxl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxl.common.R;
import com.zxl.entity.Employee;
import com.zxl.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping(path = "/login")  //因为前端的请求是post，所以要PostMapping
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1.因为前端返回的数据是json格式，因此需要@RequestBody
        // 2.返回数据{’username‘:'dfsdaf','password':'1234560'},这里面的返回的key要和employee里的属性对应，否则无法封装。
        //HttpServletRequest 是用来，因为登录成功后会将employee写到session里，到时候可以用request.getsession（）。

        //1.加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2.根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee one = employeeService.getOne(queryWrapper); //因为username是唯一的。可以使用getOne()。得到数据库查到的对象

        //如果没查到
        if (one == null) {
            return R.error("登录失败");
        }
        //如果数据库中记录的密码不等于输入的密码。
        if (!one.getPassword().equals(password)) {
            return R.error("密码不对");
        }
        //如果员工状态禁用
        if (one.getStatus() == 0) {
            return R.error("用户被禁用");
        }

        //登录成功，将登录人id写入到session中，相当于浏览器的缓存
        request.getSession().setAttribute("employee", one.getId());
        return R.success(one);
    }

    @PostMapping(path = "/logout")
    public R<String> logout(HttpServletRequest request) {
        //Employee employee = new Employee();
        request.getSession().removeAttribute("employee");
        return R.success("退出"); //这里退出
    }

    /**
     * 添加员工
     *
     * @param request
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        //设置一些用户没填写的数据
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));

        //employee.setCreateTime(LocalDateTime.now()); 在MyMetaObjectMapper类中统一处理
        //employee.setUpdateTime(LocalDateTime.now());在MyMetaObjectMapper类中统一处理

        //为什么登录的时候要id写入缓存  。登录成功，写入到session中，相当于浏览器的缓存
        //        request.getSession().setAttribute("employee",one.getId());
        //方便下面设置setCreateUser
        //Long employeeid = (Long) request.getSession().getAttribute("employee");在MyMetaObjectMapper类中统一处理
        //employee.setCreateUser(employeeid);在MyMetaObjectMapper类中统一处理
        //employee.setUpdateUser(employeeid);在MyMetaObjectMapper类中统一处理
        boolean save = employeeService.save(employee);   //添加可能新增用户失败,新增的用户名可能已经存在。添加GlobalExceptionHandler
        return R.success("添加员工成功"); //这里退出
    }

    /**
     * 员工分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //接受的参数名字要和前端返回的名字完全相同，如果不相同，可以加@RequestParam
        log.info(page + "" + pageSize);
        Page page1 = new Page(page, pageSize);

        //构建条件表达式
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        boolean flag;
        if (name == null) {
            flag = false;
        } else {
            flag = true;
        }
        queryWrapper.like(flag, Employee::getName, name);
        queryWrapper.orderByAsc(Employee::getCreateTime);
        //执行查询
        employeeService.page(page1, queryWrapper); //别忘记加这个参数queryWrapper
        return R.success(page1);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        //获取当前登录用户信息
        Long employeeid = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(employeeid);

        log.info("修改状态" + employee.toString());
        employeeService.updateById(employee);
        return R.success("修改成功！");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("get_by_id");
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }
}

