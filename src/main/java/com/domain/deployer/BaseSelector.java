package com.domain.deployer;

import com.domain.pojo.JobInfo;
import com.domain.pojo.NodeInfo;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by  Domain
 * on 2019/2/12 19:49;
 */
@Service
public class BaseSelector implements NodeSelector {
    /**
     * 简单的选择方法，遍历所有节点，若isSuitable,则选中
     * @param nodes
     * @param job
     * @param num
     * @return
     */
    @Override
    public LinkedList<NodeInfo> select(List<NodeInfo> nodes, JobInfo job, int num) {
        LinkedList<NodeInfo>    selectedNodes = new LinkedList<>();
        int count = 0;
        for(NodeInfo node:nodes){
            if(isSuitable(job,node)){
                selectedNodes.add(node);
                count++;
            }
            if(count==num)
                break;
        }
        System.out.println("Selector select num of nodes:nodes = "+selectedNodes.toString());
        return selectedNodes;
    }
    protected boolean isSuitable(JobInfo requestNode,NodeInfo node){
        boolean flag = true;
        if(Integer.parseInt(node.getNodeDisk())< Integer.parseInt(requestNode.getRequestDisk()))
            flag = false;
        else if(Integer.parseInt(node.getNodeMem())<Integer.parseInt(requestNode.getRequestMem()))
            flag = false;
        return flag;
    }



}
