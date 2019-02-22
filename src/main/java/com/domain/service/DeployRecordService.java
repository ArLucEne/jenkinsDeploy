package com.domain.service;
    /*
    * hxh
    * today
    */
import com.hisense.orm.service.BaseService;
import com.domain.pojo.DeployRecord;
import org.springframework.stereotype.Service;

@Service
public class DeployRecordService extends BaseService<DeployRecord>{

    @Override
    protected void beforeAdd(DeployRecord clz) throws RuntimeException {
    }

}
