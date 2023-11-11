package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "订单接口")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 订单条件查询
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("conditionSearch")
    @ApiOperation("订单条件查询")
    public Result<PageResult> history(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("订单条件查询：{}",ordersPageQueryDTO);
        PageResult pageResult=orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<OrderStatisticsVO>statistics(){
        log.info("各个状态的订单数量统计");
        OrderStatisticsVO orderStatisticsVO=orderService.getStatistics();
        return Result.success(orderStatisticsVO);
    }
    /**
     * 查询订单信息
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单信息")
    public Result<OrderVO> orderDetail(@PathVariable Long id){
        log.info("查询订单信息：{}",id);
        OrderVO orderVO=orderService.getWithDetailById(id);
        return Result.success(orderVO);
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result<String>confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        log.info("接单");
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }
    /**
     * 拒单
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result<String>rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        log.info("拒单");
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }
    /**
     * 取消订单
     *
     * @return
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) throws Exception {
        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }
    /**
     * 派送订单
     *
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result delivery(@PathVariable("id") Long id) {
        orderService.delivery(id);
        return Result.success();
    }
    /**
     * 完成订单
     *
     * @return
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable("id") Long id) {
        orderService.complete(id);
        return Result.success();
    }
}
