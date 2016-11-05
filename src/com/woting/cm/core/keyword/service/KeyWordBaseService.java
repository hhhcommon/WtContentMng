package com.woting.cm.core.keyword.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.woting.cm.core.keyword.persis.po.KeyWordPo;
import com.woting.cm.core.keyword.persis.po.KeyWordResPo;


public class KeyWordBaseService {
	
	@Resource(name = "defaultDAO")
	private MybatisDAO<KeyWordPo> keyWordDao;
	@Resource(name = "defaultDAO")
	private MybatisDAO<KeyWordResPo> keyWordResDao;
	
	@PostConstruct
	public void initParam() {
		keyWordDao.setNamespace("A_KEYWORD");
		keyWordResDao.setNamespace("A_KEYWORDRES");
	}
	
	public boolean KeyWordIsNull(String kwName) {
		if (kwName!=null) {
			List<KeyWordPo> kws = keyWordDao.queryForList("getKeyWord", kwName);
			if (kws!=null && kws.size()>0) return false;
			else return true;
		}
		return false;
	}
	
	public void insertKeyWords(List<KeyWordPo> kws) {
		if (kws!=null && kws.size()>0) {
			Map<String, Object> m = new HashMap<>();
			m.put("list", kws);
			keyWordDao.insert("insertKeyWordList", m);
		}
	}
	
	public void insertKwRefs(List<KeyWordResPo> krs) {
		if (krs!=null && krs.size()>0) {
			Map<String, Object> m = new HashMap<>();
			m.put("list", krs);
			keyWordResDao.insert("insertKwResList", m);
		}
	}
	
	public List<KeyWordPo> getRandKeyWordByOwner(String tagType, String userId, String resId, String tagsize) {
		Map<String, Object> m = new HashMap<>();
		m.put("resId", resId);
		m.put("size", Integer.valueOf(tagsize));
		m.put("isValidate", 1);
		if (tagType.equals("1")) {
			m.put("ownerId", "cm");
			m.put("ownerType", 0);
			List<KeyWordPo> kwlist = keyWordDao.queryForList("getKeyWordRand", m);
			if (kwlist!=null && kwlist.size()>0) {
				return kwlist;
			} else {
				return null;
			}
		}
		if (tagType.equals("2")) {
			m.put("ownerId", userId);
			m.put("ownerType", 1);
			List<KeyWordPo> kwlist = keyWordDao.queryForList("getKeyWordRand", m);
			if (kwlist!=null && kwlist.size()>0) {
				return kwlist;
			} else {
				return null;
			}
		}
		return null;
	}	
}
