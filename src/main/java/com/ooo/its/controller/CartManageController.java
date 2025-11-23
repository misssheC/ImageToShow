package com.ooo.its.controller;

import com.ooo.its.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartManageController {
    @Autowired
    private CartService cartService;
    @Autowired
    private LogService logService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @GetMapping("/user/add")
    public ResponseEntity<?> AddGoods(@RequestParam("goodsID")Long goods,HttpSession session){
        String qqNumber = (String) session.getAttribute("qqNumber");
        logService.SaveLog(qqNumber,"用户加入 <span style='color:red'>"+goodsService.FindGoodsName(goods)+"</span> 到购物车",4);
        if(qqNumber != null) {
            cartService.AddToCart(qqNumber, goods);
            return ResponseEntity.ok("ok");
        }
        else
            return ResponseEntity.status(401).body("用户未登录");
    }

    @GetMapping("/user/remove")
    public ResponseEntity<?> RemoveGoods(@RequestParam("goodsID")Long goods,HttpSession session){
        String qqNumber = (String) session.getAttribute("qqNumber");
        logService.SaveLog(qqNumber,"用户移除 <span style='color:red'>"+goodsService.FindGoodsName(goods)+"</span> 从购物车",5);
        if(qqNumber != null) {
            boolean d = orderService.DeleteOrder(qqNumber,goods);
            if(d) {
                cartService.RemoveFromCart(qqNumber, goods);
                return ResponseEntity.ok("ok0");
            }
            else
                return ResponseEntity.status(401).body("删除失败");
        }
        else
            return ResponseEntity.status(401).body("用户未登录");
    }

}
