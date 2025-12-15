package com.ooo.its.service;

import com.ooo.its.entity.UserInfo;
import com.ooo.its.entity.User;
import com.ooo.its.repository.UserInfoRep;
import com.ooo.its.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserInfoService {
    @Autowired
    private UserInfoRep userInfoRep;
    @Autowired
    private UserRepository userRepository;
    private Optional<UserInfo> user;

    public boolean SaveLoginInfo(String qqNumber, String model, String IPAddress, String device, String region) {
        Optional<UserInfo> userOptional = userInfoRep.findByQqNumber(qqNumber);
        UserInfo user = userOptional.orElse(new UserInfo());
        Date update = new Date();
        if (user.getLastTime() != null)
            update = user.getLastTime();
        user.setModel(model);
        user.setIpAddress(IPAddress);
        if (user.getLastTime() == null || shouldUpdateUpdateField(user.getLastTime()))
            user.setUpdate(update);
        user.setLastTime(new Date());
        user.setNumberOfLogins(user.getNumberOfLogins() + 1);
        user.setDevice(device);
        user.setRegion(region);

        userInfoRep.save(user);
        return true;
    }

    private boolean shouldUpdateUpdateField(Date lastLoginTime) {
        Date currentTime = new Date();
        long timeDifference = currentTime.getTime() - lastLoginTime.getTime();
        long twoDaysInMillis = 24 * 60 * 60 * 1000L;

        return timeDifference > twoDaysInMillis;
    }

    public List<UserInfo> ShowAllUserInfo() {
        return userInfoRep.findAllByOrderByLastTimeDesc();
    }

    public boolean RecordAboutUser(String qq, int amount, int quantity) {
        Optional<UserInfo> userOptional = userInfoRep.findByQqNumber(qq);
        UserInfo user = userOptional.orElse(new UserInfo());
        user.setLumpSum(user.getLumpSum() + amount);
        user.setPurchaseQuantity(user.getPurchaseQuantity() + quantity);
        return true;
    }

    public List<UserInfo> Ranking() {
        return userInfoRep.findAllByOrderByLumpSumDesc();
    }

    public boolean RegisterUserInfo(String qq) {
        UserInfo verification = userInfoRep.findByQqNumber(qq).orElse(null);
        if (verification == null) {
            UserInfo userInfo = new UserInfo();
            userInfo.setqqNumber(qq);
            userInfo.setRegTime(new Date());
            userInfo.setModel("no");
            userInfo.setIpAddress("0.0.0.0");
            userInfo.setDevice("unknown");
            userInfo.setRegion("unknown");
            userInfoRep.save(userInfo);
            return true;
        }
        return false;
    }

    public List<Map<String, String>> AllandAll() {
        Map<String, String> map = new HashMap<>();
        Integer money = userInfoRep.sumTotalLumpSum();
        Integer saleGoods = userInfoRep.sumTotalPurchaseQuantity();
        map.put("totalLumpSum", String.valueOf(money != null ? money : 0));
        map.put("totalPurchaseQuantity", String.valueOf(saleGoods != null ? saleGoods : 0));
        return Arrays.asList(map);
    }

    public Date FindLastTime(String qq) {
        return Objects.requireNonNull(userInfoRep.findByQqNumber(qq).orElse(null)).getUpdate();
    }

    public boolean DeleteUserInfo() {
        List<UserInfo> userInfos = userInfoRep.findAllByNumberOfLoginsAndPurchaseQuantity(0, 0);
        try {
            for (UserInfo u : userInfos) {
                userInfoRep.deleteByQqNumber(u.getQqNumber());
                userRepository.deleteByQqNumber(u.getQqNumber());
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int getVipStatus(String qq){
        Optional<User> user = userRepository.findByQqNumber(qq);
        return user.map(User::getVip).orElse(0);
    }

    public int getBlackStatus(String qq){
        Optional<User> user = userRepository.findByQqNumber(qq);
        return user.map(User::getBlack).orElse(0);
    }
}
