package com.Cason.reggie.service.impl;

import com.Cason.reggie.common.CustomException;
import com.Cason.reggie.entity.Category;
import com.Cason.reggie.entity.Dish;
import com.Cason.reggie.entity.Setmeal;
import com.Cason.reggie.mapper.CategoryMapper;
import com.Cason.reggie.service.CategoryService;
import com.Cason.reggie.service.DishService;
import com.Cason.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceIml extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    private SetmealService setmealService;
    /**
     * 根据id查询当前分类是否关联了菜品，然后再做删除
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        //查询该分类ID对应的菜品的总量
        int count1 = dishService.count(dishLambdaQueryWrapper);
        //如果存在关联菜品，抛出异常
        if(count1 > 0){
            throw new CustomException("当前分类下关联了"+String.valueOf(count1)+"个菜品，不能删除");
        }

        LambdaQueryWrapper<Setmeal>setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);

        if(count2 > 0){
            throw new CustomException("当前分类下关联了"+String.valueOf(count2)+"个套餐，不能删除");
        }
    super.removeById(id);

    }
}
