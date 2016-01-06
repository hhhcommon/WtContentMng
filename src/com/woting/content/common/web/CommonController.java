package com.woting.content.common.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.ui.tree.ZTree;
import com.spiritdata.framework.util.StringUtils;
import com.woting.WtContentMngConstants;
import com.woting.content.common.util.RequestUtils;
import com.woting.dictionary.model.DictDetail;
import com.woting.dictionary.model.DictModel;
import com.woting.dictionary.model._CacheDictionary;

@Controller
@RequestMapping(value="/common/")
public class CommonController {
    @RequestMapping(value="getCataTreeWithSel.do")
    @ResponseBody
    public Map<String,Object> getCataTreeWithSel(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        Map<String, Object> m=RequestUtils.getDataFromRequestParam(request);
        String cataId=(String)m.get("cataId");
        if (!StringUtils.isNullOrEmptyOrSpace(cataId)&&!cataId.equals("null")) {
            _CacheDictionary _cd = ( (CacheEle<_CacheDictionary>)SystemCache.getCache(WtContentMngConstants.CACHE_DICT)).getContent();
            try {
                DictModel dm=_cd.getDictModelById(cataId);
                ZTree<DictDetail> eu1=new ZTree<DictDetail>(dm.dictTree);
                map.put("jsonType", "1");
                map.put("data", eu1.toTreeMap());
            } catch (CloneNotSupportedException e) {
                map.put("jsonType", "2");
                map.put("err", e.getMessage());
                e.printStackTrace();
            }
        } else {
            map.put("jsonType", "2");
            map.put("err", "无法得到正确的分类Id");
        }
        return map;
    }
}