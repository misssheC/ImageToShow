package com.ooo.its.service;

import com.ooo.its.entity.*;
import com.ooo.its.entity.Record;
import com.ooo.its.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<Order> SearchOrderByQQ(String qq){
        return orderRep.findAllByQqNumberOrderByIdDesc(qq);
    }

    public List<Cart> SearchCartByQQ(String qq){
        return cartRep.findByQqNumberOrderByIdDesc(qq);
    }

    public List<Record> SearchRecordByQQ(String qq){
        return recordRep.findAllByQqNumberOrderByIdDesc(qq);
    }

    public UserInfo SearchPerson(String qq){
        return userInfoRep.findByQqNumber(qq).orElse(null);
    }

    public List<Log> SearchLogByUser(String qq){
        return logRep.findByUserOrderByTimeDesc(qq);
    }

}
