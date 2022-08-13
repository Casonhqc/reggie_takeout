package com.Cason.reggie.service.impl;

import com.Cason.reggie.entity.ShoppingCart;
import com.Cason.reggie.mapper.ShoppingCartMapper;
import com.Cason.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>implements ShoppingCartService {
}
