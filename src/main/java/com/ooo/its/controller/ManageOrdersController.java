package com.ooo.its.controller;

import com.ooo.its.entity.*;
import com.ooo.its.entity.Record;
import com.ooo.its.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ManageOrdersController {
    @Autowired
    OrderService orderService;
    @Autowired
    CartService cartService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    ClinchService clinchService;
    @Autowired
    RecordService recordService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    UserService userService;
    @Autowired
    LogService logService;

    @GetMapping("/admin/allOrders")
    @ResponseBody
    public List<Map<String, Object>> ShowAllOrders() {
        List<Order> orders = orderService.AllOrders();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Order o : orders) {
            Map<String, Object> orderMap = new HashMap<>();

            // 将订单的基本信息添加到map中
            orderMap.put("id", o.getId());
            orderMap.put("goodsId", o.getGoodsId());
            orderMap.put("qqNumber", o.getQqNumber());
            orderMap.put("status", String.valueOf(o.getState()));
            orderMap.put("date", o.getDate());
            orderMap.put("batch",String.valueOf(o.getBatch()));
            String goodsUrl = goodsService.FindImageUrl(o.getGoodsId());
            orderMap.put("imageUrl", goodsUrl);

            result.add(orderMap);
        }

        return result;
    }


    @GetMapping("/admin/allcart")
    @ResponseBody
    public List<Cart> ShowAllCarts(){
        return cartService.ShowAll();
    }

    @GetMapping("/admin/allinfo")
    @ResponseBody
    public List<UserInfo> ShowAllInfo(){
        return userInfoService.ShowAllUserInfo();
    }

    @GetMapping("/admin/ranking")
    @ResponseBody
    public List<UserInfo> ShowRanking(){
        return userInfoService.Ranking();
    }


    @GetMapping("/admin/clinch")
    public ResponseEntity<?> Clinch(@RequestParam String qqNumber,@RequestParam int amount){
        List<Order> orders = orderService.FindHandleData(qqNumber,0);
        int sum = 0;
        int batch = recordService.FindBatch(qqNumber) + 1;
        for(Order order : orders) {
            order.setState(1);
            orderService.SaveOrder(order);
            sum++;
        }
        boolean result = recordService.RecordTransaction(qqNumber,amount,sum,batch);
        if (result)
            return ResponseEntity.ok("ok");
        else
            return ResponseEntity.status(401).body("error");
    }

    @GetMapping("/admin/complete")
    public ResponseEntity<?> CompleteOrder(@RequestParam String qqNumber,@RequestParam int batch){
        List<Order> orders = orderService.FindHandleData(qqNumber,1);
        for (Order o : orders){
            o.setState(2);
            orderService.SaveOrder(o);
            goodsService.IncreaseBuy(o.getGoodsId());
        }
        Record record = recordService.FindUnPayOrder(qqNumber,batch,0);
        int amount = record.getAmount();
        int quantity = record.getGoodsQuantity();
        record.setIsPay(1);
        userInfoService.RecordAboutUser(qqNumber,amount,quantity);
        cartService.ClearCart(qqNumber);
        if(recordService.SaveRecord(record))
            return ResponseEntity.ok("ok");
        else
            return ResponseEntity.status(401).body("no");
    }

    @GetMapping("/admin/print")
    @ResponseBody
    public List<String> Print(@RequestParam String qqNumber,@RequestParam int batch){
        List<Order> orders = orderService.FindHandleData(qqNumber,0);
        List<String> result = new ArrayList<>();
        for (Order o :orders)
            result.add(goodsService.FindGoodsName(o.getGoodsId()));
        return result;
    }

    @GetMapping("/admin/reg")
    public ResponseEntity<?> Register(@RequestParam String qqNumber){
        boolean a = userInfoService.RegisterUserInfo(qqNumber);
        boolean b = userService.RegisterUser(qqNumber);
        if (a && b)
            return ResponseEntity.ok("ok");
        else
            return ResponseEntity.status(401).body("no");
    }

    @GetMapping("/admin/log")
    @ResponseBody
    public List<Log> ShowLogs(){
        return logService.ShowLog();
    }

    @GetMapping("/admin/history")
    @ResponseBody
    public List<Record> ShowHistoryOrders(){
        return recordService.FindAllRecords();
    }

    @GetMapping("/admin/allandall")
    @ResponseBody
    public List<Map<String,String>> AllandAll(){
        return userInfoService.AllandAll();
    }
}
