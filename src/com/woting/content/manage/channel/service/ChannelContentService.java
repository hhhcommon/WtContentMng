package com.woting.content.manage.channel.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.ui.tree.ZTree;
import com.woting.WtContentMngConstants;
import com.woting.cm.core.channel.mem._CacheChannel;
import com.woting.cm.core.channel.model.Channel;
import com.woting.cm.core.channel.persis.po.ChannelAssetPo;
import com.woting.cm.core.channel.persis.po.ChannelPo;
import com.woting.cm.core.channel.service.ChannelService;
import com.woting.cm.core.media.persis.po.SeqMaRefPo;
import com.woting.cm.core.media.service.MediaService;
import com.woting.content.manage.utils.ChannelUtils;

public class ChannelContentService {
	private _CacheChannel _cc=null;
	@Resource(name="defaultDAO")
    private MybatisDAO<ChannelAssetPo> channelAssetDao;
	@Resource(name="defaultDAO")
    private MybatisDAO<ChannelPo> channelDao;
	@Resource
	private ChannelService channelService;
	@Resource
	private MediaService mediaService;
	private Map<String, Object> FlowFlagState = new HashMap<String, Object>() {
		{
			put("0", "已提交");
			put("1", "审核");
			put("2", "发布");
			put("3", "未通过");
			put("4", "撤回");
			put("已提交", "5");
			put("审核", "1");
			put("发布", "2");
			put("未通过", "3");
			put("撤回", "4");
		}
	};
	
	@SuppressWarnings("unchecked")
	@PostConstruct 
    public void initParam() {
		channelDao.setNamespace("A_CHANNEL");
		channelAssetDao.setNamespace("A_CHANNELASSET");
        _cc=(SystemCache.getCache(WtContentMngConstants.CACHE_CHANNEL)==null?null:((CacheEle<_CacheChannel>)SystemCache.getCache(WtContentMngConstants.CACHE_CHANNEL)).getContent());
    }
	
