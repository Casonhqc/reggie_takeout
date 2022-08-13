package com.Cason.reggie.service;

import com.Cason.reggie.DTO.DishDto;
import com.Cason.reggie.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DishService extends IService<Dish> {
    //操作两张表，填入新增菜品和口味
    public  void  saveWithFlavor(DishDto dishDto);
    public DishDto getByIdWithFlavor(Long id);

   public  void updateWithFlavor(DishDto dishDto);
}
