package com.woting.version.manage.web;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.framework.util.StringUtils;
import com.woting.version.core.model.Version;
import com.woting.version.core.model.VersionConfig;
import com.woting.version.core.service.VersionService;

@Lazy(true)
@Controller
@RequestMapping(value="/version/")
public class VersionController {
    @Resource
    private VersionService verService;

    @RequestMapping(value="getVerCfg.do")
    @ResponseBody
    public Map<String,Object> getVerCfg(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        try {
            //0-判断权限：目前没有这个功能
            //1-获取版本配置信息
            VersionConfig vfg=verService.getVerConfig();
            if (vfg==null) {
                map.put("ReturnType", "1011");
                map.put("Message", "获得版本配置信息为空");
            } else {
                map.put("ReturnType", "1001");
                map.put("VerCfgInfo", vfg.toHashMap());
            }
            return map;
        } catch(Exception e) {
            e.printStackTrace();
            map.put("ReturnType", "T");
            map.put("TClass", e.getClass().getName());
            map.put("Message", e.getMessage());
            return map;
        }
    }

    @RequestMapping(value="updateVerCfg.do")
    @ResponseBody
    public Map<String,Object> updateVerCfg(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        try {
            //0-判断权限：目前没有这个功能

            map.put("ServerStatus", "1");
            return map;
        } catch(Exception e) {
            e.printStackTrace();
            map.put("ReturnType", "T");
            map.put("TClass", e.getClass().getName());
            map.put("Message", e.getMessage());
            return map;
        }
    }
}