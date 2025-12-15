package com.ooo.its.service;

import com.ooo.its.entity.User;
import com.ooo.its.entity.UserInfo;
import com.ooo.its.repository.UserInfoRep;
import com.ooo.its.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    public boolean validateUser(String qq_number, String password, String region) {
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
        boolean regionMatch = region.equals(userInfo.getRegion());
        if (userInfo.getLastTime() == null) {
            if ("unknown".equals(userInfo.getRegion())) {
                return true;
            }
            return regionMatch;
        }
        long timeDifference = currentTime.getTime() - userInfo.getLastTime().getTime();
        long twentyFourHours = 72 * 60 * 60 * 1000;
        if (timeDifference <= twentyFourHours) {
            return regionMatch;
        } else {
            return true;
        }
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