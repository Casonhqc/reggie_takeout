package com.Cason.reggie.Controller;

import com.Cason.reggie.common.R;
import com.Cason.reggie.entity.Employee;
import com.Cason.reggie.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

   @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //将密码进行m5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //在数据库中查询用户名
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();//查询器
        queryWrapper.eq(Employee :: getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //如果没有查询到，返回登陆失败
        if(emp == null){
            return R.error("登陆失败");

        }
        //查到用户开始比对密码，如果不一致返回失败
        if(!emp.getPassword().equals(password)){
            return R.error("登陆失败");
        }
        //查看用户的状态
        if(emp.getStatus() == 0){
            return R.error("账户已禁用");
        }

        //登录成功
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);

    }

    @PostMapping("/logout")
    public R<String> logout (HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息:{}",employee.toString());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
      //  employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

//        Long empId = (Long)request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);
        return R.success("新增成功");//研究一下前端是如何处理response进行跳转的
    }

    /**
     * 员工信息分页和查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //先创建一个分页构造器
        Page pageInfo = new Page(page,pageSize);
        //生成条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(!StringUtils.isEmpty(name),Employee::getName,name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 员工信息修改
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

        //获得修改人
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        //设置修改人
//        employee.setUpdateUser(empId);
//        //设置更新时间
//        employee.setUpdateTime(LocalDateTime.now());
        //调用service
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 编辑时信息的复现
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public  R<Employee> getById(@PathVariable Long id){
        Employee employee =employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到员工信息");
    }
}
