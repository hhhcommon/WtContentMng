package com.woting.content.common.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.framework.util.DateUtils;
import com.spiritdata.framework.util.StringUtils;
import com.woting.passport.UGA.persistence.pojo.UserPo;
import com.woting.passport.UGA.service.UserService;

@Controller
public class CommonController {
    @RequestMapping(value="/get.do")
    @ResponseBody
    public Map<String,Object> entryApp(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        return map;
    }
}