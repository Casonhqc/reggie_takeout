package com.Cason.reggie.Controller;

import com.Cason.reggie.DTO.DishDto;
import com.Cason.reggie.common.R;
import com.Cason.reggie.entity.Category;
import com.Cason.reggie.entity.Dish;
import com.Cason.reggie.entity.DishFlavor;
import com.Cason.reggie.service.CategoryService;
import com.Cason.reggie.service.DishFlavorService;
import com.Cason.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
 @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
    dishService.saveWithFlavor(dishDto);
    return R.success("新增菜品成功");
 }

    /**
     * 菜品分页显示
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
 @GetMapping("/page")
    public R<Page>page(int page,int pageSize,String name){
     Page<Dish> pageInfo = new Page<>(page,pageSize);
     Page<DishDto> pageDto = new Page<>();

     LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
     queryWrapper.like(name!= null,Dish::getName,name);
     queryWrapper.orderByDesc(Dish::getUpdateTime);

     dishService.page(pageInfo,queryWrapper);

     //对象拷贝
     BeanUtils.copyProperties(pageInfo,pageDto,"records");
     //把原先的page对象records取出来
     List<Dish> records = pageInfo.getRecords();
     //做一个新的page对象需要的records
     List<DishDto> list = records.stream().map((item) ->{
         DishDto dishDto = new DishDto();//新建一个dishDto来作为新的records的元素
         BeanUtils.copyProperties(item,dishDto);//需要的只是新增categoryName，而Dto继承了Dish，所以可其他属性复制

         Long categoryId = item.getCategoryId();//取出分类id
         Category category = categoryService.getById(categoryId);//根据id找到对应类并且创造对象
       if(category != null){
           String categoryName = category.getName();//调用方法找的name
           dishDto.setCategoryName(categoryName);
       }
         return dishDto;
     }).collect(Collectors.toList());
     pageDto.setRecords(list);//把修改后的record注入page对象
     return R.success(pageDto);
 }

    /**
     * 根据id查到对应的菜品和口味，实现信息复现
     * @param id
     * @return
     */
 @GetMapping("{id}")
    public R<DishDto> get(@PathVariable Long id){

     DishDto dishDto = dishService.getByIdWithFlavor(id);
     return R.success(dishDto);
 }
    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    @GetMapping("/list")
    public R<List<DishDto>> list (Dish dish){//传过来的是categoryId，直接用Dish存
        LambdaQueryWrapper<Dish>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //该条件筛选出状态为1的菜品
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);
        List<DishDto> dishDtoList = list.stream().map((item)->{
            Long dishId = item.getId();
            DishDto dishDto = dishService.getByIdWithFlavor(dishId);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }


}
