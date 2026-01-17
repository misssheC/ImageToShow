package com.ooo.its.service;

import com.ooo.its.entity.Admin;
import com.ooo.its.repository.AdminRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class AdminAuthService {
    @Autowired
    private AdminRep adminRep;

    public boolean Authenticate(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return false;
        }
        Optional<Admin> adminOpt = adminRep.findByName(username);
        return adminOpt.isPresent() &&
                checkPassword(password, adminOpt.get().getPasskey());
    }
    private boolean checkPassword(String inputPassword, String storedPassword) {
        return inputPassword.equals(storedPassword);
    }
}
