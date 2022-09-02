package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
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

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新建菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品管理分页
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("我进来菜品管理页面了");

        //分页构造器对象
        Page<Dish> pageInfo = new Page(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo, queryWrapper);

        //对象拷贝,records里面装了所有Dish的信息，因为考虑到要往里面添加Category_name，所以不能直接拿过来放到dishDto里面
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        //拿出PageInfo对象里面的records对象集合
        List<Dish> records = pageInfo.getRecords();

//        使用stream循环records集合，整个目的是把category_name整成中文，然后塞给dishDto
        List<DishDto> list = records.stream().map((item) -> {
            //单独新建一个DishDto对象，用来塞东西进去
            DishDto dishDto = new DishDto();
            //把records集合里面遍历出来的都塞进去
            BeanUtils.copyProperties(item, dishDto);
            //获取分类的ID
            Long categoryId = item.getCategoryId();
            //根据id查询分类对象，得到分类名
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            //得到每个遍历的分类名后，塞进去
            dishDto.setCategoryName(categoryName);

            return dishDto;
        }).collect(Collectors.toList());


        //for循环仿照stream方式实现，原理理解了。代码方式不同而已
//        List<DishDto> list = new ArrayList<>();
//        for (Dish record : records) {
//            DishDto dishDto = new DishDto();
//            BeanUtils.copyProperties(record, dishDto);
//            Long categoryId = record.getCategoryId();
//            Category category = categoryService.getById(categoryId);
//            String categoryName = category.getName();
//            dishDto.setCategoryName(categoryName);
//            list.add(dishDto);
//        }
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 修改菜品信息-获取菜品信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        log.info("我进来修改菜品信息了");
        log.info("菜品ID：{}", id);
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 保存修改菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        log.info(list.toString());

        return R.success(list);

    }
}
