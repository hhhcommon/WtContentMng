package com.woting.cm.core.complexref.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.woting.cm.core.complexref.persis.po.ComplexRefPo;

public class ComplexRefService {
	@Resource(name = "defaultDAO")
	private MybatisDAO<ComplexRefPo> complexRefDao;
	
	@PostConstruct
	public void initParam() {
		complexRefDao.setNamespace("A_COMPLEXREF");
	}
	
	public void insertComplexRef(List<ComplexRefPo> cs) {
		Map<String, Object> m = new HashMap<>();
		m.put("list", cs);
		complexRefDao.insert("insertComplexRefList", m);
	}
	
	public List<ComplexRefPo> getComplexRefList(String assetTableName, String assetId, String dictMID, String dictDId) {
		Map<String, Object> m = new HashMap<>();
		if (assetTableName!=null) {
			m.put("assetTableName", assetTableName);
		}
		if (assetId!=null) {
			m.put("assetId", assetId);
		}
		if (dictMID!=null) {
			m.put("dictMID", dictMID);
		}
		if (dictDId!=null) {
			m.put("dictDId", dictDId);
		}
		m.put("orderByClause", " cTime");
		List<ComplexRefPo> cps = complexRefDao.queryForList("getList", m);
		if (cps!=null && cps.size()>0) {
			return cps;
		}
		return null;
	}
	
	public void deleteComplexRef(String assetTableName, String assetId, String dictDId) {
		Map<String, Object> m = new HashMap<>();
		m.put("assetTableName", assetTableName);
		m.put("assetId", assetId);
		m.put("dictDId", dictDId);
		complexRefDao.delete("deleteByEntity", m);
	}
}
