package com.Cason.reggie.service.impl;

import com.Cason.reggie.DTO.SetmealDto;
import com.Cason.reggie.common.CustomException;
import com.Cason.reggie.entity.Setmeal;
import com.Cason.reggie.entity.SetmealDish;
import com.Cason.reggie.mapper.SetmealMapper;
import com.Cason.reggie.service.SetmealDishService;
import com.Cason.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>implements SetmealService {


    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐，查询两个表，把dto的数据填入
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息，操作setmeal表，执行insert操作
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) ->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息，操作setmealDish表
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，操纵setmeal和setmeal两张表
     * @param ids
     */
    @Override
    public void deleteWithDish(List<Long> ids) {
        //查询当前ids中status为在售的数量count
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);

        if(count > 0){
            throw new CustomException("当前套餐在售，不能删除");

        }
        //如果可以删除
        this.removeByIds(ids);
        //处理setmealDish表
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(queryWrapper1);
    }
}
