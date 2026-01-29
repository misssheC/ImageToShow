package com.ooo.its.service;

import com.ooo.its.entity.User;
import com.ooo.its.entity.UserInfo;
import com.ooo.its.repository.UserInfoRep;
import com.ooo.its.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserInfoRep userInfoRep;

    public Optional<User> findBy(String qq_number) {
        return userRepository.findByQqNumber(qq_number);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public boolean validateUser(String qq_number, String password, String device) {
        Optional<User> userOpt = userRepository.findByQqNumber(qq_number);
        Optional<UserInfo> userInfoOpt = userInfoRep.findByQqNumber(qq_number);
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password)) {
            return false;
        }
        if (userInfoOpt.isEmpty()) {
            return false;
        }
        UserInfo userInfo = userInfoOpt.get();
        Date currentTime = new Date();

        boolean deviceMatch = compareDevice(device, userInfo.getDevice());
        if (userInfo.getLastTime() == null) {
            if ("unknown".equals(userInfo.getRegion())) {
                return true;
            }
            return deviceMatch;
        }
        long timeDifference = currentTime.getTime() - userInfo.getLastTime().getTime();
        long twentyFourHours = 72 * 60 * 60 * 1000;
        if (timeDifference <= twentyFourHours) {
            return deviceMatch;
        } else {
            return true;
        }
    }
    public boolean compareDevice(String device1, String device2) {
        if (device1 == null || device2 == null) {
            return Objects.equals(device1, device2);
        }
        device1 = device1.trim().toLowerCase();
        device2 = device2.trim().toLowerCase();
        if ("unknown".equals(device1) && "unknown".equals(device2)) {
            return true;
        }
        if ("unknown".equals(device1) || "unknown".equals(device2)) {
            return false;
        }
        return device1.equals(device2);
    }
    public boolean RegisterUser(String qq) {
        User verification = userRepository.findByQqNumber(qq).orElse(null);
        if (verification == null) {
            User user = new User();
            user.setQqnumber(qq);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public int Identity(String qq) {
        Optional<User> u = userRepository.findByQqNumber(qq);
        int result = 0;
        if (u.isPresent()) {
            if (u.get().getBlack() == 1) {
                result = 1;
            }
            if (u.get().getVip() == 1) {
                result = 2;
            }
        }
        return result;
    }

    public String GetLastRegion(String qq){
        Optional<UserInfo> u = userInfoRep.findByQqNumber(qq);
        return u.map(UserInfo::getRegion).orElse(null);
    }

    public boolean setIdentity(String qqNumber, int s) {
        Optional<User> u = userRepository.findByQqNumber(qqNumber);
        if (u.isEmpty()) {
            return false;
        }
        User user = u.get();
        switch (s) {
            case 1:
                if (user.getVip() == 1) return false;
                user.setVip(1);
                break;
            case 2:
                if (user.getVip() == 0) return false;
                user.setVip(0);
                break;
            case 3:
                if (user.getBlack() == 1) return false;
                user.setBlack(1);
                break;
            case 4:
                if (user.getBlack() == 0) return false;
                user.setBlack(0);
                break;
            default:
                return false;
        }
        userRepository.save(user);
        return true;
    }
}