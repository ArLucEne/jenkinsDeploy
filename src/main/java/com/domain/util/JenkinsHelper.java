package com.domain.util;

import com.cdancy.jenkins.rest.JenkinsClient;
import com.cdancy.jenkins.rest.domain.common.IntegerResponse;
import com.cdancy.jenkins.rest.domain.common.RequestStatus;
import com.domain.pojo.JenkinsJob;
import com.domain.pojo.JobInfo;
import com.domain.pojo.NodeInfo;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.config.YamlProcessor;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by  Domain
 * on 2019/2/12 13:43;
 */

public class JenkinsHelper {
    private JenkinsClient client;
    private String jenkinsAddr = "";
    private String jenkinsPort = "8090";
    private String user = "admin";
    private String pwd = "admin";


    /**
     * 初始化jenkinsClient
     */
    public  JenkinsHelper(){
        String endpoint = "http://"+this.jenkinsAddr+":"+this.jenkinsPort;
        String credentials = this.user+":"+this.pwd;
        this.client=JenkinsClient.builder()
                .endPoint(endpoint)
                .credentials(credentials)
                .build();
    }

    public JenkinsHelper(String jenkinsAddr,String jenkinsPort){
        String endpoint = "http://"+jenkinsAddr+":"+jenkinsPort;
        this.client = JenkinsClient.builder()
                .endPoint(endpoint)
                .build();
    }

    public JenkinsHelper(String jenkinsAddr,String jenkinsPort,String user,String pwd){
        String endpoint = "http://"+jenkinsAddr+":"+jenkinsPort;
        String credentials = user+":"+pwd;
        this.client=JenkinsClient.builder()
                .endPoint(endpoint)
                .credentials(credentials)
                .build();
    }
    private boolean hasPublishers(Document dom){
        Element root = dom.getRootElement();
        String rootName = root.getName();
        List<Element> publishers = dom.selectNodes("//"+rootName+"/buildWrappers//publishers");
        System.out.println("if document hasPublishers:publishers="+publishers.toString());
        return !publishers.isEmpty();
    }

    public List<String> build2TargetNodes(JobInfo job, LinkedList<NodeInfo> nodes) throws DocumentException {
        String jobConfig = client.api().jobsApi().config(null,job.getJobName());
        Document dom = DocumentHelper.parseText(jobConfig);
        System.err.println(job.getJobName()+"'s original config:\n"+dom.asXML());
        Document clonedDom ;
        if(hasPublishers(dom))
            clonedDom = configClone(dom,nodes);
        else
            clonedDom = configAdd(dom,nodes);
        client.api().jobsApi().config(null,job.getJobName(),clonedDom.asXML());
        //IntegerResponse response = client.api().jobsApi().build(null,job.getJobName());
        if(true){

            return buildRecords(clonedDom);

        }else
            return null;


    }

    /**
     * 获得build记录
     * @param clonedDom
     * @return
     */
    private List<String> buildRecords(Document clonedDom) {
        Element root = clonedDom.getRootElement();
        String rootName = root.getName();
        Element publishRoot = (Element) clonedDom.selectSingleNode("//"+rootName+"/buildWrappers//publishers");
        List<String> buildNames = getBuiltNodesNames(publishRoot.elements());
        return buildNames;
    }

    /**
     * 扩容配置节点
     * 克隆配置文件，添加nodes中合适的节点
     * @param dom
     * @param nodes
     * @return
     */
    private Document configClone(Document dom, LinkedList<NodeInfo> nodes){
        Element root = dom.getRootElement();
        String rootName = root.getName();
        Element publishRoot = (Element) dom.selectSingleNode("//"+rootName+"/buildWrappers//publishers");
        System.out.println("JenkinsHelper clone config");
        List<Element> builtNodes = publishRoot.elements();
        int cloneNum = nodes.size()-builtNodes.size();        //本次需clone的节点数量
        List<String> builtNodesName = getBuiltNodesNames(builtNodes);  //获得之前已部署的节点
        LinkedList<NodeInfo> targetNodes = new LinkedList<>();
        for(NodeInfo node:nodes){                                 //将目标节点中之前已部署的节点删除  nodes.size>=buildedNodes.size
            if(!builtNodesName.contains(node.getNodeName()))
                targetNodes.add(node);
        }
        Element cloneTemplate = builtNodes.get(0); //将第一个publisher作为clone模板
        setDefultPublisherCommand(cloneTemplate);
        for(int i=0;i<cloneNum;i++){                //
                Element cloneEle = (Element) cloneTemplate.clone();
                Element configName = cloneEle.element("configName");
                //System.err.println(configName.getName());
                configName.setText(targetNodes.get(i).getNodeName());
                publishRoot.add(cloneEle);
        }
        System.err.println("JenkinsHelper cloned config:"+dom.asXML().toString());
        return dom;
    }

