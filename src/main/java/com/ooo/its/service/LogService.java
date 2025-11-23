package com.ooo.its.service;

import com.ooo.its.entity.Log;
import com.ooo.its.repository.LogRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class LogService {

    @Autowired
    LogRep logRep;


    public boolean SaveLog(String qq,String event,int type){
        Log log = new Log();
        log.setUser(qq);
        log.setEvent(event);
        log.setTime(new Date());
        log.setType(type);
        logRep.save(log);
        return true;
    }

    public List<Log> ShowLog(){
        Date twentyFourHoursAgo = new Date(System.currentTimeMillis() - 72 * 60 * 60 * 1000);
        return logRep.findLast24HoursLogs(twentyFourHoursAgo);
    }

    public List<Log> FindByUser(String qq){
        return logRep.findByUser(qq);
    }

    public List<Log> FindByType(int type){
        return logRep.findByType(type);
    }
}
