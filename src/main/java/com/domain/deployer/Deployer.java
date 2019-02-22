package com.domain.deployer;

import com.domain.dao.DeployRecordDao;
import com.domain.dao.JobInfoDao;
import com.domain.dao.NodeInfoDao;
import com.domain.pojo.DeployRecord;
import com.domain.pojo.JenkinsJob;
import com.domain.pojo.JobInfo;
import com.domain.pojo.NodeInfo;
import com.domain.util.JenkinsHelper;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by  Domain
 * on 2019/2/13 8:46;
 */
@Service
public class Deployer {
    @Autowired
    JobInfoDao jobDao;
    @Autowired
    DeployRecordDao deployDao;
    @Autowired
    NodeInfoDao nodeDao;
    @Autowired
    RequestClient requestClient;
    @Autowired
    BaseSelector selector;

    public Object deploy(String jobId,int num){
        JobInfo jobid = new JobInfo();
        jobid.setJobId(jobId);
        JobInfo job = jobDao.selectObjOneByObject(jobid);

        System.out.println("Deployer select jobInfo By jobId:job="+job.toString());

        List<NodeInfo> nodes = requestClient.getNodeInfo();
        LinkedList<NodeInfo> selectNodes = selector.select(nodes,job,num);
        JenkinsHelper jenkins = new JenkinsHelper("","8080");
        List<String> records  = new LinkedList<>();
        try {
            records= jenkins.build2TargetNodes(job,selectNodes);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return createRecord(records,job);
    }

    public Object createJob(JenkinsJob job,int num){
        if(job.isIfBuildDefult())
            setDefultPublisherCommand(job);
        JobInfo jobInfo = JenkinsJobToJobInfo(job);     //获得jobInfo对象的硬件资源需求
        List<NodeInfo> nodes = requestClient.getNodeInfo();    //监控所有节点实时资源信息
        LinkedList<NodeInfo> selectNodes = selector.select(nodes,jobInfo,num);      //在所有nodes中选出符合jobInfo个需求的num个node
        JenkinsHelper jenkins = new JenkinsHelper("","8080");

        List<String> records  = new LinkedList<>();
        try {
            records= jenkins.createJob(job,selectNodes);    //创建job并部署到选择出的node上
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        if(records!=null){
            return createRecord(records,jobInfo);   //若部署成功，则记录部署详情，否则为空
        }else
            return "fail build";

    }
    private JobInfo JenkinsJobToJobInfo(JenkinsJob job){
        JobInfo jobInfo = new JobInfo();
        jobInfo.setJobId(UUID.randomUUID().toString().replace("-",""));
        jobInfo.setJobName(job.getJobName());
        jobInfo.setRequestCpu(job.getRequestCpu());
        jobInfo.setRequestDisk(job.getRequestDisk());
        jobInfo.setRequestMem(job.getRequestMem());
        jobInfo.setRequestIo(job.getRequestIO());
        jobDao.insertOne(jobInfo);
        return jobInfo;
    }
    private List<DeployRecord> createRecord(List<String> nodeNames,JobInfo job){
        List<DeployRecord> records = new LinkedList<>();
        for(String node:nodeNames){
            DeployRecord record = new DeployRecord();
            record.setDeployId(UUID.randomUUID().toString().replace("-",""));
            record.setJobId(job.getJobId());
            record.setJobName(job.getJobName());
            List<NodeInfo> nodeList = nodeDao.selectObjByWhere(new NodeInfo()," where node_name="+node);
            NodeInfo temp = nodeList.get(0);
            record.setNodeName(node);
            record.setNodeAddr(temp.getNodeAddr());
            record.setDeployTime(Long.toString(System.currentTimeMillis()));
            record.setDeployStatus("success");
            records.add(record);
            deployDao.insertOne(record);
        }
        return records;
    }
    private void setDefultPublisherCommand(JenkinsJob job){
        String sourceFilePath="JOB_NAME\\target\\"+job.getBuildJarFileName();
        String removePrefix = "JOB_NAME\\target";
        String remoteDir = "/home/workspace/JOB_NAME";
        String execCommand = "" +
                "cd /home/hxh" +
                "if [ ! -f \"start.sh\"];then" +
                "cat > start.sh << ENDSH" +
                "\n" +
                "#!/bin/bash\n" +
                "\n" +
                "if [ \"$2\" != \"\" ];then\n" +
                "fsrc=$3$1\"-\"$2\".jar\"\n" +
                "time=$(date +%Y%m%d%H%M)\n" +
                "fname=$3$1$time\".jar\"\n" +
                "\n" +
                "if [ -f $fsrc ]\n" +
                "then\n" +
                "echo \"mv $fsrc $fname\"\n" +
                "mv $fsrc $fname\n" +
                "fi\n" +
                "\n" +
                "if [ -f $fname ]\n" +
                "then\n" +
                "for loop in `ps -ef|grep $1|grep -v \".sh\"|grep -v \"grep\"|awk '{print $2}'`\n" +
                "do\n" +
                "        kill -s 9 $loop\n" +
                "        echo \"the $1 $loop are shutdown!\"\n" +
                "done\n" +
                "echo \"the $1 are shutdown!\"\n" +
                "fi\n" +
                "\n" +
                "echo \"${fname} start!\"\n" +
                "java -jar ${fname} >/dev/null &\n" +
                "fi\n" +
                "ENDSH" +
                "fi" +
                "chmod +x start.sh" +
                "/home/hxh/start.sh JOB_NAME 1.0.0 /home/workspace/JOB_NAME/";
        job.setBuildExecCommand(execCommand);
        job.setBuildRemoteDir(remoteDir);
        job.setBuildRemovePrefix(removePrefix);
        job.setBuildSourceFilesPath(sourceFilePath);

    }
}
