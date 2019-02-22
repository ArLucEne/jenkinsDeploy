package com.domain.controller;

import com.domain.dao.NodeInfoDao;
import com.domain.deployer.Deployer;
import com.domain.deployer.RequestClient;
import com.domain.pojo.JenkinsJob;
import com.domain.pojo.NodeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by  Domain
 * on 2019/2/12 13:12;
 */
@RestController
public class TestController {
    @Autowired
    Deployer deployer;

    /**
     * 部署Id为jobId的job到num个节点中，若已部署了m个节点，重新选择num-m个节点
     * @param jobId
     * @param num
     * @return
     */
    @RequestMapping(value = {"/deploy"},method = {RequestMethod.GET})
    public Object test(@RequestParam String jobId,@RequestParam int num){
        System.out.println("Controller get Params:jobId="+jobId+";num="+num);
        return deployer.deploy(jobId,num);
    }

    /**
     * 创建新job，部署到job.getNodeNum个节点中
     * @param job
     * @return
     */
    @RequestMapping(value = {"/create"},method = {RequestMethod.POST})
    public Object create( @RequestBody JenkinsJob job){
        return deployer.createJob(job,job.getNodeNum());
    }
}
