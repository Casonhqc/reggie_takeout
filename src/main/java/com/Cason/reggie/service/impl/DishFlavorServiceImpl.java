package com.Cason.reggie.service.impl;

import com.Cason.reggie.entity.DishFlavor;
import com.Cason.reggie.mapper.DishFlavorMapper;
import com.Cason.reggie.service.DishFlavorService;
import com.Cason.reggie.service.DishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>implements DishFlavorService {
}
