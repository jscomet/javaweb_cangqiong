package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 插入菜品数据
     * @param dishes
     */
    void insertBatch(List<SetmealDish> dishes);

    /**
     * 根据套餐id获取中间表信息
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id=#{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);

    @Delete("delete  from setmeal_dish where setmeal_id=#{setmealId}")
    void deleteByDishId(Long setmealId);

    @Select("select sd.dish_id from setmeal_dish sd left outer join dish d on d.id=sd.dish_id where sd.setmeal_id=#{setmealId} and d.status=0")
    List<Long> getStoppedDishIdsBySetmealId(Long setmealId);

    void deleteBySetmealIds(List<Long> setmealIds);
}
