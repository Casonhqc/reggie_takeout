package com.Cason.reggie.Controller;

import com.Cason.reggie.common.BaseContext;
import com.Cason.reggie.common.R;
import com.Cason.reggie.entity.ShoppingCart;
import com.Cason.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //设置用户id，指定当前是哪个用户购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //查询当前菜品或套餐是否在购物车内
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        if(dishId != null){
            //说明加入的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }
        else{
            //加入的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //查询当前菜品或套餐在不在购物车
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        if(cartServiceOne != null){
            //如果已经存在，直接在原基础上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number+1);
            shoppingCartService.updateById(cartServiceOne);
        }
        else{
            //如果不存在就直接添加到购物车
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }
    return R.success(cartServiceOne);
    }
    @GetMapping("/list")
    public R<List<ShoppingCart> > list(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.gt(ShoppingCart::getNumber,0);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }
    @PostMapping("/sub")
    public R<ShoppingCart> update(@RequestBody ShoppingCart shoppingCart){
        //设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //先通过用户id定位
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        if(shoppingCart.getDishId() != null){
            //要修改的是菜品,通过菜品id定位
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }
        else{
            //通过套餐id进行定位
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //生成对应的对象
        ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);

        shoppingCart1.setNumber(shoppingCart1.getNumber()-1);


        //修改数据库
        shoppingCartService.updateById(shoppingCart1);

        return  R.success(shoppingCart1);
    }
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车");

    }
}
