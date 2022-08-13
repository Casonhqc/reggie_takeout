package com.Cason.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.Cason.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {

}