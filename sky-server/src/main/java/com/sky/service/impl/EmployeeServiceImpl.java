package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO md5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     */
    public void save(EmployeeDTO employeeDTO){
//        System.out.println("当前线程" + Thread.currentThread().getId());
        Employee employee = new Employee();
        //对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);
        //设置账号状态，默认为正常
        employee.setStatus(StatusConstant.ENABLE);
        //设置默认密码,并加密
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        //设置创建和修改时间
        LocalDateTime createtime = LocalDateTime.now();
        employee.setCreateTime(createtime);
        employee.setUpdateTime(createtime);
        //设置当前记录创建人id和修改人id
        //TODO 从线程存储空间中获取用户id
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());
        //使用持久层插入到数据库
        employeeMapper.insert(employee);
    }

    /**
     * 分页查询
     * @param employeePagequeryDTO
     * @return
     */
    public PageResult pageQuery(EmployeePageQueryDTO employeePagequeryDTO){
        //select * from employee Limit 0,10
        //开始分页查询，pom.xml已配置好PageHelper
        PageHelper.startPage(employeePagequeryDTO.getPage(),employeePagequeryDTO.getPageSize());
        //还需把employeePagequeryDTO中的name拼到sql语句去,遵循PageHelper的规则写法：
        Page<Employee> page= employeeMapper.pageQuery(employeePagequeryDTO);
        //处理成需要的PageResult对象
        long total = page.getTotal();
        List<Employee> records= page.getResult();
        return new PageResult(total, records);
    }
}
