package com.domain.util;

import com.alibaba.fastjson.JSONObject;
import com.cdancy.jenkins.rest.JenkinsClient;
import com.cdancy.jenkins.rest.domain.common.IntegerResponse;
import com.cdancy.jenkins.rest.domain.common.RequestStatus;
import com.cdancy.jenkins.rest.domain.job.BuildInfo;
import com.domain.pojo.JenkinsJob;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.el.ELException;
import javax.swing.plaf.synth.SynthEditorPaneUI;

/**
 * Created by  Domain
 * on 2019/2/14 9:35;
 */
public class JenkinsTest {
    public static void main(String[] args) throws DocumentException {
        //JenkinsClient client = JenkinsClient.builder().endPoint("http://:8080").build();
        //System.out.println(client.api().jobsApi().config(null,"aatest"));
        String publisher = "<jenkins.plugins.publish__over__ssh.BapSshPublisher plugin=\"publish-over-ssh@1.19.1\">\n" +
                "              <configName>100</configName>\n" +
                "              <verbose>false</verbose>\n" +
                "              <transfers>\n" +
                "                <jenkins.plugins.publish__over__ssh.BapSshTransfer>\n" +
                "                  <remoteDirectory>/home/hxh/saasp-oprsys/</remoteDirectory>\n" +
                "                  <sourceFiles>it_isc\\target\\it_isc-1.0.0.jar</sourceFiles>\n" +
                "                  <excludes/>\n" +
                "                  <removePrefix>it_isc\\target</removePrefix>\n" +
                "                  <remoteDirectorySDF>false</remoteDirectorySDF>\n" +
                "                  <flatten>false</flatten>\n" +
                "                  <cleanRemote>false</cleanRemote>\n" +
                "                  <noDefaultExcludes>false</noDefaultExcludes>\n" +
                "                  <makeEmptyDirs>false</makeEmptyDirs>\n" +
                "                  <patternSeparator>[, ] </patternSeparator>\n" +
                "                  <execCommand>/home/hxh/start.sh it_isc 1.0.0 /home/hxh/saasp-oprsys/</execCommand>\n" +
                "                  <execTimeout>120000</execTimeout>\n" +
                "                  <usePty>false</usePty>\n" +
                "                  <useAgentForwarding>false</useAgentForwarding>\n" +
                "                </jenkins.plugins.publish__over__ssh.BapSshTransfer>\n" +
                "              </transfers>\n" +
                "              <useWorkspaceInPromotion>false</useWorkspaceInPromotion>\n" +
                "              <usePromotionTimestamp>false</usePromotionTimestamp>\n" +
                "            </jenkins.plugins.publish__over__ssh.BapSshPublisher>";
        Document dom = DocumentHelper.parseText(publisher);
        Element publish = dom.getRootElement();
        System.out.println(publish.getName());
        Element execCommand = (Element) publish.selectSingleNode("//"+publish.getName()+"/transfers//execCommand");
        System.out.println(publish.asXML());
        System.err.println(execCommand.asXML());
        String command = execCommand.getText();
        String defultCommand="cd /home/hxh\n" +
                "if [ ! -f \"start.sh\"];then\n" +
                "cat > start.sh << ENDSH\n" +
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
                "ENDSH\n" +
                "fi\n" +
                "chmod +x start.sh\n";
        if(command.indexOf("\n")<=0)
            command = defultCommand+command;
        execCommand.setText(command);
        System.out.println(publish.asXML());







    }
}
