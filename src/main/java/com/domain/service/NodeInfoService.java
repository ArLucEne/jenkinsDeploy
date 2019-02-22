package com.domain.service;
    /*
    * hxh
    * today
    */
import com.hisense.orm.service.BaseService;
import com.domain.pojo.NodeInfo;
import org.springframework.stereotype.Service;

@Service
public class NodeInfoService extends BaseService<NodeInfo>{

    @Override
    protected void beforeAdd(NodeInfo clz) throws RuntimeException {
    }

}