    public void setDefultPublisherCommand(Element publish){
        Document dom = publish.getDocument();
        System.out.println(publish.asXML());
        System.out.println(dom.asXML());
        Element execCommand = (Element) publish.selectSingleNode("//transfers//execCommand");
        String command = execCommand.getText();
        String defultCommand="cd /home/hxh/\n" +
                "if [ ! -f \"start.sh\"];then\n" +
                "cat > start.sh << ENDSH\n" +
                "\n" +
                "#!/bin/bash\n" +
                "\n" +
                "if [ \"$2\" != \"\" ];then\n" +
                "fsrc=$3$1\"-\"$2\".jar\"\n" +
                "time=$(date +%25Y%25m%25d%25H%25M)\n" +
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
                "ENDSH\n" +
                "fi\n" +
                "chmod %2Bx start.sh\n";
        if(command.indexOf("\n")<=0)
            command = defultCommand+command;
        execCommand.setText(command);
    }

    /**
     * 获得已部署的节点ConfigName队列
     * @param builtNodes
     * @return
     */
    private List<String> getBuiltNodesNames(List<Element> builtNodes){
        LinkedList<String> builtNodesName = new LinkedList<>();
        for(Element element:builtNodes){
            Element configName = element.element("configName");
            builtNodesName.add(configName.getText());
        }
        return builtNodesName;
    }

