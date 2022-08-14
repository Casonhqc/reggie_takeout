package com.Cason.reggie.Controller;

import com.Cason.reggie.DTO.SetmealDto;
import com.Cason.reggie.common.R;
import com.Cason.reggie.entity.Category;
import com.Cason.reggie.entity.Setmeal;
import com.Cason.reggie.service.CategoryService;
import com.Cason.reggie.service.SetmealDishService;
import com.Cason.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息：{}",setmealDto.getSetmealDishes());
        setmealService.saveWithDish(setmealDto);

        return R.success("保存成功");
    }

    /**
     * 套餐界面的分页
     */
    @Autowired
    CategoryService categoryService;
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> pageDto = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,queryWrapper);

        BeanUtils.copyProperties(pageInfo,pageDto,"records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) ->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        pageDto.setRecords(list);
        return R.success(pageDto);
    }
@DeleteMapping
@CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> delete (@RequestParam List<Long> ids){
        setmealService.deleteWithDish(ids);
        return R.success("删除成功");
}
@PostMapping("/status/{status}")
    public R<String> update(@PathVariable int status,@RequestParam List<Long> ids){
        log.info("获取参数{},{}",status,ids);
        LambdaUpdateWrapper<Setmeal> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.in(Setmeal::getId,ids).set(Setmeal::getStatus,status);

        setmealService.update(queryWrapper);

        return R.success("修改状态成功");
}
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }
}

