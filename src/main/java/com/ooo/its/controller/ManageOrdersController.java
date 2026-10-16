package com.ooo.its.controller;

import com.ooo.its.entity.*;
import com.ooo.its.entity.Record;
import com.ooo.its.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class ManageOrdersController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private RecordService recordService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private UserService userService;
    @Autowired
    private LogService logService;
    @Autowired
    private AnalysisService analysisService;
    @Autowired
    private PassWordService passWordService;
    @Autowired
    private GoodsKeyService goodsKeyService;
    @Autowired
    private LoadService loadService;
    @Autowired
    private QqSendService qqSendService;

    @GetMapping("/admin/allOrders")
    @ResponseBody
    public List<Map<String, Object>> ShowAllOrders(@RequestParam int number) {
        List<Order> orders = orderService.AllOrders(number);
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
            orderMap.put("isLoad",o.getIs_load());
            orderMap.put("isShip",o.getIs_ship());

            result.add(orderMap);
        }

        return result;
    }


    @GetMapping("/admin/allcart")
    @ResponseBody
    public Page<Cart> showAllCartsPage(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "200") int size) {
        return cartService.showAllPage(page, size);
    }
    @GetMapping("/admin/allinfo")
    @ResponseBody
    public Map<String, Object> ShowAllInfo(@RequestParam(defaultValue = "1") int page) {
        Page<UserInfo> userPage = userInfoService.ShowAllUserInfo(page);
        List<Map<String, Object>> result = convertUserInfoToMapList(userPage.getContent());
        Map<String, Object> response = new HashMap<>();
        response.put("users", result);
        response.put("currentPage", page);
        response.put("totalCount", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());
        return response;
    }

    @GetMapping("/admin/ranking")
    @ResponseBody
    public List<UserInfo> ShowRanking(){
        return userInfoService.Ranking();
    }


    @GetMapping("/admin/clinch")
    public ResponseEntity<?> Clinch(@RequestParam String qqNumber,@RequestParam int amount){
        List<Order> orders = orderService.FindHandleData(qqNumber,0);
        int sum = 0;
        int batch = recordService.FindBatch(qqNumber) + 1;
        for(Order order : orders) {
            order.setState(1);
            orderService.SaveOrder(order);
            cartService.ChangeState(qqNumber,order.getGoodsId(),2);
            sum++;
        }
        boolean result = recordService.RecordTransaction(qqNumber,amount,sum,batch);
        if (result)
            return ResponseEntity.ok("ok");
        else
            return ResponseEntity.status(401).body("error");
    }
    @GetMapping("/admin/edit")
    public ResponseEntity<?> Edit(@RequestParam String qqNumber,
                                  @RequestParam int batch,
                                  @RequestParam int amount){
        Record record = recordService.FindUnPayOrder(qqNumber,batch,0);
        record.setAmount(amount);
        if(recordService.SaveRecord(record))
            return ResponseEntity.ok("ok");
        else
            return ResponseEntity.status(401).body("no");
    }

    @GetMapping("/admin/complete")
    public ResponseEntity<?> CompleteOrder(@RequestParam String qqNumber,@RequestParam int batch){
        List<Order> orders = orderService.FindHandleDataByBatch(qqNumber,1,batch);
        for (Order o : orders){
            o.setState(2);
            orderService.SaveOrder(o);
            goodsService.IncreaseBuy(o.getGoodsId());
        }
        passWordService.SetOk(qqNumber,batch);
        Record record = recordService.FindUnPayOrder(qqNumber,batch,0);
        int amount = record.getAmount();
        int quantity = record.getGoodsQuantity();
        record.setIsPay(1);
        userInfoService.RecordAboutUser(qqNumber,amount,quantity);
        cartService.ClearCart(qqNumber);
        if(recordService.SaveRecord(record)) {
            analysisService.IncomeStatistics(amount);
            return ResponseEntity.ok("ok");
        }
        else
            return ResponseEntity.status(401).body("no");
    }

    @GetMapping("/admin/print")
    @ResponseBody
    public List<String> Print(@RequestParam String qqNumber,@RequestParam int batch){
        List<Order> orders = orderService.PrintOrders(qqNumber,batch);
        List<String> result = new ArrayList<>();
        for (Order o :orders)
            result.add(goodsService.FindGoodsName(o.getGoodsId()));
        return result;
    }

    @GetMapping("/admin/reg")
    public ResponseEntity<?> Register(@RequestParam String qqNumber){
        boolean a = userInfoService.RegisterUserInfo(qqNumber);
        boolean b = userService.RegisterUser(qqNumber);
        if (a && b)
            return ResponseEntity.ok("ok");
        else
            return ResponseEntity.status(401).body("no");
    }

    @GetMapping("/admin/log")
    @ResponseBody
    public List<Log> ShowLogs(){
        return logService.ShowLog();
    }

    @GetMapping("/admin/history")
    @ResponseBody
    public List<Record> ShowHistoryOrders(){
        return recordService.FindAllRecords();
    }

    @GetMapping("/admin/allandall")
    @ResponseBody
    public List<Map<String,String>> AllandAll(){
        return userInfoService.AllandAll();
    }

    @GetMapping("/admin/clearuser")
    public ResponseEntity<?>ClearUser(){
        if(userInfoService.DeleteUserInfo()){
            return ResponseEntity.ok().body("ok");
        }
        else {
            return ResponseEntity.status(401).body("no");
        }
    }

    @GetMapping("/admin/setvip")
    public ResponseEntity<?> SetVip(@RequestParam String qqNumber ,@RequestParam int s){
        if(userService.setIdentity(qqNumber,s))
            return ResponseEntity.ok("ok");
        return ResponseEntity.status(401).body("no");
    }

    @GetMapping("/admin/view")
    @ResponseBody
    public Map<String,String> View(@RequestParam String goodsId){

        Long id = Long.parseLong(goodsId);
        Map<String,String> r = new HashMap<>();
        String url = goodsService.ViewPicture(id);
        r.put("Purl" , url);
        return r;
    }


    @GetMapping("/admin/load")
    public ResponseEntity<?> load(@RequestParam String qqNumber,
                                  @RequestParam int batch) {

        // 1. 只做一次轻量的 DB 校验，避免"点了按钮才发现没订单"
        try {
            List<Order> orders = orderService.FindHandleDataByBatch(qqNumber, 1, batch);
            if (orders == null || orders.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "msg", "没有需要装货的订单",
                        "qqNumber", qqNumber,
                        "batch", batch));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("备货任务下发失败: " + e.getMessage());
        }

        try {
            loadService.loadAsync(qqNumber, batch);
        } catch (TaskRejectedException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("备货任务队列已满，请稍后再试");
        }

        return ResponseEntity.ok(Map.of(
                "msg", String.format("QQ:%s 批次:%d 备货任务已下发", qqNumber, batch),
                "qqNumber", qqNumber,
                "batch", batch));
    }

    @GetMapping("/admin/ship")
    public ResponseEntity<?> ship(
            @RequestParam String qqNumber,
            @RequestParam int batch) {

        try {
            List<GoodsKey> keys = goodsKeyService.findByQqNumberAndBatch(qqNumber, batch);
            if (keys == null || keys.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "msg", "该批次没有可发货的文件",
                        "qqNumber", qqNumber,
                        "batch", batch
                ));
            }
            List<Order> orders = orderService.FindHandleDataByBatch(qqNumber, 1, batch);

            int success = 0, failed = 0, shipped = 0;
            List<String> failedIds = new ArrayList<>();

            for (GoodsKey key : keys) {
                String messageId = key.getMessageId();
                String fileName = key.getFilename();

                boolean ok = qqSendService.forwardMessageToUser(qqNumber, messageId, fileName);

                if (ok) {
                    success++;
                    if (orders != null && !orders.isEmpty() && fileName != null) {
                        try {
                            String goodsName = stripZipSuffix(fileName);
                            var goods = goodsService.FindGoodsByName(goodsName);
                            if (goods != null) {
                                Long goodsId = goods.getId();
                                for (Order order : orders) {
                                    if (!Integer.valueOf(1).equals(order.getIs_ship())
                                            && goodsId.equals(order.getGoodsId())) {
                                        order.setIs_ship(1);
                                        orderService.SaveOrder(order);
                                        shipped++;
                                        break;
                                    }
                                }
                            } else {
                                System.err.println("[ship] 未找到 Goods，filename=" + fileName
                                        + "，goodsName=" + goodsName);
                            }
                        } catch (Exception e) {
                            System.err.println("[ship] 更新 Order.is_ship 失败: file="
                                    + fileName + ", msg=" + e.getMessage());
                        }
                    }
                } else {
                    failed++;
                    failedIds.add(messageId == null ? "(空)" : messageId);
                }

                try {
                    Thread.sleep(500L);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("msg", "发货完成");
            result.put("qqNumber", qqNumber);
            result.put("batch", batch);
            result.put("total", keys.size());
            result.put("success", success);
            result.put("failed", failed);
            result.put("ordersShipped", shipped);
            if (!failedIds.isEmpty()) {
                result.put("failedMessageIds", failedIds);
            }
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("发货失败: " + e.getMessage());
        }
    }
    private String stripZipSuffix(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return fileName;
        }

        String[] parts = fileName.split("\\.");
        if (parts.length <= 2) {
            return fileName;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length - 2; i++) {
            if (i > 0) {
                sb.append(".");
            }
            sb.append(parts[i]);
        }
        return sb.toString();
    }

    private List<Map<String, Object>> convertUserInfoToMapList(List<UserInfo> userInfoList) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (UserInfo userInfo : userInfoList) {
            Map<String, Object> userMap = new LinkedHashMap<>();
            userMap.put("qqNumber", userInfo.getQqNumber());
            userMap.put("lastTime", userInfo.getLastTime());
            userMap.put("regTime", userInfo.getRegTime());
            userMap.put("purchaseQuantity", userInfo.getPurchaseQuantity());
            userMap.put("numberOfLogins", userInfo.getNumberOfLogins());
            userMap.put("lumpSum", userInfo.getLumpSum());
            userMap.put("ipAddress", userInfo.getIpAddress());
            userMap.put("device", userInfo.getDevice());
            userMap.put("region", userInfo.getRegion());
            userMap.put("vip", userInfoService.getVipStatus(userInfo.getQqNumber()));
            userMap.put("black", userInfoService.getBlackStatus(userInfo.getQqNumber()));
            userMap.put("overseas", userInfoService.getOverseasStatus(userInfo.getQqNumber()));

            result.add(userMap);
        }

        return result;
    }
}
