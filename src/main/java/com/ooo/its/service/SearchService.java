package com.ooo.its.service;

import com.ooo.its.entity.*;
import com.ooo.its.entity.Record;
import com.ooo.its.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public List<Order> SearchOrderByQQ(String qq, int state , int page) {
        Pageable pageable = PageRequest.of(page - 1, 100, Sort.by(Sort.Direction.DESC, "date"));
        boolean isQqEmpty = (qq == null || qq.trim().isEmpty());

        if (!isQqEmpty && state == 1000)
            return orderRep.findAllByQqNumberOrderByIdDesc(qq,pageable);
        if (isQqEmpty && state != 1000)
            return orderRep.findAllByStateOrderByIdDesc(state,pageable);
        if (!isQqEmpty)
            return orderRep.findByQqNumberAndStateOrderByIdDesc(qq, state,pageable);
        return new ArrayList<>();
    }

    public List<Cart> SearchCartByQQ(String qq, int state , int page) {
        Pageable pageable = PageRequest.of(page - 1, 100, Sort.by(Sort.Direction.DESC, "time"));
        boolean hasQq = qq != null && !qq.trim().isEmpty();
        if (hasQq && state == 1000)
            return cartRep.findAllByQqNumberOrderByIdDesc(qq,pageable);
        if (!hasQq && state != 1000)
            return cartRep.findByStateOrderByIdDesc(state,pageable);
        if (hasQq)
            return cartRep.findByQqNumberAndStateOrderByIdDesc(qq, state,pageable);
        return new ArrayList<>();
    }

    public List<Record> SearchRecordByQQ(String qq , int page){
        Pageable pageable = PageRequest.of(page - 1, 100, Sort.by(Sort.Direction.DESC, "time"));
        return recordRep.findAllByQqNumberOrderByIdDesc(qq,pageable);
    }

    public UserInfo SearchPerson(String qq){
        return userInfoRep.findByQqNumber(qq).orElse(null);
    }

    public List<Log> SearchLogByUser(String qq, int type, int page) {
        Pageable pageable = PageRequest.of(page - 1, 100, Sort.by(Sort.Direction.DESC, "time"));
        boolean hasQq = qq != null && !qq.trim().isEmpty();
        if (hasQq && type == 1000)
            return logRep.findByUserOrderByTimeDesc(qq,pageable);
        if (!hasQq && type != 1000)
            return logRep.findByTypeOrderByTimeDesc(type,pageable);
        if (hasQq)
            return logRep.findByUserAndTypeOrderByTimeDesc(qq, type,pageable);
        return new ArrayList<>();
    }

}
