package com.sky.controller.user;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
@Api(tags = "C端-订单接口")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> sumbit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单：{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO=orderService.sumbitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);

    }
    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    /**
     * 历史订单分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/historyOrders")
    @ApiOperation("历史订单分页查询")
    public Result<PageResult>history(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("历史订单分页查询：{}",ordersPageQueryDTO);
        PageResult pageResult=orderService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 查询订单信息
     * @param id
     * @return
     */
    @GetMapping("orderDetail/{id}")
    @ApiOperation("查询订单信息")
    public Result<OrderVO> orderDetail(@PathVariable Long id){
        log.info("查询订单信息：{}",id);
        OrderVO orderVO=orderService.getWithDetailById(id);
        return Result.success(orderVO);
    }

    /**
     * 取消订单
     * @param id
     * @return
     */
    @PutMapping("cancel/{id}")
    @ApiOperation("取消订单")
    public Result<String> cancel(@PathVariable Long id) throws Exception {
        log.info("取消订单：{}",id);
        orderService.cancelById(id);
        return Result.success();
    }

    /**
     * 再来一单
     * @param id
     * @return
     */
    @PostMapping("repetition/{id}")
    @ApiOperation("再来一单")
    public Result<String> repetition(@PathVariable Long id){
        log.info("再来一单：{}",id);
        orderService.repetition(id);
        return Result.success();
    }
}
