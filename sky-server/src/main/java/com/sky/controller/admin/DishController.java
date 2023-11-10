package com.sky.controller.admin;

import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    @Cacheable(cacheNames = "DishPageQueryCache")
    public Result<PageResult>page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询{}",dishPageQueryDTO);
        PageResult pageResult=dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 商品批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    @CacheEvict(cacheNames = {"ListDishVOCache","DishPageQueryCache"},allEntries = true)
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品批量删除：{}",ids);
        dishService.deleteBatch(ids);

//        //将所有的菜品缓存数据清理掉，所有以dish_开头的key
//        cleanCache("dish_*");

        return Result.success();
    }
    /**
     * 根据id查询菜品
     * @param id
     * @return Result.success(dishVO)
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品：{}",id);
        DishVO dishVO=dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * "新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    @CacheEvict(cacheNames = {"ListDishVOCache","DishPageQueryCache"},key="#dishDTO.categoryId")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);

//        //清除缓存数据
//        String key="dish_"+dishDTO.getCategoryId();
//        cleanCache(key);
        return Result.success();
    }

    /**
     * 修改商品
     * @param dishDTO
     * @return Result.success()
     */
    @PutMapping
    @ApiOperation("修改商品")
    @CacheEvict(cacheNames = {"ListDishVOCache","DishPageQueryCache"},allEntries = true)
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改商品:{}",dishDTO);
        dishService.updateWithFlavor(dishDTO);

//        //将所有的菜品缓存数据清理掉，所有以dish_开头的key
//        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 启用禁用菜品
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用菜品")
    @CacheEvict(cacheNames = {"ListDishVOCache","DishPageQueryCache"},allEntries = true)
    public Result<String> startOrStop(@PathVariable("status") Integer status, Long id){
        dishService.startOrStop(status,id);

//        //将所有的菜品缓存数据清理掉，所有以dish_开头的key
//        cleanCache("dish_*");

        return Result.success();
    }

    private void cleanCache(String pattern){
        Set keys=redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    @Cacheable(cacheNames = "ListDishVOCache",key="#categoryId")
    public Result<List<DishVO>> list(Long categoryId) {
        //构造redis中的key，规则：dish_分类id
//        String key = "dish_" + categoryId;
//        //查询redis中是否存在菜品
//        List<DishVO>list=(List<DishVO>)redisTemplate.opsForValue().get(key);
//        if(list!=null&& list.size()>0){
//            log.info("从缓存中获取：{}",categoryId);
//            //如果存在，直接返回数据
//            return Result.success(list);
//        }
        log.info("根据分类id查询菜品{}",categoryId);
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品
        List<DishVO>list = dishService.listWithFlavor(dish);

//        redisTemplate.opsForValue().set(key,list);

        return Result.success(list);
    }
}
