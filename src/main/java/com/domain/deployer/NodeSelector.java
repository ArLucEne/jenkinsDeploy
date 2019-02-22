package com.domain.deployer;

import com.domain.pojo.JobInfo;
import com.domain.pojo.NodeInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by  Domain
 * on 2019/2/12 19:13;
 */
public interface  NodeSelector {
    public  LinkedList<NodeInfo> select(List<NodeInfo> nodes, JobInfo job, int num);
}
