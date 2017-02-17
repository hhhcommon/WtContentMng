package com.woting.cm.core.person.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.woting.cm.core.person.persis.po.PersonPo;
import com.woting.cm.core.person.persis.po.PersonRefPo;

public class PersonService {
	@Resource(name = "defaultDAO")
	private MybatisDAO<PersonPo> personDao;
	@Resource(name = "defaultDAO")
	private MybatisDAO<PersonRefPo> personRefDao;
	
	@PostConstruct
	public void initParam() {
	    personDao.setNamespace("A_PERSON");
	    personRefDao.setNamespace("A_PERSONREF");
	}

	public PersonRefPo getPersonRefBy(String resTableName, String resId) {
		Map<String, Object> m = new HashMap<>();
		m.put("resTableName", resTableName);
		m.put("resId", resId);
		List<PersonRefPo> pfs = personRefDao.queryForList("getListBy", m);
		if (pfs!=null && pfs.size()>0) {
			return pfs.get(0);
		}
		return null;
	}
	
	public List<PersonRefPo> getPersonRefsBy(String resTableName, String resId) {
		Map<String, Object> m = new HashMap<>();
		m.put("resTableName", resTableName);
		m.put("resId", resId);
		List<PersonRefPo> pfs = personRefDao.queryForList("getListBy", m);
		if (pfs!=null && pfs.size()>0) {
			return pfs;
		}
		return null;
	}
	
	public int getPersonRefsByPIdAndResTableName(String personId, String resTableName) {
		Map<String, Object> m = new HashMap<>();
		m.put("resTableName", resTableName);
		m.put("personId", personId);
		return personRefDao.getCount("getCount", m);
		
	}
	
	public List<PersonRefPo> getPersonRefByPId(String personId, String resTableName) {
		Map<String, Object> m = new HashMap<>();
		m.put("resTableName", resTableName);
		m.put("personId", personId);
		List<PersonRefPo> pfs = personRefDao.queryForList("getListBy", m);
		if (pfs!=null && pfs.size()>0) {
			return pfs;
		}
		return null;
	}
	
	public List<PersonRefPo> getPersonRefByPIdAndMediaType(String personId, String resTableNames) {
		Map<String, Object> m = new HashMap<>();
		String orstr = " resTableName = 'wt_SeqMediaAsset' or resTableName = 'wt_MediaAsset'";
		if (resTableNames!=null) {
			String[] tableNames = resTableNames.split(",");
			if (tableNames.length>0) {
				orstr = "";
				for (String str : tableNames) {
					orstr += " or resTableName = '"+str+"'";
				}
				orstr = orstr.substring(3);
			}
		}
		m.put("ByClause", orstr);
		m.put("personId", personId);
		List<PersonRefPo> pfs = personRefDao.queryForList("getListBy", m);
		if (pfs!=null && pfs.size()>0) {
			return pfs;
		}
		return null;
	}
	
	public List<Map<String, Object>> getPersonByPId(String resId, String resTableName) {
		List<PersonRefPo> pfs = getPersonRefsBy(resTableName, resId);
		if (pfs!=null && pfs.size()>0) {
			List<Map<String, Object>> pmaps = new ArrayList<>();
			for (PersonRefPo personRefPo : pfs) {
				PersonPo po = getPersonPoById(personRefPo.getPersonId());
				if (po!=null) {
					Map<String, Object> m = new HashMap<>();
					m.put("resTableName", resTableName);
					m.put("resId", resId);
					m.put("pName", po.getpName());
					m.put("cName", "内容-主播");
					m.put("personId", po.getId());
					m.put("perImg", po.getPortrait());
					pmaps.add(m);
				}
			}
			return pmaps;
		}
		return null;
	}
	
	public PersonPo getPersonPoById(String id) {
		Map<String, Object> m = new HashMap<>();
		m.put("id", id);
		PersonPo po = personDao.getInfoObject("getList", m);
		if (po!=null) {
			return po;
		}
		return null;
	}
	
	public void insertPerson(List<PersonPo> ps) {
		Map<String, Object> m = new HashMap<>();
		m.put("list", ps);
		personDao.insert("insertList", m);
	}
	
	public void insertPerson(PersonPo po) {
		List<PersonPo> pos = new ArrayList<>();
		pos.add(po);
		Map<String, Object> m = new HashMap<>();
		m.put("list", pos);
		personDao.insert("insertList", m);
	}
	
	public void insertPersonRef(List<PersonRefPo> pfs) {
		Map<String, Object> m = new HashMap<>();
		m.put("list", pfs);
		personRefDao.insert("insertList", m);
	}
	
	public void insertPersonRef(PersonRefPo pf) {
		List<PersonRefPo> pfs = new ArrayList<>();
		pfs.add(pf);
		Map<String, Object> m = new HashMap<>();
		m.put("list", pfs);
		personRefDao.insert("insertList", m);
	}
	
	public void remove(String personId, String resTableName, String resId) {
		Map<String, Object> m = new HashMap<>();
		m.put("resTableName", resTableName);
		m.put("resId", resId);
		m.put("personId", personId);
		personRefDao.delete("deleteByMap", m);
	}
	
}
