package com.ooo.its.controller;

import com.ooo.its.entity.Cart;
import com.ooo.its.repository.RecordRep;
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
    @Autowired
    private RecordRep recordRep;
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
                return ResponseEntity.ok("ok");
            }
            else
                return ResponseEntity.status(401).body("删除失败");
        }
        else
            return ResponseEntity.status(401).body("用户未登录");
    }

    @GetMapping("/user/cancel")
    public ResponseEntity<?> Cancellation(@RequestParam("goodsId")Long goods,HttpSession session){
        String qqNumber = (String) session.getAttribute("qqNumber");
        logService.SaveLog(qqNumber,"用户撤销了 <span style='color:red'>"+goodsService.FindGoodsName(goods)+"</span> 从购物车",5);
        if(qqNumber != null){
            boolean c = orderService.DeleteOrder(qqNumber,goods);
            if(c){
                cartService.ChangeState(qqNumber,goods,0);
                return ResponseEntity.ok("ok");
            }
            else
                return ResponseEntity.status(401).body("撤回失败");
        }
        else
            return ResponseEntity.status(401).body("用户未登录");
    }

    @GetMapping("/user/addone")
    public ResponseEntity<?> AddOne(@RequestParam("goodsId")Long goods,
                                    HttpSession session){
        String qqNumber = (String) session.getAttribute("qqNumber");
        logService.SaveLog(qqNumber,"用户重新加入了 <span style='color:red'>"+goodsService.FindGoodsName(goods)+"</span> 从购物车",4);
        int batch = recordRep.findMaxBatchByQq(qqNumber) + 1;
        Cart cart = cartService.GetCartId(qqNumber,goods);
        if(qqNumber != null){
           boolean d = orderService.CreateOrder(qqNumber,cart,batch);
           if(d){
               cartService.ChangeState(qqNumber,goods,1);
               return ResponseEntity.ok("ok");
           }
           else
               return ResponseEntity.status(401).body("撤回失败");
        }
        else
            return ResponseEntity.status(401).body("用户未登录");
        }

}
