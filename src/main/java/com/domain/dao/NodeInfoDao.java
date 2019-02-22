package com.domain.dao;

import com.hisense.orm.dao.BaseDao;
import com.domain.pojo.NodeInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.LinkedList;
import java.util.List;

/*
    * hxh
    * today
    */
@Mapper
public interface NodeInfoDao extends BaseDao<NodeInfo> {
        @Select("select * from node_info")
         LinkedList<NodeInfo> getAllNodes();
}
