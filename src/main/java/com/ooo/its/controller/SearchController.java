package com.ooo.its.controller;

import com.ooo.its.entity.*;
import com.ooo.its.entity.Record;
import com.ooo.its.service.GoodsService;
import com.ooo.its.service.SearchService;
import com.ooo.its.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class SearchController {
    @Autowired
    private SearchService searchService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private GoodsService goodsService;
    @GetMapping("/admin/searchOrder")
    @ResponseBody
    public List<Map<String, Object>> SearchOrders(@RequestParam String qq,
                                                  @RequestParam int state) {
        List<Order> orders = searchService.SearchOrderByQQ(qq,state);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Order o : orders) {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", o.getId());
            orderMap.put("goodsId", o.getGoodsId());
            orderMap.put("goodsName",goodsService.FindGoodsName(o.getGoodsId()));
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

    @GetMapping("/admin/searchPerson")
    @ResponseBody
    public Map<String,Object> SearchPerson(@RequestParam String qq){
        UserInfo userInfo = searchService.SearchPerson(qq);
        Map<String, Object> personMap = new LinkedHashMap<>();
        if (userInfo == null) {
            return personMap;
        }
        personMap.put("qqNumber", userInfo.getQqNumber());
        personMap.put("lastTime", userInfo.getLastTime());
        personMap.put("regTime", userInfo.getRegTime());
        personMap.put("purchaseQuantity", userInfo.getPurchaseQuantity());
        personMap.put("numberOfLogins", userInfo.getNumberOfLogins());
        personMap.put("lumpSum", userInfo.getLumpSum());
        personMap.put("ipAddress", userInfo.getIpAddress());
        personMap.put("device", userInfo.getDevice());
        personMap.put("region", userInfo.getRegion());
        personMap.put("vip", userInfoService.getVipStatus(userInfo.getQqNumber()));
        personMap.put("black", userInfoService.getBlackStatus(userInfo.getQqNumber()));
        return personMap;
    }


    @GetMapping("/admin/searchHistory")
    @ResponseBody
    public List<Record> SearchHistory(@RequestParam String qq){
        return searchService.SearchRecordByQQ(qq);
    }


    @GetMapping("/admin/searchLog")
    @ResponseBody
    public List<Log> SearchLog(@RequestParam String qq,
                               @RequestParam int type){
        return searchService.SearchLogByUser(qq,type);
    }


    @GetMapping("/admin/searchCart")
    @ResponseBody
    public List<Cart> SearchCart(@RequestParam String qq,
                                 @RequestParam int state){
        return searchService.SearchCartByQQ(qq,state);
    }

}
