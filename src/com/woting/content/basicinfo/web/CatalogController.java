package com.woting.content.basicinfo.web;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.framework.util.RequestUtils;
import com.spiritdata.framework.util.StringUtils;
import com.woting.cm.core.dict.model.DictDetail;
import com.woting.cm.core.dict.service.DictService;

/**
 * 分类(字典)信息前台控制
 * @author wanghui
 */
@Controller
@RequestMapping(value="/baseinfo/")
public class CatalogController {
    @Resource
    private DictService dictService;

    /**
     * 添加一个分类
     */
    @RequestMapping(value="addCatalog.do")
    @ResponseBody
    public Map<String,Object> addCatalog(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        try {
            //0-获取参数
            //权限管理，目前不做
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
            //1-组织参数
            DictDetail dd=new DictDetail(); //字典项业务对象
            dd.setMId(catalogType);
            if (StringUtils.isNullOrEmptyOrSpace(CatalogId)) dd.setParentId(CatalogId);
            dd.setDdName(name);
            dd.setAliasName((data.get("AliasName")==null?null:data.get("AliasName")+""));
            dd.setBCode((data.get("BCode")==null?null:data.get("BCode")+""));
            dd.setOrder((data.get("Sort")==null?0:Integer.parseInt(data.get("Sort")+"")));
            dd.setDesc((data.get("Descn")==null?null:data.get("Descn")+""));
            //2-加入字典项
            int ret=dictService.insertDictDetail(dd);
            if (ret==1) {
                map.put("ReturnType", "1001");
                map.put("Message", "添加成功");
            } else if (ret==2) {
                map.put("ReturnType", "1002");
                map.put("Message", "未找到父亲结点");
            } else if (ret==3) {
                map.put("ReturnType", "1003");
                map.put("Message", "名称重复");
            } else if (ret==4) {
                map.put("ReturnType", "1004");
                map.put("Message", "bCode重复");
            } else {
                map.put("ReturnType", "T");
                map.put("Message", "未知异常");
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

    /**
     * 修改一个分类
     */
    @RequestMapping(value="updateCatalog.do")
    @ResponseBody
    public Map<String,Object> updateCatalog(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        try {
            //0-获取参数
            //权限管理，目前不做
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
            //1-组织参数
            DictDetail dd=new DictDetail(); //字典项业务对象
            dd.setMId(catalogType);
            if (StringUtils.isNullOrEmptyOrSpace(CatalogId)) dd.setId(CatalogId);
            dd.setDdName(name);
            dd.setAliasName((data.get("AliasName")==null?null:data.get("AliasName")+""));
            dd.setBCode((data.get("BCode")==null?null:data.get("BCode")+""));
            dd.setOrder((data.get("Sort")==null?0:Integer.parseInt(data.get("Sort")+"")));
            int validate=(data.get("Validate")==null?1:Integer.parseInt(data.get("Validate")+""));
            dd.setIsValidate(validate>2&&validate<1?1:validate); //默认是生效的
            dd.setDesc((data.get("Descn")==null?null:data.get("Descn")+""));
            //2-修改字典项
            int ret=dictService.updateDictDetail(dd);
            if (ret==1) {
                map.put("ReturnType", "1001");
                map.put("Message", "添加成功");
            } else if (ret==2) {
                map.put("ReturnType", "1002");
                map.put("Message", "未找到对应结点");
            } else if (ret==3) {
                map.put("ReturnType", "1003");
                map.put("Message", "名称重复");
            } else if (ret==4) {
                map.put("ReturnType", "1004");
                map.put("Message", "bCode重复");
            } else if (ret==5) {
                map.put("ReturnType", "1005");
                map.put("Message", "与原信息相同，不必修改");
            } else {
                map.put("ReturnType", "T");
                map.put("Message", "未知异常");
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

    /**
     * 删除一个分类
     */
    @RequestMapping(value="delCatalog.do")
    @ResponseBody
    public Map<String,Object> delCatalog(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        try {
            //0-获取参数
            //权限管理，目前不做
            Map<String, Object> m=RequestUtils.getDataFromRequest(request);
            if (m==null||m.size()==0) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }
            String catalogType=(m.get("CatalogType")==null?null:m.get("CatalogType")+"");
            String CatalogId=(m.get("CatalogId")==null?null:m.get("CatalogId")+"");
            if (StringUtils.isNullOrEmptyOrSpace(CatalogId)) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }
            //1-组织参数
            DictDetail dd=new DictDetail(); //字典项业务对象
            dd.setMId(catalogType);
            if (StringUtils.isNullOrEmptyOrSpace(CatalogId)) dd.setId(CatalogId);
            int force=(m.get("Force")==null?1:Integer.parseInt(m.get("Force")+""));
            force=force==1?1:0;//默认是不允许强制删除的
            //2-修改字典项
            String ret=dictService.delDictDetail(dd, force);
            if (ret.equals("1")) {
                map.put("ReturnType", "1001");
                map.put("Message", "添加成功");
            } else if (ret.equals("2")) {
                map.put("ReturnType", "1002");
                map.put("Message", "未找到对应结点");
            } else {
                String s[]=ret.split("::");
                if (s.length!=2||!s[0].equals("3")) {
                    map.put("ReturnType", "T");
                    map.put("Message", "未知异常");
                } else {
                    map.put("ReturnType", "1003");
                    map.put("Message", s[1]);
                }
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
}