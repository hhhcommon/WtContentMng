package com.woting.content.manage.channel.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.annotation.EnableLoadTimeWeaving;

import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.ui.tree.ZTree;
import com.spiritdata.framework.util.JsonUtils;
import com.woting.WtContentMngConstants;
import com.woting.cm.core.channel.mem._CacheChannel;
import com.woting.cm.core.channel.model.Channel;
import com.woting.cm.core.channel.persis.po.ChannelAssetPo;
import com.woting.cm.core.channel.persis.po.ChannelPo;
import com.woting.cm.core.channel.service.ChannelService;
import com.woting.content.manage.utils.ChannelUtils;

public class ChannelContentService {
	private _CacheChannel _cc=null;
	@Resource(name="defaultDAO")
    private MybatisDAO<ChannelAssetPo> channelAssetDao;
	@Resource(name="defaultDAO")
    private MybatisDAO<ChannelPo> channelDao;
	@Resource
	private ChannelService channelService;
	
	@PostConstruct 
    public void initParam() {
		channelDao.setNamespace("A_CHANNEL");
		channelAssetDao.setNamespace("A_CHANNELASSET");
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
	
	public Map<String, Object> getFiltrateByUserId(String userid, String mediatype) {
    	Map<String, Object> m = new HashMap<>();
    	m.put("publisherId", userid);
    	List<ChannelAssetPo> l = channelAssetDao.queryForList("getListBy", m);
    	if (l!=null && l.size()>0) {
    		_CacheChannel _cd=channelService.loadCache();
    		ZTree<Channel> zc = new ZTree<>(_cd.channelTree);
    		Map<String, Object> rem = new HashMap<>();
    		List<Map<String, Object>> smam = new ArrayList<>();
    		List<String> titles = new ArrayList<>();
    		for (ChannelAssetPo chaPo : l) {
    			Map<String, Object> mc = zc.findNode(chaPo.getChannelId()).toHashMap();
    			mc = ChannelUtils.buildChanelMap(mc);
    			Map<String, Object> mp = zc.findNode(chaPo.getChannelId()).getParent().toHashMap();
    			mp = ChannelUtils.buildChanelMap(mp);
				if (mp.get("nodeName").equals("栏目根")) {
					if (rem.containsKey(mc.get("nodeName"))) {
						continue;
					} else {
						rem.put(mc.get("nodeName")+"", mc);
						titles.add(mc.get("nodeName")+"");
					}
				} else {
					if (rem.containsKey(mp.get("nodeName"))) {
						Map<String, Object> m1 = (Map<String, Object>) rem.get(mp.get("nodeName"));
						List<Map<String, Object>> chils = (List<Map<String, Object>>) m1.get("children");
						if (chils!=null && chils.size()>0) {
							boolean isok = true;
							for (Map<String, Object> m2 : chils) {
								if(m2.get("nodeName").equals(mc.get("nodeName"))) {
									isok = false;
								}
							}
							if (isok) {
								chils.add(mc);
							}
						}
					} else {
						rem.put(mp.get("nodeName")+"", mp);
						titles.add(mp.get("nodeName")+"");
						List<Map<String, Object>> chils = new ArrayList<>();
						chils.add(mc);
						mp.put("children", chils);
					}
				}
				if (mediatype.equals("MediaAsset") && chaPo.getAssetType().equals("wt_SeqMediaAsset")) {
					Map<String, Object> m3 = new HashMap<>();
					m3.put("PubName", chaPo.getPubName());
					m3.put("FlowFlag", chaPo.getFlowFlag());
					m3.put("PubId", chaPo.getAssetId());
					m3.put("ChannelId", chaPo.getChannelId());
					smam.add(m3);
				}
			}
    		Map<String, Object> map = new HashMap<>();
    		if (rem!=null && rem.size()>0) {
    			List<Map<String, Object>> channels = new ArrayList<>();
    			for (String str : titles) {
					Map<String, Object> tm = (Map<String, Object>) rem.get(str);
					channels.add(tm);
				}
				map.put("ChannelList", channels);
			}
    		if (smam!=null && smam.size()>0) {
				map.put("SeqMediaList", smam);
			}
    		return map;
		}
    	return null;
    }
}
