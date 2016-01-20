package com.woting.content.broadcast.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.woting.content.common.util.RequestUtils;
import com.woting.content.pubref.persistence.pojo.ResCataRefPo;
import com.woting.dictionary.model.DictDetail;
import com.woting.dictionary.model.DictModel;
import com.woting.dictionary.model._CacheDictionary;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.model.Page;
import com.spiritdata.framework.ui.tree.EasyUiTree;
import com.woting.WtContentMngConstants;
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
        map.put("returnType","1001");
        return map;
    }
    @RequestMapping(value="update.do")
    @ResponseBody
    public Map<String,Object> updateBroadcast(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        Map<String, Object> m=RequestUtils.getDataFromRequestParam(request);
        bcService.update(m);
        map.put("returnType","1001");
        return map;
    }

    @RequestMapping(value="loadBc.do")
    @ResponseBody
    public Collection<Map<String,Object>> loadBc(HttpServletRequest request) {
        Page<Map<String,Object>> _p=new Page<Map<String, Object>>();
        Map<String, Object> m=RequestUtils.getDataFromRequestParam(request);
        _p = bcService.getViewList(m);
        Collection<Map<String,Object>> retResult=_p.getResult();
        if (retResult!=null&&retResult.size()>0) {
            String ids="";
            for (Map<String,Object> one: retResult) {//此次扫描，得到所有的Id
                ids=","+one.get("id");
            }
            List<ResCataRefPo> rcrpL = bcService.getCataRefList("'"+ids.substring(1)+"'");
            if (rcrpL!=null&&rcrpL.size()>0) {
                for (Map<String,Object> one: retResult) {//此次扫描，填充数据
                    ids=""+one.get("id");
                    String areaName="", typeName="";
                    boolean up=false, down=false;
                    for (int i=0; i<rcrpL.size()-1; i++) {
                        if (up&&down) break;
                        ResCataRefPo rcrp=rcrpL.get(i);
                        if (rcrp.getResId().equals(ids)) {
                            if (!up) up=!up;
                            if (rcrp.getDictMid().equals("1")) typeName+=","+rcrp.getTitle();
                            else if (rcrp.getDictMid().equals("2")) areaName+=","+rcrp.getTitle();
                        } else {
                            if (up) down=!down;
                            break;
                        }
                    }
                    if (up&&down) {
                        typeName=typeName.substring(1);
                        areaName=areaName.substring(1);
                    }
                    
                }
            }
        }
        return retResult;
    }

    @RequestMapping(value="delBc.do")
    @ResponseBody
    public Map<String,Object> delBc(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        Map<String, Object> m=RequestUtils.getDataFromRequestParam(request);
        bcService.del(m.get("ids")+"");
        return map;
    }

    @RequestMapping(value="getCataTrees4View.do")
    @ResponseBody
    //这是一个临时方法
    public Map<String,Object> getCataTrees4View(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        List<Map<String, Object>> l = new ArrayList<Map<String, Object>>();
        CacheEle<_CacheDictionary> cache=((CacheEle<_CacheDictionary>)SystemCache.getCache(WtContentMngConstants.CACHE_DICT));
        if (cache==null) {
            map.put("jsonType", "0");
            map.put("data", "没有数据");
        } else {
            try {
                _CacheDictionary _cd = ((CacheEle<_CacheDictionary>)SystemCache.getCache(WtContentMngConstants.CACHE_DICT)).getContent();
                DictModel dm=_cd.getDictModelById("1");
                EasyUiTree<DictDetail> eu1=new EasyUiTree<DictDetail>(dm.dictTree);
                l.add(eu1.toTreeMap());
                dm=_cd.getDictModelById("2");
                eu1=new EasyUiTree<DictDetail>(dm.dictTree);
                l.add(eu1.toTreeMap());
                map.put("jsonType", "1");
                map.put("data", l);
            } catch (CloneNotSupportedException e) {
                map.put("jsonType", "2");
                map.put("err", e.getMessage());
                e.printStackTrace();
            }
        }
        return map;
    }

    @RequestMapping(value="getInfo.do")
    @ResponseBody
    public Map<String,Object> getInfo(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        Map<String, Object> m=RequestUtils.getDataFromRequestParam(request);
        m=bcService.getInfo(m.get("bcId")+"");
        if (m==null) {
            map.put("returnType","1002");
        } else {
            map.put("bcInfo", m);
            map.put("returnType","1001");
        }
        return map;
    }
}