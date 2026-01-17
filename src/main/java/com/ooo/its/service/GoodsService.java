package com.ooo.its.service;

import com.ooo.its.entity.Goods;
import com.ooo.its.repository.GoodsRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private GoodsRep goodsRep;
    public List<String> getGoodsUrlByFolder(String parentFolder) {
        List<Goods> goodsList = goodsRep.findByParentFolderAndDeleteOrderByUpdateDesc(parentFolder,0);
        List<String> goodsUrl = new ArrayList<>();

        for (Goods goods : goodsList) {
            goodsUrl.add(goods.getUrl());
        }

        return goodsUrl;
    }
    public  List<Goods> getGoodsByFolder(String p){
        return goodsRep.findByParentFolderAndDeleteOrderByUpdateDesc(p,0);
    }

    public boolean IncreaseClick(Long goodid){
        Goods goods = goodsRep.findById(goodid).orElse(null);
        assert goods != null;
        goods.setClick(goods.getClick() + 1);
        goodsRep.save(goods);
        return true;
    }

    public String FindImageUrl(Long id){
        return goodsRep.findById(id).orElse(null).getUrl();
    }

    public String FindGoodsName(Long id){
        return goodsRep.findById(id).orElse(null).getName();
    }

    public boolean IncreaseBuy(Long goodid){
        Goods goods = goodsRep.findById(goodid).orElse(null);
        assert goods != null;
        goods.setBuy(goods.getBuy() + 1);
        goodsRep.save(goods);
        return true;
    }

    public List<Goods> Recommend(){
        return goodsRep.findAllOrderByBuyDesc();
    }

    public List<Goods> Update(Date time){
        return goodsRep.findAllByUpdateGreaterThanAndDeleteEquals(time,0);
    }


    public String ViewPicture(Long id){
        return goodsRep.findById(id).orElse(null).getUrl();
    }
}
