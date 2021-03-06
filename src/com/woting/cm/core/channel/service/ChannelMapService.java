package com.woting.cm.core.channel.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.woting.cm.core.channel.persis.po.ChannelMapRefPo;

@Service
public class ChannelMapService {
	@Resource(name="defaultDAO")
    private MybatisDAO<ChannelMapRefPo> channelMapRefDao;
	
	@PostConstruct 
    public void initParam() {
		channelMapRefDao.setNamespace("A_CHANNELMAPREF");
    }
	
	public ChannelMapRefPo getInfo(String id) {
		Map<String, Object> m = new HashMap<>();
		m.put("id", id);
		return channelMapRefDao.getInfoObject("getList", m);
	}
	
	public List<ChannelMapRefPo> getList(String channelId, String srcMid, String srcDid, String srcName, String whereSql) {
		Map<String, Object> m = new HashMap<>();
		if (channelId!=null) {
			m.put("channelId", channelId);
		}
		if (srcMid!=null) {
			m.put("srcMid", srcMid);
		}
		if (srcDid!=null) {
			m.put("srcDid", srcDid);
		}
		if (srcName!=null) {
			m.put("srcName", srcName);
		}
		if (whereSql!=null) {
			m.put("whereSql", whereSql);
		}
		List<ChannelMapRefPo> chamaps = channelMapRefDao.queryForList(m);
		if (chamaps!=null && chamaps.size()>0) {
			return chamaps;
		}
		return null;
	}
	
	public void insertList(List<ChannelMapRefPo> chamaps) {
		if (chamaps!=null && chamaps.size()>0) {
			Map<String, Object> m = new HashMap<>();
			m.put("list", chamaps);
			channelMapRefDao.insert("insertList", m);
		}
	}
	
	public void updateChannelMapRef(ChannelMapRefPo channelMapRefPo) {
		if (channelMapRefPo!=null) {
			channelMapRefDao.update(channelMapRefPo);
		}
	}
	
	public void deleteById(String id) {
		Map<String, Object> m = new HashMap<>();
		if (id!=null) {
			m.put("id", id);
			channelMapRefDao.delete("deleteByEntity", m);
		}
	}
	
	public void deleteBy(String channelId, String srcMid, String srcDid, String srcName) {
		Map<String, Object> m = new HashMap<>();
		if (channelId!=null) {
			m.put("channelId", channelId);
		}
		if (srcMid!=null) {
			m.put("srcMid", srcMid);
		}
		if (srcDid!=null) {
			m.put("srcDid", srcDid);
		}
		if (srcName!=null) {
			m.put("srcName", srcName);
		}
		channelMapRefDao.delete("deleteByEntity", m);
	}
	
	public void deleteBy(Map<String, Object> m) {
		channelMapRefDao.delete("delByIds", m);
	}
}
