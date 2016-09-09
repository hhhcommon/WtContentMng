package com.woting.crawlerdb.dict.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.woting.cm.core.dict.persis.po.DictDetailPo;
import com.woting.cm.core.dict.persis.po.DictRefResPo;
import com.woting.content.manage.dict.service.DictContentService;
import com.woting.crawlerdb.dict.persis.po.CDictDPo;

@Service
public class CDictService {
	@Resource
	private DictContentService dictContentService;
	@Resource(name = "defaultDAO_DB")
	private MybatisDAO<CDictDPo> cDictDDao;

	@PostConstruct
	public void initParam() {
		cDictDDao.setNamespace("A_CDICTD");
	}

	/**
	 * 获取抓取库的分类数据
	 * 
	 * @param mId
	 * @param isValidate
	 * @param publisher
	 * @param maxdepth
	 * @return
	 */
	public List<Map<String, Object>> getCDictDList(String mId, int isValidate, String publisher, String maxdepth) {
		Map<String, Object> m = new HashMap<String, Object>();
		List<Map<String, Object>> cdd = new ArrayList<>();
		m.put("isValidate", isValidate);
		m.put("publisher", publisher);
		m.put("mId", mId);
		if (maxdepth.equals("1"))
			m.put("pId", "0");
		List<CDictDPo> dds = cDictDDao.queryForList("getList", m);
		if (dds == null || dds.size() == 0)
			return null;
		for (CDictDPo cd : dds) {
			Map<String, Object> mm = new HashMap<>();
			mm.put("Id", cd.getId());
			mm.put("MId", cd.getmId());
			mm.put("Title", cd.getDdName());
			mm.put("VisitUrl", cd.getVisitUrl());
			mm.put("Publisher", cd.getPublisher());
			DateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			mm.put("CTime", sd.format(cd.getcTime()));
			cdd.add(mm);
		}
		return cdd;
	}

	/**
	 * 添加资源库与抓取库字典映射关系
	 * @param dictmid
	 * @param dictdid
	 * @param cdictmid
	 * @param cdictdid
	 * @return 0：创建成功   1：已存在  2：创建失败
	 */
	public int addCDDAndDDRef(String dictmid, String dictdid, String cdictmid, String cdictdid) {
		String refname = "";
		if (dictmid.equals("3") && cdictmid.equals("3"))
			refname = "外部分类-内容分类";
		Map<String, Object> m = new HashMap<>();
		m.put("refName", "外部分类-内容分类");
		m.put("resTableName", "hotspot_DictD");
		m.put("resId", cdictdid);
		m.put("dictMid", dictmid);
		m.put("dictDid", dictdid);
		if(dictContentService.getDictRefResInfo(m)!=null) return 1;
		boolean isok = dictContentService.insertResDictRef(refname, "hotspot_DictD", cdictdid, dictmid, dictdid);
		if (isok)
			return 0;
		return 2;
	}

	/**
	 * 查询资源库与抓取库字典映射关系
	 * @param dictmid
	 * @param dictdid
	 * @param dictdname
	 * @param sourcenum
	 * @return
	 */
	public List<Map<String, Object>> getCCateResRef(String dictmid, String dictdid, String dictdname,String sourcenum) {
		DictDetailPo ddp = new DictDetailPo();
		CDictDPo cd = new CDictDPo();
		List<Map<String, Object>> ms = new ArrayList<>();
		Map<String, Object> m = new HashMap<>();
		String refname = "外部分类-内容分类";
		m.put("refName", refname);
		List<DictRefResPo> drrs = new ArrayList<>();
		if (sourcenum.equals("0")) {
			if (dictdname.equals("null")) {
				ddp = dictContentService.getDictDetailInfo(dictdid);
				if (ddp==null) return null;
			} else {
				ddp = dictContentService.getDictDetailInfo(dictdid);
				if(ddp==null || !ddp.getDdName().equals(dictdname))
					return null;
			}
			m.put("dictMid", dictmid);
			m.put("dictDid", dictdid);
			drrs = dictContentService.getDictRefList(m);
			if (drrs.size() > 0) {
				for (DictRefResPo drr : drrs) {
					Map<String, Object> mm = new HashMap<>();
					cd = getCDictDInfo(drr.getResId());
					if (cd == null)
						continue;
					mm.put("Id",drr.getId());
					mm.put("Title", ddp.getDdName());
					mm.put("SrcId", cd.getId());
					mm.put("SrcTitle", cd.getDdName());
					DateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					mm.put("CTime", sd.format(drr.getCTime()));
					ms.add(mm);
				}
			}
		}
		if(sourcenum.equals("1")) {
			if (dictdname.equals("null")) {
				cd = getCDictDInfo(dictdid);
				if (cd==null) return null;
			} else {
				cd = getCDictDInfo(dictdid);
				if(cd==null || !cd.getDdName().equals(dictdname))
					return null;
			}
			m.put("resId", dictdid);
			drrs = dictContentService.getDictRefList(m);
			if (drrs.size()>0) {
				for (DictRefResPo drr : drrs) {
					Map<String, Object> mm = new HashMap<>();
					ddp = dictContentService.getDictDetailInfo(drr.getDictDid());
					if(ddp==null)
						continue;
					mm.put("Id", drr.getId());
					mm.put("Title", cd.getDdName());
					mm.put("SrcId", ddp.getId());
					mm.put("SrcTitle", ddp.getDdName());
					DateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					mm.put("CTime", sd.format(drr.getCTime()));
					ms.add(mm);
				}
			}
		}
		return ms;
	}

	/**
	 * 查询抓取库分类信息
	 * @param id
	 * @return
	 */
	public CDictDPo getCDictDInfo(String id) {
		Map<String, Object> m = new HashMap<>();
		m.put("id", id);
		List<CDictDPo> cds = cDictDDao.queryForList("getList", m);
		if (cds!=null && cds.size()>0) {
			return cds.get(0);
		}
		return null;
	}
	
	/**
	 * 删除资源库与抓取库字典映射关系
	 * @param id
	 * @return
	 */
	public boolean delDictResRef(String id) {
		dictContentService.delDictRefRes(id);;
		Map<String, Object> m = new HashMap<>();
		m.put("id", id);
		if (dictContentService.getDictRefResInfo(m)!=null) {
			return false;
		}
		return true;
	}
}
