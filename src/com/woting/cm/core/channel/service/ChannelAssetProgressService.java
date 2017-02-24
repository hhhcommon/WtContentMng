package com.woting.cm.core.channel.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.woting.cm.core.channel.persis.po.ChannelAssetProgressPo;

public class ChannelAssetProgressService {
	@Resource(name = "defaultDAO")
	private MybatisDAO<ChannelAssetProgressPo> channelAssetProgressDao;
	
	@PostConstruct
	public void initParam() {
		channelAssetProgressDao.setNamespace("A_CHANNELASSETPROGRESS");
	}
	
	public void insertChannelAssetProgress(ChannelAssetProgressPo cPo) {
		channelAssetProgressDao.insert(cPo);
	}
	
	public ChannelAssetProgressPo getChannelAssetProgress(Map<String, Object> m) {
		return channelAssetProgressDao.getInfoObject("getList", m);
	}
	
	public void updateChannelAssetProgress(ChannelAssetProgressPo cPo) {
		channelAssetProgressDao.update(cPo);
	}
	
	public List<Map<String, Object>> getChannelAssetProgresBy(String channelId, String sourceId, String mediaType, int applyFlowFlag, int reFlowFlag, int page, int pageSize) {
		Map<String, Object> m = new HashMap<>();
		String whereClauseBy = "";
		if (channelId!=null) {
			whereClauseBy += " and cha.channelId = '"+channelId+"'";
		}
		if (sourceId!=null) {
			whereClauseBy += " and cha.publisherId = '"+sourceId+"'";
		}
		if (mediaType!=null) {
			whereClauseBy += " and ("+mediaType+")";
		}
		m.put("whereClauseBy", whereClauseBy);
		
		if (applyFlowFlag!=0) {
			m.put("applyFlowFlag", applyFlowFlag);
		}
		m.put("reFlowFlag", reFlowFlag);
		m.put("limitClauseBy", " limit "+(page-1)*pageSize+","+page*pageSize);
		List<Map<String, Object>> ls = channelAssetProgressDao.queryForListAutoTranform("getListBy", m);
		if (ls!=null && ls.size()>0) {
			return ls;
		}
		return null;
	}
	
	public int getChannelAssetProgresByCount(String channelId, String sourceId, String mediaType, int applyFlowFlag, int reFlowFlag) {
		Map<String, Object> m = new HashMap<>();
		String whereClauseBy = "";
		if (channelId!=null) {
			whereClauseBy += " and cha.channelId = '"+channelId+"'";
		}
		if (sourceId!=null) {
			whereClauseBy += " and cha.publisherId = '"+sourceId+"'";
		}
		if (mediaType!=null) {
			whereClauseBy += " and ("+mediaType+")";
		}
		m.put("whereClauseBy", whereClauseBy);
		
		if (applyFlowFlag!=0) {
			m.put("applyFlowFlag", applyFlowFlag);
		}
		m.put("reFlowFlag", reFlowFlag);
		int num = channelAssetProgressDao.getCount("getListByCount", m);
		return num;
	}
	
	public List<Map<String, Object>> getChannelAssetProgresBy(int applyFlowFlag, int reFlowFlag, String whereClauseBy, String limitClauseBy) {
		Map<String, Object> m = new HashMap<>();
		if (applyFlowFlag!=0) {
			m.put("applyFlowFlag", applyFlowFlag);
		}
		m.put("reFlowFlag", reFlowFlag);
		m.put("whereClauseBy", whereClauseBy);
		m.put("limitClauseBy", limitClauseBy);
		List<Map<String, Object>> ls = channelAssetProgressDao.queryForListAutoTranform("getListBy", m);
		if (ls!=null && ls.size()>0) {
			return ls;
		}
		return null;
	}
	
	public void remove(String resId, String resTableName) {
		Map<String, Object> m = new HashMap<>();
		m.put("assetId", resId);
		m.put("assetType", resTableName);
		channelAssetProgressDao.delete("deleteByEntity", m);
	}
}
