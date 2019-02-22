package com.domain.controller;

import com.hisense.orm.resource.BaseController;
import com.domain.pojo.DeployRecord;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hxh
 * @date today
 */

@RestController
@RequestMapping("/deployrecord")
public class DeployRecordController extends BaseController<DeployRecord>{
}
