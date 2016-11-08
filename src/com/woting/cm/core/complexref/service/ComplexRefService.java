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
}
