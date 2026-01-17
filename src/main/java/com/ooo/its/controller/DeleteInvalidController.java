package com.ooo.its.controller;

import com.ooo.its.service.DeleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DeleteInvalidController {
    @Autowired
    private DeleteService deleteService;


    @GetMapping("/admin/deletecart")
    public ResponseEntity<?> DeleteCart(@RequestParam String cartId){
        Long id = Long.parseLong(cartId);
        boolean r = deleteService.DeleteCartByAdmin(id);
        if (r)
            return ResponseEntity.ok("ok");
        else
            return ResponseEntity.status(401).body("no");
    }

    @GetMapping("/admin/deleteorder")
    public ResponseEntity<?> DeleteOrder(@RequestParam String orderId,
                                         @RequestParam String qq,
                                         @RequestParam String goodsId){
        Long oid = Long.parseLong(orderId);
        Long gid = Long.parseLong(goodsId);

        boolean r = deleteService.DeleteOrderByAdmin(oid,qq,gid);
        if (r)
            return ResponseEntity.ok("ok");
        else
            return ResponseEntity.status(401).body("no");
    }

}
