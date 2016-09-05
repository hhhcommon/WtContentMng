package com.woting.content.manage.channel.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.woting.WtContentMngConstants;
import com.woting.cm.core.channel.mem._CacheChannel;
import com.woting.cm.core.channel.model.Channel;
import com.woting.cm.core.channel.persis.po.ChannelAssetPo;

public class ChannelContentService {
	private _CacheChannel _cc=null;
	
	@PostConstruct 
    public void initParam() {
        _cc=(SystemCache.getCache(WtContentMngConstants.CACHE_CHANNEL)==null?null:((CacheEle<_CacheChannel>)SystemCache.getCache(WtContentMngConstants.CACHE_CHANNEL)).getContent());
    }
	
	public List<Map<String, Object>> getChannelAssetList(List<ChannelAssetPo> chapolist){
		if(chapolist==null) return null;
		 List<Map<String, Object>> ret=new ArrayList<Map<String, Object>>();
	     if (_cc==null) _cc=(SystemCache.getCache(WtContentMngConstants.CACHE_CHANNEL)==null?null:((CacheEle<_CacheChannel>)SystemCache.getCache(WtContentMngConstants.CACHE_CHANNEL)).getContent());
	     for (ChannelAssetPo caPo: chapolist) {
	        Map<String, Object> one=caPo.toHashMap();
	        if (_cc!=null) {
	            TreeNode<Channel> _c=(TreeNode<Channel>)_cc.channelTree.findNode(caPo.getChannelId());
	            if (_c!=null) one.put("channelName", _c.getNodeName());
	        }
	        ret.add(one);
	    }
	    return ret;
	}
	
	public void updateChannelAsset(){
		
	}
}
