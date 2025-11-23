package com.ooo.its.controller;
import com.ooo.its.entity.Cart;
import com.ooo.its.repository.RecordRep;
import com.ooo.its.service.CartService;
import com.ooo.its.service.GoodsService;
import com.ooo.its.service.LogService;
import com.ooo.its.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserCartController {
    @Autowired
    CartService cartService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    RecordRep recordRep;
    @Autowired
    LogService logService;


    @GetMapping("/user/showme")
    @ResponseBody
    public Map<String, Object> LookCart(HttpSession session){
        String qq = (String) session.getAttribute("qqNumber");
        logService.SaveLog(qq,"用户进入查看购物车",6);
        List<Cart> cartList = cartService.ShowMyCart(qq);

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> cartData = new ArrayList<>();

        for (Cart cart : cartList) {
            Map<String, Object> cartItem = new HashMap<>();
            cartItem.put("cart", cart);
            cartItem.put("goodsImageUrl", goodsService.FindImageUrl(cart.getGoodsId()));
            cartItem.put("goodsName" , goodsService.FindGoodsName(cart.getGoodsId()));
            cartData.add(cartItem);
        }

        result.put("cartItems", cartData);
        result.put("totalCount", cartData.size());

        return result;
    }

    @GetMapping("/user/mycart")
    public String ShowMyCart(){
        return "mycart.html";
    }

    @GetMapping("/user/Settlement")
    public ResponseEntity<?>Settlement(HttpSession session){

        String qq = (String) session.getAttribute("qqNumber");
        logService.SaveLog(qq,"用户提交了订单",7);
        int batch = recordRep.findMaxBatchByQq(qq) + 1;
        Long gid;
        List<Cart> cartList = cartService.ShowMyCart(qq);
        //System.out.println(qq);
        for(Cart cart : cartList) {
            if (cart.getState() == 0) {
                gid = cart.getGoodsId();
                orderService.CreateOrder(qq, cart, batch);
                cartService.ChangeState(qq, gid, 1);
            }
            else
                continue;
        }
        return ResponseEntity.ok("ok");

    }
}
