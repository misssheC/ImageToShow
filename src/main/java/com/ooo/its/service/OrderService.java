package com.ooo.its.service;

import com.ooo.its.entity.Cart;
import com.ooo.its.entity.Goods;
import com.ooo.its.entity.Order;
import com.ooo.its.repository.OrderRep;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    OrderRep orderRep;

    public boolean CreateOrder(String qq , Cart cart , int batch){
        Order order = new Order();
        order.setQqNumber(qq);
        order.setDate(new Date());
        order.setGoodsId(cart.getGoodsId());
        order.setBatch(batch);
        try {
            orderRep.save(order);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean DeleteOrder(String qq , Long goodsID){
        try {
            orderRep.deleteByQqNumberAndGoodsIdAndState(qq, goodsID, 0);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
    public List<Order> AllOrders(int n){
        Pageable pageable = PageRequest.of(0, n, Sort.by("id").descending());
        return orderRep.findAllByOrderByIdDesc(pageable);
    }


    public List<Order> FindHandleData(String qq,int state){
        return orderRep.findByQqNumberAndState(qq,state);
    }

    public List<Order> PrintOrders(String qq , int batch){
        return orderRep.findByQqNumberAndBatch(qq,batch);
    }
    public boolean SaveOrder(Order order){
        try {
            orderRep.save(order);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
