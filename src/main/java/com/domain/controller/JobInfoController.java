package com.domain.controller;

import com.hisense.orm.resource.BaseController;
import com.domain.pojo.JobInfo;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hxh
 * @date today
 */

@RestController
@RequestMapping("/jobinfo")
public class JobInfoController extends BaseController<JobInfo>{
}
