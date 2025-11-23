package com.ooo.its.controller;

import com.ooo.its.entity.Order;
import com.ooo.its.entity.Record;
import com.ooo.its.service.GoodsService;
import com.ooo.its.service.LogService;
import com.ooo.its.service.OrderService;
import com.ooo.its.service.RecordService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PayController {

    @Autowired
    RecordService recordService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;
    @Autowired
    LogService logService;
    @GetMapping("/user/topay")
    @ResponseBody
    public Map<String, Object> UserPay(HttpSession session) {
        String qq = (String) session.getAttribute("qqNumber");
        logService.SaveLog(qq,"用户进入支付页面",8);
        int MaxBatch = recordService.FindBatch(qq);
        int amount = 0;
        Record record = recordService.FindUnPayOrder(qq, MaxBatch, 0);
        List<Order> orders = orderService.FindHandleData(qq, 1);
        int sum = orders.size();

        List<String> goodsNames = new ArrayList<>();
        for (Order order : orders) {
            String goodsName = goodsService.FindGoodsName(order.getGoodsId());
            goodsNames.add(goodsName);
        }

        Map<String, Object> result = new HashMap<>();
        if(record != null)
            amount = record.getAmount();
            result.put("money", amount);
        result.put("sum", sum);
        result.put("goodsNames", goodsNames);
        if(sum != 0) {
            result.put("wxpayQRcode", "/pay/wx.png");
            result.put("alipayQRcode", "/pay/ali.png");
            result.put("Go",1);
        }
        else
            result.put("Go",0);
        return result;
    }

    @GetMapping("/user/pay")
    public String PayPage(){
        return "pay.html";
    }
}
