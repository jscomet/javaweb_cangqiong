package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单明细数据
     * @param orderDetails
     */
    void insertBatch(List<OrderDetail> orderDetails);

    /**
     * 根据order查询订单详细表
     * @param orderId
     * @return
     */
    @Select("select * from order_detail where order_id=#{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);

    /**
     * 根据订单ID获取菜品ID
     * @param orderId
     * @return
     */
    @Select("select dish_id from order_detail where order_id=#{orderId} and not ISNULL(dish_id)")
    List<Long> getDishIdsByOrderId(Long orderId);
}
