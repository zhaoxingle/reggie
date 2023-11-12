package com.zxl.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.entity.Employee;
import com.zxl.mapper.EmployeeMapper;
import com.zxl.service.EmployeeService;
import org.springframework.stereotype.Service;
//首先创建entity，然后创建controller 用来响应前端的请求。然后会使用 EmployeeServiceImpl ，然后在调用mapper操控数据库

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService {


}