	@SuppressWarnings("unchecked")
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
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getFiltrateByUserId(String userid, String mediatype, List<Map<String, Object>> flowflags, List<Map<String, Object>> channelIds,
			List<Map<String, Object>> seqMediaIds) {
    	Map<String, Object> m = new HashMap<>();
    	m.put("publisherId", "0");
    	String wheresql = "";
    	if (flowflags!=null && flowflags.size()>0) {
    		String flowstr = "";
			for (Map<String, Object> ffm : flowflags) {
				flowstr += ",'"+ffm.get("Id")+"'";
			}
			flowstr = flowstr.substring(1);
			wheresql +=" and flowflag in ("+flowstr+")"; 
		}
    	if (channelIds!=null && channelIds.size()>0) {
    		String chastr = "";
			for (Map<String, Object> cham : channelIds) {
				chastr += ",'"+cham.get("Id")+"'";
			}
			chastr = chastr.substring(1);
			wheresql +=" and channelId in ("+chastr+")"; 
		}
    	if (seqMediaIds!=null && seqMediaIds.size()>0) {
			String mediaids = "";
			for (Map<String, Object> seqm : seqMediaIds) {
				List<SeqMaRefPo> smarefs = mediaService.getSeqMaRefBySid(seqm.get("Id")+"");
				if (smarefs!=null && smarefs.size()>0) {
					for (SeqMaRefPo seqMaRefPo : smarefs) {
						mediaids += ",'"+seqMaRefPo.getMId()+"'";
					}
				}
			}
			mediaids = mediaids.substring(1);
			wheresql +=" and assetId in ("+mediaids+")"; 
		}
    	if (mediatype.equals("SeqMedia")) {
			m.put("assetType", "wt_SeqMediaAsset");
			wheresql += " and assetId in (select resId from wt_Person_Ref where personId = '" + userid
				+ "' and resTableName = 'wt_SeqMediaAsset' )";
		} else {
			if (mediatype.equals("MediaAsset")) {
				wheresql += " and assetId in (select resId from wt_Person_Ref where personId = '" + userid
						+ "' and resTableName = 'wt_SeqMediaAsset' )";
			}
		}
    	if (wheresql.length()>3) {
			m.put("wheresql", wheresql);
		}
    	m.put("sortByClause", " cTime desc,pubTime desc");
    	List<ChannelAssetPo> l = channelAssetDao.queryForList("getListBy", m);
    	if (l!=null && l.size()>0) {
    		_CacheChannel _cd=channelService.loadCache();
    		ZTree<Channel> zc = new ZTree<>(_cd.channelTree);
    		Map<String, Object> rem = new HashMap<>();
    		List<Map<String, Object>> smam = new ArrayList<>();
    		List<Map<String, Object>> ffm = new ArrayList<>();
    		List<String> titles = new ArrayList<>();
    		String contentName = "";
    		for (ChannelAssetPo chaPo : l) {
    			try {
					if (mediatype.equals("MediaAsset") && chaPo.getAssetType().equals("wt_SeqMediaAsset")) {
						if (!contentName.contains(chaPo.getPubName())) {
							contentName += chaPo.getPubName();
						    Map<String, Object> m3 = new HashMap<>();
						    m3.put("PubName", chaPo.getPubName());
						    m3.put("FlowFlag", chaPo.getFlowFlag());
						    m3.put("PubId", chaPo.getAssetId());
						    m3.put("ChannelId", chaPo.getChannelId());
						    smam.add(m3);
						}
					}
	    			
	    			boolean fmIsOk = true;
	    			if (ffm!=null && ffm.size()>0) {
						for (Map<String, Object> fm : ffm) {
							if (fm.get("FlowFlagId").equals(FlowFlagState.get(FlowFlagState.get(chaPo.getFlowFlag()+"")))) {
								fmIsOk = false;
							}
						}
					}
	    			if (fmIsOk) {
	    				Map<String, Object> fm = new HashMap<>();
	    			    fm.put("FlowFlagId", FlowFlagState.get(FlowFlagState.get(chaPo.getFlowFlag()+"")));
	    			    fm.put("FlowFlagName", FlowFlagState.get(chaPo.getFlowFlag()+""));
	    			    ffm.add(fm);
					}
	    			
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
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
    		Map<String, Object> map = new HashMap<>();
    		if (rem!=null && rem.size()>0 && (channelIds==null || channelIds.size()==0)) {
    			List<Map<String, Object>> channels = new ArrayList<>();
    			for (String str : titles) {
					Map<String, Object> tm = (Map<String, Object>) rem.get(str);
					channels.add(tm);
				}
				map.put("ChannelList", channels);
			}
    		if (smam!=null && smam.size()>0 && (seqMediaIds==null || seqMediaIds.size()==0)) {
				map.put("SeqMediaList", smam);
			}
    		if (ffm!=null && ffm.size()>0 && (flowflags==null || flowflags.size()==0)) {
				map.put("FlowFlag", ffm);
			}
    		return map;
		}
    	return null;
    }
	
	public ChannelAssetPo getChannelAssetByAssetIdAndPubId(String assetId, String pubId, String assetType) {
		Map<String, Object> m = new HashMap<>();
		m.put("assetId", assetId);
		m.put("publisherId", pubId);
		m.put("assetType", assetType);
		List<ChannelAssetPo> chas = channelAssetDao.queryForList("getList", m);
		if (chas!=null && chas.size()>0) {
			return chas.get(0);
		}
		return null;
	}
	
	public List<Map<String, Object>> getPersonContentList(Map<String, Object> m) {
		List<Map<String, Object>> ls = channelAssetDao.queryForListAutoTranform("getPersonContentListBy", m);
		if (ls!=null && ls.size()>0) {
			return ls;
		}
		return null;
	}
	
	public ChannelAssetPo getChannelAssetByIdTypeAndChannelId(String assetId, String assetType, String channelId) {
		Map<String, Object> m = new HashMap<>();
		m.put("assetId", assetId);
		m.put("assetType", assetType);
		m.put("channelId", channelId);
		return channelAssetDao.getInfoObject("getListBy", m);
	}
	
	public List<Map<String, Object>> getChannelAssetsByAssetIdsAndAssetType(Map<String, Object> m) {
		List<Map<String, Object>> chas = channelAssetDao.queryForListAutoTranform("getChannelAndChannelAssetBy", m);
		if (chas!=null && chas.size()>0) {
			return chas;
		}
		return null;
	}
}
