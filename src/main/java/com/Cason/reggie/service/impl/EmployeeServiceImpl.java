package com.Cason.reggie.service.impl;

import com.Cason.reggie.entity.Employee;
import com.Cason.reggie.mapper.EmployeeMapper;
import com.Cason.reggie.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
