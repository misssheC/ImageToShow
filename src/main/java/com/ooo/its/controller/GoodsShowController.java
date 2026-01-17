package com.ooo.its.controller;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.ooo.its.entity.*;
import com.ooo.its.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.event.ListDataEvent;
import java.util.*;

@Controller
public class GoodsShowController {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private FolderService folderService;
    @Autowired
    private LogService logService;
    @Autowired
    private UserInfoService userInfoService;
    @GetMapping("/user/showfolders")
    @ResponseBody
    public Map<String, Object> getFolders(HttpSession session) {
        String qqNumber = (String) session.getAttribute("qqNumber");
        int loginNumber = userInfoService.getLoginNumber(qqNumber);
        List<Folder> folderList = folderService.getFolderList();

        Map<String, Object> result = new HashMap<>();
        result.put("loginNumber", loginNumber);
        result.put("folderList", folderList);

        return result;
    }

    @GetMapping("/user/showimages")
    public ResponseEntity<List<Map<String, String>>> getImagesByType(@RequestParam("type") String folderType, HttpSession session) {
        String qq = (String) session.getAttribute("qqNumber");
        logService.SaveLog(qq,"查看了文件夹 <span style='color:blue'>"+folderType+"</span>",2);
        List<Goods> images = goodsService.getGoodsByFolder(folderType);
        List<Map<String, String>> result = new ArrayList<>();
        for (Goods goods : images) {
            Map<String, String> map = new HashMap<>();
            map.put("id", String.valueOf(goods.getId()));
            map.put("name", goods.getName());
            map.put("url", goods.getUrl());
            result.add(map);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/goodsclick")
    @ResponseBody
    public void GoodsClick(@RequestParam Long goodsId,HttpSession session){
        goodsService.IncreaseClick(goodsId);
        String qq = (String) session.getAttribute("qqNumber");
        logService.SaveLog(qq,"查看了预览 <span style='color:red'>"+goodsService.FindGoodsName(goodsId)+"</span>",3);
    }

    @GetMapping("/user/images")
    public String ShowImages(){
        return "folderImages";
    }

    @GetMapping("/user/recommendgoods")
    @ResponseBody
    public List<Map<String,String>> Recommend(HttpSession session) {
        List<Goods> goodsList = goodsService.Recommend();
        List<Map<String, String>> result = new ArrayList<>();
        String qq = (String) session.getAttribute("qqNumber");
        logService.SaveLog(qq,"查看了 <span style='color:blue'>推荐</span>",2);
        for (Goods goods : goodsList) {
            Map<String, String> map = new HashMap<>();
            map.put("id", String.valueOf(goods.getId()));
            map.put("name", goods.getName());
            map.put("url", goods.getUrl());
            result.add(map);
        }

        return result;
    }

    @GetMapping("/user/recommend")
    public String RecommendPage(){
        return "recommend.html";
    }

    @GetMapping("/user/updategoods")
    @ResponseBody
    public List<Map<String,String>> ShowUpdate(HttpSession session) {
        List<Map<String, String>> result = new ArrayList<>();
        String qq = (String) session.getAttribute("qqNumber");
        logService.SaveLog(qq,"查看了 <span style='color:blue'>最近更新</span>",2);
        Date Time = userInfoService.FindLastTime(qq);
        List<Goods> goodsList = goodsService.Update(Time);
        for (Goods goods : goodsList) {
            Map<String, String> map = new HashMap<>();
            map.put("id", String.valueOf(goods.getId()));
            map.put("name", goods.getName());
            map.put("url", goods.getUrl());
            result.add(map);
        }
        return result;
    }

    @GetMapping("/user/instructions")
    public String Instructions(){

        return "instructions.html";
    }

    @GetMapping("/user/read")
    public ResponseEntity<?> Read(HttpSession session){
        String qq = (String) session.getAttribute("qqNumber");
        logService.SaveLog(qq,"阅读了 <span style='color:blue'>用户须知</span>",9);
        return ResponseEntity.ok().body("ok");
    }

    @GetMapping("/user/update")
    public String UpdatePage(){
        return "update.html";
    }
}