    /**
     *
     * 若原配置未配置publisher，则添加default节点
     * @param dom
     * @param nodes
     * @return
     * @throws DocumentException
     */
    private Document configAdd(Document dom,LinkedList<NodeInfo> nodes) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document publisherDom = reader.read("xmlTemplete//BuildwrapperTemplate.xml");
        Element publisherRoot = publisherDom.getRootElement();
        //System.err.println(publisherRoot.getName());
        Element publisher = (Element) publisherDom.selectSingleNode("//jenkins.plugins.publish__over__ssh.BapSshPostBuildWrapper//publishers");
        List<Element> temps = publisher.elements();
        Element cloneTemplante = temps.get(0);
        //System.err.println(cloneTemplante.getName());
        Element configName = cloneTemplante.element("configName");
        configName.setText(nodes.get(0).getNodeName());
        for(int i=1;i<nodes.size();i++){
            Element cloneEle = (Element) cloneTemplante.clone();
            Element name = cloneEle.element("configName");
            name.setText(nodes.get(i).getNodeName());
            publisher.add(cloneEle);
        }
        Element root = dom.getRootElement();
        Element publishers = root.element("buildWrappers");
        publishers.add(publisherRoot);
        System.err.println("JenkinsHelper added config:"+root.asXML());
        return root.getDocument();
    }

    /**
     * *****************创建job********
     * @param job
     * @param nodes
     * @return
     * @throws DocumentException
     */
    public List<String> createJob(JenkinsJob job, LinkedList<NodeInfo> nodes) throws DocumentException {
        String jobConfigXml = getXmlByJob(job,nodes);
        RequestStatus createResponse = client.api().jobsApi().create(null,job.getJobName(),jobConfigXml);      //创建job并提交配置文件
        if(createResponse.errors().size()==0){
            IntegerResponse buildResponse = client.api().jobsApi().build(null,job.getJobName());   //部署job
            if(buildResponse.errors().size()==0){
                List<String> buildNames = new LinkedList<>();
                for(NodeInfo node:nodes)
                    buildNames.add(node.getNodeName());
                return buildNames;
            }else {
                System.err.println(buildResponse.errors().toString());
                return null;
            }
        }else{
            System.err.println(createResponse.errors().toString());
            return null;
        }

    }

    /**
     * 通过JeninsJob创建相应xml配置文件
     * @param job
     * @param nodes
     * @return
     * @throws DocumentException
     */
    private String getXmlByJob(JenkinsJob job, LinkedList<NodeInfo> nodes) throws DocumentException {
        Document dom;
        SAXReader reader = new SAXReader();
        if("maven2-moduleset".equals(job.getJobCategory()))     //若创建mavenJob，选择maven模板，否则读project模板
            dom = reader.read("xmlTemplete//MavenTemplate.xml");
        else
            dom = reader.read("xmlTemplete//ProjectTemplate.xml");
        addGeneralNode(job,dom);
        Document scm;
        if("SubversionSCM".equals(job.getScmOption())) {
            scm = reader.read("xmlTemplete//SubversionTemplate.xml");
            addSubversionNode(job,scm);
        }
        else {
            scm = reader.read("xmlTemplete//GitSCMTemplate.xml");
            addGitNode(job,scm);
        }
        Element scmRoot = scm.getRootElement();
        Element root = dom.getRootElement();
        root.add(scmRoot);
        Element parentPublisher = (Element) dom.selectSingleNode("//"+root.getName()+"/buildWrappers//publishers");
        for(NodeInfo nodeInfo:nodes){
            Element publisher = addPublisher(job,nodeInfo);
            parentPublisher.add(publisher);
        }
        System.err.println(dom.asXML());
        return dom.asXML();

    }

    /**
     * 配置JenkinsJob中的常规属性
     * @param job
     * @param dom
     */
    private void addGeneralNode(JenkinsJob job,Document dom){
        Element root = dom.getRootElement();
        Element description = (Element) dom.selectSingleNode("//"+root.getName()+"/description");
        description.setText(job.getJobDescription());
        Element startegy = (Element) dom.selectSingleNode("//"+root.getName()+"/properties//strategy");
        Element daysToKeep = startegy.element("daysToKeep");
        daysToKeep.setText(job.getBuildStrategyDaysToKeep());
        Element numToKeep = startegy.element("numToKeep");
        numToKeep.setText(job.getBuildStrategyNumToKeep());
        Element goals = root.addElement("goals");
        goals.setText(job.getBuildGoals());
    }

    /**
     * 源码管理选择Subversion，配置其仓库路径和credentialsId
     * @param job
     * @param scm
     */
    private void addSubversionNode(JenkinsJob job,Document scm){
        Element root = scm.getRootElement();
        Element remote = (Element) scm.selectSingleNode("//"+root.getName()+"/locations//remote");
        remote.setText(job.getScmUrl());
        Element credentialsId = (Element) scm.selectSingleNode("//"+root.getName()+"/locations//credentialsId");
        credentialsId.setText(job.getScmCredentialsId());

    }

    /***
     * 源码管理为Git，配置路径和credentialsId
     * @param job
     * @param scm
     */
    private void addGitNode(JenkinsJob job,Document scm){
        Element root = scm.getRootElement();
        Element url = (Element) scm.selectSingleNode("//"+root.getName()+"//url");
        url.setText(job.getScmUrl());
        Element credentialsId = (Element) scm.selectSingleNode("//"+root.getName()+"/userRemoteConfigs//credentialsId");
        credentialsId.setText(job.getScmCredentialsId());
    }

    /**
     * 添加一个publisher节点
     * 设置Dir和command
     * @param job
     * @param node
     * @return
     * @throws DocumentException
     */
    private Element addPublisher(JenkinsJob job,NodeInfo node) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document publisher = reader.read("xmlTemplete/PublishersTemplate.xml");
        Element root = publisher.getRootElement();
        Element configName = root.element("configName");
        configName.setText(node.getNodeName());
        Element tag = (Element) publisher.selectSingleNode("//"+root.getName()+"/transfers/jenkins.plugins.publish__over__ssh.BapSshTransfer");
        Element sourceFiles = tag.element("sourceFiles");
        sourceFiles.setText(job.getBuildSourceFilesPath());
        Element removePrefix = tag.element("removePrefix");
        removePrefix.setText(job.getBuildRemovePrefix());
        Element remoteDir = tag.element("remoteDirectory");
        remoteDir.setText(job.getBuildRemoteDir());
        Element execCommand = tag.element("execCommand");
        execCommand.setText(job.getBuildExecCommand());
        return root;
    }
}
