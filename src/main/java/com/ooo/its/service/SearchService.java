package com.ooo.its.service;

import com.ooo.its.entity.*;
import com.ooo.its.entity.Record;
import com.ooo.its.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {

    @Autowired
    private OrderRep orderRep;
    @Autowired
    private CartRep cartRep;
    @Autowired
    private UserInfoRep userInfoRep;
    @Autowired
    private RecordRep recordRep;
    @Autowired
    private LogRep logRep;
    public List<Order> SearchOrderByQQ(String qq, int state) {
        boolean isQqEmpty = (qq == null || qq.trim().isEmpty());

        if (!isQqEmpty && state == 1000)
            return orderRep.findAllByQqNumberOrderByIdDesc(qq);
        if (isQqEmpty && state != 1000)
            return orderRep.findAllByStateOrderByIdDesc(state);
        if (!isQqEmpty)
            return orderRep.findByQqNumberAndStateOrderByIdDesc(qq, state);
        return new ArrayList<>();
    }

    public List<Cart> SearchCartByQQ(String qq, int state) {
        boolean hasQq = qq != null && !qq.trim().isEmpty();
        if (hasQq && state == 1000)
            return cartRep.findByQqNumberOrderByIdDesc(qq);
        if (!hasQq && state != 1000)
            return cartRep.findByStateOrderByIdDesc(state);
        if (hasQq)
            return cartRep.findByQqNumberAndStateOrderByIdDesc(qq, state);
        return new ArrayList<>();
    }

    public List<Record> SearchRecordByQQ(String qq){
        return recordRep.findAllByQqNumberOrderByIdDesc(qq);
    }

    public UserInfo SearchPerson(String qq){
        return userInfoRep.findByQqNumber(qq).orElse(null);
    }

    public List<Log> SearchLogByUser(String qq, int type) {
        boolean hasQq = qq != null && !qq.trim().isEmpty();
        if (hasQq && type == 1000)
            return logRep.findByUserOrderByTimeDesc(qq);
        if (!hasQq && type != 1000)
            return logRep.findByTypeOrderByTimeDesc(type);
        if (hasQq)
            return logRep.findByUserAndTypeOrderByTimeDesc(qq, type);
        return new ArrayList<>();
    }

}
