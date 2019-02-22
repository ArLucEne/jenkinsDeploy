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
import org.jclouds.date.TimeStamp;

@Getter
@Setter
@ApiModel(value="job_info",description="job_info")
@TbName(name = "job_info")
@ToString
public class JobInfo extends Page {
    @ApiModelProperty(value="jobId",name="job_id")
    @TbColumn(column = "job_id")
    public String jobId;
    @ApiModelProperty(value="jobName",name="job_name")
    @TbColumn(column = "job_name")
    public String jobName;
    @ApiModelProperty(value="requestMem",name="request_mem")
    @TbColumn(column = "request_mem")
    public String requestMem;
    @ApiModelProperty(value="requestCpu",name="request_cpu")
    @TbColumn(column = "request_cpu")
    public String requestCpu;
    @ApiModelProperty(value="requestDisk",name="request_disk")
    @TbColumn(column = "request_disk")
    public String requestDisk;
    @ApiModelProperty(value="requestIo",name="request_io")
    @TbColumn(column = "request_io")
    public String requestIo;
}
