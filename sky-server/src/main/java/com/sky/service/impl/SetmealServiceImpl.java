package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 套餐业务实现
 */
@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

    /**
     * 分页查询
     *
     * @param setmealPageQueryDTO
     * @return total+Page<SetmealVO>
     */
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());

        Page<SetmealVO> page=setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 新增套餐
     *
     * @param setmealDTO
     * @return
     */
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        //向套餐表中插入数据
        setmealMapper.insert(setmeal);

        Long setmealId=setmeal.getId();
        //获取insert语句生成的主键值
        List<SetmealDish> dishes=setmealDTO.getSetmealDishes();
        if(dishes!=null&&dishes.size()>0){
            dishes.forEach(setmealDish ->{
                setmealDish.setSetmealId(setmealId);
            } );
        }
        setmealDishMapper.insertBatch(dishes);
    }

    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    public SetmealVO getByIdWithDishes(Long id) {
        //根据id获取套餐信息
        Setmeal setmeal=setmealMapper.getById(id);
        //根据套餐id获取菜品信息
        List<SetmealDish>  setmealDishes=setmealDishMapper.getBySetmealId(id);

        //将查询到的数据封装到VO中
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 修改套餐信息
     *
     * @param setmealDTO
     * @return
     */
    @Transactional
    public void updateWithDishes(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //更改套餐信息
        setmealMapper.update(setmeal);

        Long setmealId = setmeal.getId();
        //重新插入菜品信息
        setmealDishMapper.deleteByDishId(setmealId);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes!=null&&setmealDishes.size()>0){
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });
        }
        setmealDishMapper.insertBatch(setmealDishes);

    }

    /**
     * 启用禁用套餐
     *
     * @param status
     * @param id
     * @return
     */
    public void startOrStop(Integer status, Long id) {
        if(status.equals(StatusConstant.ENABLE)){
            List<Long> dishIds=setmealDishMapper.getStoppedDishIdsBySetmealId(id);
            if(dishIds!=null&&dishIds.size()>0){
                throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }

    /**
     * 批量删除套餐
     *
     * @param ids
     * @return
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断是否有起售中的套餐
        for(Long id:ids){
            Setmeal setmeal=setmealMapper.getById(id);
            if(setmeal.getStatus().equals(StatusConstant.ENABLE) ){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        //删除套餐表中的套餐信息
        setmealMapper.deleteByIds(ids);
        setmealDishMapper.deleteBySetmealIds(ids);
    }
}
