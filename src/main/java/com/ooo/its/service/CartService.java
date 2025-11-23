package com.ooo.its.service;

import com.ooo.its.entity.Cart;
import com.ooo.its.repository.CartRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRep cartRep;
    public boolean AddToCart(String qq,Long goodsid){
        Cart newCart = new Cart();
        if (cartRep.findByQqNumberAndGoodsId(qq, goodsid) == null){
            newCart.setQqNumber(qq);
            newCart.setGoodsId(goodsid);
            newCart.setTime(new Date());
            cartRep.save(newCart);
        }
        else
            return false;
        return true;
    }
    public boolean RemoveFromCart(String qq,Long goodsid){
        Cart cart = cartRep.findByQqNumberAndGoodsId(qq,goodsid);
        if(cart == null)
            return false;
        else{
            cartRep.deleteByQqNumberAndGoodsId(qq, goodsid);
            return true;
        }
    }

    public List<Cart> ShowMyCart(String QQ){

        return cartRep.findByQqNumber(QQ);
    }

    public boolean ChangeState(String qq , Long id , int event){
        Cart cart = cartRep.findByQqNumberAndGoodsId(qq,id);
        cart.setState(event);
        try {
            cartRep.save(cart);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public List<Cart> ShowAll(){
        return cartRep.findAll();
    }

    public boolean ClearCart(String qq){
        cartRep.deleteByQqNumber(qq);
        return true;
    }
}
