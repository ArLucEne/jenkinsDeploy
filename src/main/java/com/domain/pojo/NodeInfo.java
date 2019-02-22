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
@ApiModel(value="node_info",description="node_info")
@TbName(name = "node_info")
@ToString
public class NodeInfo extends Page {
    @ApiModelProperty(value="nodeId",name="node_id")
    @TbColumn(column = "node_id")
    public String nodeId;
    @ApiModelProperty(value="nodeAddr",name="node_addr")
    @TbColumn(column = "node_addr")
    public String nodeAddr;
    @ApiModelProperty(value="nodeName",name="node_name")
    @TbColumn(column = "node_name")
    public String nodeName;
    @ApiModelProperty(value="nodeDisk",name="node_disk")
    @TbColumn(column = "node_disk")
    public String nodeDisk;
    @ApiModelProperty(value="nodeIo",name="node_io")
    @TbColumn(column = "node_io")
    public String nodeIo;
    @ApiModelProperty(value="nodeCpu",name="node_cpu")
    @TbColumn(column = "node_cpu")
    public String nodeCpu;
    @ApiModelProperty(value="nodeMem",name="node_mem")
    @TbColumn(column = "node_mem")
    public String nodeMem;
}
