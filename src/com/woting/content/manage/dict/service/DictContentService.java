package com.woting.content.manage.dict.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.ui.tree.EasyUiTree;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.SpiritRandom;
import com.woting.WtContentMngConstants;
import com.woting.cm.core.dict.mem._CacheDictionary;
import com.woting.cm.core.dict.model.DictDetail;
import com.woting.cm.core.dict.model.DictModel;
import com.woting.cm.core.dict.model.DictRefRes;
import com.woting.cm.core.dict.persis.po.DictDetailPo;
import com.woting.cm.core.dict.persis.po.DictRefResPo;
import com.woting.cm.core.media.service.MediaService;

@Service
public class DictContentService {
	@Resource
	private MediaService mediaService;
	@Resource(name="defaultDAO")
    private MybatisDAO<DictDetailPo> dictdDao;
	@Resource(name="defaultDAO")
	private MybatisDAO<DictRefResPo> dictrefDao;
	
	@PostConstruct
    public void initParam() {
        dictdDao.setNamespace("A_DDETAIL");
        dictrefDao.setNamespace("A_DREFRES");
    }
	
	@SuppressWarnings("unchecked")
	public void addCataLogs(String mid, String did, String mediatype, String assetid) {
		// 保存专辑分类信息到wt_ResDict_Ref
		try {
			_CacheDictionary _cd = ((CacheEle<_CacheDictionary>) SystemCache.getCache(WtContentMngConstants.CACHE_DICT)).getContent();
			DictModel dm = _cd.getDictModelById(mid);
			EasyUiTree<DictDetail> eu1 = new EasyUiTree<DictDetail>(dm.dictTree);
			Map<String, Object> m = eu1.toTreeMap();
			String refname = "";
			String restablename = "";
			if (mediatype.equals("wt_MediaAsset")) {
				refname = "单体-内容分类";
				restablename = "wt_MediaAsset";
			} else {
				if (mediatype.equals("wt_SeqMediaAsset")) {
					refname = "专辑-内容分类";
					restablename = "wt_SeqMediaAsset";
				} else {
					if(mediatype.equals("wt_Broadcast")) {
						refname = "电台-内容分类";
						restablename = "wt_Broadcast";
					}
				}
			}
			List<Map<String, Object>> chillist = (List<Map<String, Object>>) m.get("children");
			for (Map<String, Object> map2 : chillist) {
				if (map2.get("id").equals(did)) {
					DictRefRes dicres = new DictRefRes();
					dicres.setId(SequenceUUID.getPureUUID());
					dicres.setRefName(refname);
					dicres.setResTableName(restablename);
					dicres.setResId(assetid);
					dicres.setDm(dm);
					DictDetail dicd = new DictDetail();
					dicd.setId(map2.get("id") + "");
					dicd.setMId(mid);
					Map<String, Object> l = (Map<String, Object>) map2.get("attributes");
					dicd.setBCode(l.get("bCode") + "");
					dicd.setDdName(l.get("nodeName") + "");
					dicres.setDd(dicd);
					dicres.setCTime(new Timestamp(System.currentTimeMillis()));
					mediaService.saveDictRef(dicres);
				}
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	public List<Map<String, Object>> getkeywordByDictMid(String mid,int keywordnum){
		List<Map<String, Object>> l = new ArrayList<Map<String,Object>>();
		Map<String, Object> m = new HashMap<String,Object>();
		m.put("mId", mid);
		m.put("begin", SpiritRandom.getRandom(new Random(), 0, 20000));
		m.put("size", keywordnum);
		List<DictDetailPo> dictDetailPos = dictdDao.queryForList("getKeyWords", m);
		for (DictDetailPo dictDetailPo : dictDetailPos) {
			if(dictDetailPo.getDdName().length()>4)continue;
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("Id", dictDetailPo.getId());
			map.put("Title", dictDetailPo.getDdName());
			map.put("Mid", dictDetailPo.getMId());
			l.add(map);
		}
		return l;
	}
	
	public DictDetailPo getDictDetailInfo(String id) {
		Map<String, Object> m = new HashMap<>();
		m.put("id", id);
		DictDetailPo ddp = dictdDao.getInfoObject("getInfo", m);
		return ddp;
	}
	
	public Map<String, Object> insertResDictRef(String refName, String refTableName, String resId, String dictMid, String dictDid) {
		DictRefResPo dictRefRes = new DictRefResPo();
		String id = SequenceUUID.getPureUUID();
		dictRefRes.setId(id);
		dictRefRes.setRefName(refName);
		dictRefRes.setResTableName(refTableName);
		dictRefRes.setResId(resId);
		dictRefRes.setDictMid(dictMid);
		dictRefRes.setDictDid(dictDid);
		int num = dictrefDao.insert("insert", dictRefRes);
		if(num>0) {
			Map<String, Object> m = new HashMap<>();
			m.put("Id", id);
			return m;
		}
		return null;
	}
	
	public List<DictRefResPo> getDictRefList(Map<String, Object> m) {
		return dictrefDao.queryForList("getList", m);
	}
	
	public DictRefResPo getDictRefResInfo(Map<String, Object> m) {
		return dictrefDao.getInfoObject("getInfo", m);
	}
	
	public void updataDictRefInfo(DictRefResPo dictRefResPo) {
		dictrefDao.update(dictRefResPo);
	}
	
	/**
	 * 查询内容的分类信息，默认Mid为3
	 * @param ids
	 * @param mediaType
	 * @return
	 */
	public List<Map<String, Object>> getDictRefListByIdsAndMeidaType(String ids, String mediaType) {
		Map<String, Object> m = new HashMap<>();
		m.put("resIds", ids);
		m.put("resTableName", mediaType);
		List<Map<String, Object>> ls = dictrefDao.queryForListAutoTranform("getListByResIdsAndResTableName", m);
		if (ls!=null && ls.size()>0) {
			return ls;
		}
		return null;
	}
	
	/**
     * 删除字典关联表里的信息
     * @param id
     */
    public void delDictRefRes(String id) {
    	String values = "id='"+id+"'";
    	dictrefDao.execute("delByDicts", values);
    }
    
}
