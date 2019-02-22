package com.domain.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by  Domain
 * on 2019/2/14 15:18;
 */
@Getter
@Setter
@ToString
public class JenkinsJob {

    private String jobName;
    private int nodeNum;
    private String jobCategory;
    private String jobDescription;
    //基本配置
    private String buildStrategyDaysToKeep;
    private String buildStrategyNumToKeep;
    //源码管理
    private String scmOption;   //GitSCM;SubversionSCM
    private String scmUrl;
    private String scmCredentialsId;
    //构建环境
    private boolean ifBuildDefult;
    private String buildSourceFilesPath;
    private String buildJarFileName;
    private String buildRemoteDir;
    private String buildRemovePrefix;
    private String buildExecCommand;

    private String buildGoals;

    private String RequestCpu;
    private String RequestMem;
    private String RequestDisk;
    private String RequestIO;

    public void setDeafult(){
        this.jobName="aatest";
        this.nodeNum = 2;

        this.jobCategory = "maven2-moduleset";
        this.jobDescription = "test";
        this.buildStrategyDaysToKeep = "7";
        this.buildStrategyNumToKeep = "3";

        this.scmOption="SubversionSCM";
        this.scmCredentialsId = "5a5c929e-a402-47c2-9ffd-37473a8d25fd";
        this.scmUrl = "https://172.16.64.14:80/svn/SAASP/trunk/src/saasp-oprsys@HEAD";

        this.ifBuildDefult = true;
        this.buildJarFileName = "demo-0.0.1-SNAPSHOT.jar";
        this.buildSourceFilesPath = "target/demo-0.0.1-SNAPSHOT.jar";
        this.buildRemoteDir = "/test/service";
        this.buildRemovePrefix = "target";
        this.buildExecCommand = "ls";

        this.buildGoals = "clean";

        this.RequestCpu = "100";
        this.RequestDisk = "500";
        this.RequestIO = "100";
        this.RequestMem = "100";




    }
}
