package com.ooo.its.service;

import com.ooo.its.entity.Log;
import com.ooo.its.repository.CartRep;
import com.ooo.its.repository.OrderRep;
import com.ooo.its.repository.RecordRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteService {
    @Autowired
    private CartRep cartRep;
    @Autowired
    private OrderRep orderRep;
    @Autowired
    private RecordRep recordRep;

    public boolean DeleteCartByAdmin(Long cartId){
        try{
            cartRep.deleteById(cartId);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public boolean DeleteOrderByAdmin(Long orderId , String qqNumber , Long goodsId,int batch){
        try {
            orderRep.deleteById(orderId);
            cartRep.deleteByQqNumberAndGoodsId(qqNumber,goodsId);
            if(orderRep.countByQqAndBatch(qqNumber,batch) == 0)
                recordRep.deleteByQqNumberAndBatch(qqNumber,batch);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }


}
