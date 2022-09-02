package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        //创建查询构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据查询构造器，创建SQL的equal语句
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        //用Service服务去执行SQL语句
        int dishCount = dishService.count(dishLambdaQueryWrapper);

        if (dishCount > 0) {
            //存在dish关联，抛出一个业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);

        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);

        if(setmealCount > 0){
            //存在setmeal关联，抛出一个业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        log.info("要删除的ID是：{}",id);
        log.info(setmealLambdaQueryWrapper.toString());
        super.removeById(id);
    }
}
