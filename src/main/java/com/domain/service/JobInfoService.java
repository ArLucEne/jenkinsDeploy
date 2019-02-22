package com.domain.service;
    /*
    * hxh
    * today
    */
import com.hisense.orm.service.BaseService;
import com.domain.pojo.JobInfo;
import org.springframework.stereotype.Service;

@Service
public class JobInfoService extends BaseService<JobInfo>{

    @Override
    protected void beforeAdd(JobInfo clz) throws RuntimeException {
    }

}
