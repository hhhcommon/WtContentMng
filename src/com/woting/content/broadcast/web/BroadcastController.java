package com.woting.content.broadcast.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.woting.content.common.util.RequestUtils;
import com.woting.content.broadcast.service.BroadcastService;

@Controller
@RequestMapping(value="/bc/")
public class BroadcastController {
    @Resource
    private BroadcastService bcService;

    @RequestMapping(value="add.do")
    @ResponseBody
    public Map<String,Object> addBroadcast(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        Map<String, Object> m=RequestUtils.getDataFromRequestParam(request);
        bcService.add(m);
        map=m;
        return map;
    }

    @RequestMapping(value="loadBc.do")
    @ResponseBody
    public List<Map<String,Object>> loadBc(HttpServletRequest request) {
        List<Map<String,Object>> retL=new ArrayList<Map<String, Object>>();
        Map<String, Object> m=RequestUtils.getDataFromRequestParam(request);
        retL = bcService.getViewList(m);
        if (retL!=null&&retL.size()>0) {
            for (Map<String,Object> one: retL) {
                String _temp=one.get("bcImg")+"";
                String _s[]= _temp.split("::");
                if (_s.length==1) one.put("typeName", toName(_s[0]));
                else {
                    one.put("typeName", toName(_s[0]));
                    one.put("areaName", _s[1]);
                }
            }
        }
        return retL;
    }
    private String toName(String code) {
        if (code.equals("dtType1")) return "新闻";
        else if (code.equals("dtType10")) return "体育";
        else if (code.equals("dtType2")) return "财经";
        else if (code.equals("dtType3")) return "生活";
        else if (code.equals("dtType4")) return "交通";
        else if (code.equals("dtType5")) return "综艺";
        else if (code.equals("dtType6")) return "音乐";
        else if (code.equals("dtType7")) return "故事";
        else if (code.equals("dtType8")) return "民族";
        else return "网络";
    }

    @RequestMapping(value="delBc.do")
    @ResponseBody
    public Map<String,Object> delBc(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        Map<String, Object> m=RequestUtils.getDataFromRequestParam(request);
        bcService.del(m.get("ids")+"");
        return map;
    }

    @RequestMapping(value="getCata4View.do")
    @ResponseBody
    public Map<String,Object> getCata4View(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        Map<String, Object> m=RequestUtils.getDataFromRequestParam(request);
        bcService.add(m);
        map=m;
        return map;
    }
}