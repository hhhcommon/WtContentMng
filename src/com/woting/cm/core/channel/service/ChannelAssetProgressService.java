package com.woting.cm.core.channel.service;

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
}
