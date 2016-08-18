package com.woting.content.basicinfo.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.framework.util.StringUtils;
import com.woting.content.common.util.RequestUtils;

/**
 * 分类(字典)信息前台控制
 * @author wanghui
 */
@Controller
@RequestMapping(value="/baseinfo/")
public class CatalogController {

    /**
     * 添加一个分类
     */
    @RequestMapping(value="addCatalog.do")
    @ResponseBody
    public Map<String,Object> addCatalog(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        try {
            //0-获取参数
            Map<String, Object> m=RequestUtils.getDataFromRequest(request);
            if (m==null||m.size()==0) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }
            String catalogType=(m.get("CatalogType")==null?null:m.get("CatalogType")+"");
            String CatalogId=(m.get("CatalogId")==null?null:m.get("CatalogId")+"");
            Map<String,Object> data=(Map<String,Object>)m.get("Data");
            if (StringUtils.isNullOrEmptyOrSpace(catalogType)) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }
            if (data==null||data.isEmpty()) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }
            String name=(data.get("Name")==null?null:data.get("Name")+"");
            if (StringUtils.isNullOrEmptyOrSpace(name)) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }
            
            return null;
        } catch(Exception e) {
            e.printStackTrace();
            map.put("ReturnType", "T");
            map.put("TClass", e.getClass().getName());
            map.put("Message", e.getMessage());
            return map;
        }
    }

    /**
     * 修改一个分类
     */
    @RequestMapping(value="updateCatalog.do")
    @ResponseBody
    public Map<String,Object> updateCatalog(HttpServletRequest request) {
        return null;
    }

    /**
     * 删除一个分类
     */
    @RequestMapping(value="delCatalog.do")
    @ResponseBody
    public Map<String,Object> delCatalog(HttpServletRequest request) {
        return null;
    }
}