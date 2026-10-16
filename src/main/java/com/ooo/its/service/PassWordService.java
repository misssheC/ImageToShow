package com.ooo.its.service;

import com.ooo.its.entity.Passwd;
import com.ooo.its.repository.PassWordRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PassWordService {
    @Autowired
    private PassWordRep passWordRep;
    public String GetLastKey(String qq) {
        Optional<Passwd> passwd = passWordRep.findTopByQqOrderByBatchDesc(qq);
        if (passwd.isPresent() && passwd.get().getOk() == 1) {
            return passwd.get().getPassword();
        }
        return null;
    }

    public boolean Save(Passwd passwd) {
        if (passwd == null) {
            return false;
        }

        try {
            Optional<Passwd> existing = passWordRep.findByQqAndBatch(
                    passwd.getQq(), passwd.getBatch());
            if (existing.isPresent()) {
                Passwd old = existing.get();
                old.setPassword(passwd.getPassword());
                old.setOk(passwd.getOk());
                passWordRep.save(old);
            } else {
                passWordRep.save(passwd);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean SetOk(String qq, int batch) {
        Optional<Passwd> opt = passWordRep.findByQqAndBatch(qq, batch);
        if (opt.isEmpty()) {
            return false;
        }
        Passwd passwd = opt.get();
        passwd.setOk(1);
        passWordRep.save(passwd);
        return true;
    }

}
