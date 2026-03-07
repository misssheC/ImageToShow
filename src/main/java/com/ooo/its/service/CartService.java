package com.ooo.its.service;

import com.ooo.its.entity.Cart;
import com.ooo.its.repository.CartRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

        return cartRep.findByQqNumberOrderByIdDesc(QQ);
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

    public Page<Cart> showAllPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return cartRep.findAll(pageable);
    }

    public boolean ClearCart(String qq){
        cartRep.deleteByQqNumberAndState(qq,2);
        return true;
    }

    public Cart GetCartId(String qq,Long goodId){
        return cartRep.findByQqNumberAndGoodsId(qq,goodId);
    }
    public boolean IsClinch(String qq , Long goods){
        return cartRep.findByQqNumberAndGoodsId(qq, goods).getState() != 2;
    }
}
