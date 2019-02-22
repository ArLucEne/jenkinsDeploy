package com.domain.pojo;
    /*
    * hxh
    * today
    */
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import com.hisense.orm.sql.Page;
import com.hisense.orm.sql.BaseTable;
import com.hisense.orm.annotation.TbColumn;
import com.hisense.orm.annotation.TbName;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@ApiModel(value="deploy_record",description="deploy_record")
@TbName(name = "deploy_record")
@ToString
public class DeployRecord extends Page {
    @ApiModelProperty(value="deployId",name="deploy_id")
    @TbColumn(column = "deploy_id")
    public String deployId;
    @ApiModelProperty(value="jobId",name="job_id")
    @TbColumn(column = "job_id")
    public String jobId;
    @ApiModelProperty(value="jobName",name="job_name")
    @TbColumn(column = "job_name")
    public String jobName;
    @ApiModelProperty(value="nodeAddr",name="node_addr")
    @TbColumn(column = "node_addr")
    public String nodeAddr;
    @ApiModelProperty(value="nodeName",name="node_name")
    @TbColumn(column = "node_name")
    public String nodeName;
    @ApiModelProperty(value="deployStatus",name="deploy_status")
    @TbColumn(column = "deploy_status")
    public String deployStatus;
    @ApiModelProperty(value="deployTime",name="deploy_time")
    @TbColumn(column = "deploy_time")
    public String deployTime;
}
