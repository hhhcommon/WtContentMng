package com.woting.content.common.cache;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.spiritdata.framework.component.UGA.cache.FrameworkUgaCLU;
import com.spiritdata.framework.core.cache.AbstractCacheLifecycleUnit;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.woting.WtContentMngConstants;
import com.woting.cm.core.dict.mem._CacheDictionary;
import com.woting.cm.core.dict.service.DictService;
import com.woting.exceptionC.Wtcm1000CException;

public class ContentCLU extends AbstractCacheLifecycleUnit {
    private Logger logger=LoggerFactory.getLogger(FrameworkUgaCLU.class);

    @Resource
    private DictService dictService;
    
    @Override
    public void init() {
        try {
            //装载数据字典
            loadDict();
        } catch (Exception e) {
            logger.info("启动时加载{Wt内容平台}缓存出错", e);
        }
   }

    @Override
    public void refresh(String arg0) {
    }

    private void loadDict() {
        try {
            System.out.println("开始装载[系统字典]缓存");
            _CacheDictionary _cd=dictService.loadCache();
            SystemCache.remove(WtContentMngConstants.CACHE_DICT);
            SystemCache.setCache(new CacheEle<_CacheDictionary>(WtContentMngConstants.CACHE_DICT, "系统字典", _cd));
        } catch(Exception e) {
            throw new Wtcm1000CException("缓存[系统字典]失败", e);
        }
        
    }
}