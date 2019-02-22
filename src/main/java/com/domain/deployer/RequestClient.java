package com.domain.deployer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.domain.dao.NodeInfoDao;
import com.domain.pojo.NodeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by  Domain
 * on 2019/2/12 17:32;
 */
@Service
public class RequestClient {
    @Autowired
    NodeInfoDao nodeDao;

    public List<NodeInfo> getNodeInfo(){
        List<NodeInfo> nodes = nodeDao.selectObjNoPageList(new NodeInfo(),null);
        //List<NodeInfo> nodes = new LinkedList<>();
        //System.out.println("RequestClient select all NodeInfo: nodes = "+nodes.toString());
        //若节点数过多，需要使用非阻塞式并发RequestTemplate
        //NodeInfo nodeInfo = new NodeInfo();
        //nodeInfo.setNodeName("target_server");
        //nodeInfo.setNodeAddr("10.16.8.91:49527");
        //nodes.add(nodeInfo);
        List<NodeInfo> responseNodes = new LinkedList<>();
        for(NodeInfo temp:nodes){
            String url = "http://"+temp.getNodeAddr()+"/metrics";
            String responseStr = sendRequest(url);
            NodeInfo node = Str2NodeInfo(temp,responseStr);
            responseNodes.add(node);
        }
        return responseNodes;

    }

    private String sendRequest(String url){
        RestTemplate restTemplate = new RestTemplate();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("connecting to "+url+" at  "+df.format(System.currentTimeMillis()));
        String response = "";
        try {
            response=restTemplate.getForObject(url,String.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;

    }

    private NodeInfo Str2NodeInfo(NodeInfo node,String responseStr){

        JSONObject json = JSON.parseObject(responseStr);
        System.out.println("RequestClient get one Node json:josn="+json);
        String mem=json.getString("system_memory_used");
        String memAll = json.getString("memory_all");
        int mem_left=Integer.parseInt(memAll)-Integer.parseInt(mem);
        node.setNodeMem(Integer.toString(mem_left));
        String disk_used=json.getString("disk_used");
        String disk_all = json.getString("disk_all");
        int disk_left = Integer.parseInt(disk_all)- Integer.parseInt(disk_used);
        node.setNodeDisk(Integer.toString(disk_left));
        node.setNodeCpu("cpu");
        node.setNodeIo("io");
        return node;
    }

    public static void main(String[] args){
        RequestClient client = new RequestClient();
        System.out.println(client.getNodeInfo().toString());
    }
}
