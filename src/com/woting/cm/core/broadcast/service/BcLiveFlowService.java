package com.woting.cm.core.broadcast.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.woting.cm.core.broadcast.persis.po.BCLiveFlowPo;

@Service
public class BcLiveFlowService {
	
	@Resource(name="defaultDAO")
	private MybatisDAO<BCLiveFlowPo> bclfDao;
	
	@PostConstruct
    public void initParam() {
        bclfDao.setNamespace("A_BCLIVEFLOW");
    }
	
	public List<BCLiveFlowPo> getBCLiveFlowList(String bcSource){
		List<BCLiveFlowPo> bclflist = bclfDao.queryForList("getBCLiveFolwByBcSource", bcSource);
		return bclflist;
	}
	
	public Map<String, Object> getBCLFMap(String bcSource) {
		Map<String, Object> m = new HashMap<String,Object>();
		List<BCLiveFlowPo> bclflist = getBCLiveFlowList(bcSource);
		for (BCLiveFlowPo bcLiveFlowPo : bclflist) {
			m.put(bcLiveFlowPo.getBcSrcChannelId(), bcLiveFlowPo.getBcId());
		}
		return m;
	}
	
	public List<BCLiveFlowPo> getBcLiveFlowsByBcId(String bcId) {
		Map<String, Object> m = new HashMap<>();
		m.put("bcId", bcId);
//		m.put("isMain", "1");
		List<BCLiveFlowPo> ls = bclfDao.queryForList("getList", m);
		if (ls!=null && ls.size()>0) {
			return ls;
		}
		return null;
	}
	
	public void insertBCLiveFlowList(List<BCLiveFlowPo> bclflist) {
		bclfDao.insert("insertList", bclflist);
	}
	
	public void updateBCLiveFlowList(List<BCLiveFlowPo> bclflist) {
		bclfDao.update("updateList", bclflist);
	}
}
