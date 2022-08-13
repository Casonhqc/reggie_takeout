package com.Cason.reggie.service.impl;

import com.Cason.reggie.DTO.DishDto;
import com.Cason.reggie.entity.Dish;
import com.Cason.reggie.entity.DishFlavor;
import com.Cason.reggie.mapper.DishMapper;
import com.Cason.reggie.service.DishFlavorService;
import com.Cason.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j

public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品基本信息到菜品表里，dishDto继承了dish
        this.save(dishDto);

        Long dishId = dishDto.getId(); //菜品id

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存口味数据到表中，Batch代表批量操作
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //先查dish表
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto); //拷贝dish

        //查询dishFlavor表，按照id查
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);

        //将dishFlavor的集合加到DTO对应属性
        dishDto.setFlavors(list);

        return dishDto;
    }

    //更新菜品信息，同时要更新口味表
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品表
        this.updateById(dishDto);

        //先清理当前菜品的所有口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);
        //再添加当前提交过来的口味集合
        List<DishFlavor> flavors = dishDto.getFlavors();
        //要为集合的每个元素添加dishId
        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }


}
