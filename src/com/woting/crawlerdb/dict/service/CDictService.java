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
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.core.model.tree.TreeNodeBean;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.TreeUtils;
import com.woting.cm.core.dict.persis.po.DictDetailPo;
import com.woting.cm.core.dict.persis.po.DictRefResPo;
import com.woting.content.manage.dict.service.DictContentService;
import com.woting.content.publish.utils.CacheUtils;
import com.woting.crawlerdb.dict.mem._CacheCDictionary;
import com.woting.crawlerdb.dict.model.CDictDetail;
import com.woting.crawlerdb.dict.model.CDictMaster;
import com.woting.crawlerdb.dict.model.CDictModel;
import com.woting.crawlerdb.dict.persis.po.CDictDetailPo;
import com.woting.crawlerdb.dict.persis.po.CDictMasterPo;
import com.woting.exceptionC.Wtcm1000CException;

@Service
public class CDictService {
	@Resource
	private DictContentService dictContentService;
	@Resource(name = "defaultDAO_DB")
	private MybatisDAO<CDictDetailPo> cDictDDao;
	@Resource(name = "defaultDAO_DB")
	private MybatisDAO<CDictMasterPo> cDictMDao;
	private _CacheCDictionary _cd = null;

	@PostConstruct
	public void initParam() {
		cDictDDao.setNamespace("A_CDICTD");
		cDictMDao.setNamespace("A_CDICTM");
	}
	
	/**
     * 加载字典信息
     */
    @SuppressWarnings("unchecked")
	public _CacheCDictionary loadCache() {
        _cd=new _CacheCDictionary();

        try {
            //字典组列表
            Map<String, Object> param=new HashMap<String, Object>();
            List<CDictMasterPo> dmpol=cDictMDao.queryForList(param);
            if (dmpol==null||dmpol.size()==0) return null;
            List<CDictMaster> dml=new ArrayList<CDictMaster>();
            for (CDictMasterPo dmp: dmpol) {
                CDictMaster dm=new CDictMaster();
                dm.buildFromPo(dmp);
                dml.add(dm);
            }
            _cd.cdmlist =(dml.size()==0?null:dml);

            //组装dictModelMap
            if (_cd.cdmlist!=null&&_cd.cdmlist.size()>0) {
                //Map主对应关系
                for (CDictMaster dm: _cd.cdmlist) {
                    CDictModel dModel=new CDictModel(dm);
                    CDictDetail _t=new CDictDetail();
                    _t.setId(dModel.getId());
                    _t.setMid(dModel.getId());
                    _t.setNodeName("外源内容分类");
                    _t.setIsValidate(1);
                    _t.setParentId(null);
                    _t.setOrder(1);
                    _t.setBCode("root");
                    TreeNode<? extends TreeNodeBean> root=new TreeNode<CDictDetail>(_t);
                    dModel.cdictTree=(TreeNode<CDictDetail>)root;
                    _cd.cdictModelMap.put(dm.getId(), dModel);
                }
                
                //构造单独的字典树
                param.clear();
                String tempDmId="";
                param.put("mId", "3");
                List<CDictDetailPo> ddPage=cDictDDao.queryForList("getList", param);
                List<CDictDetailPo> ddpol=new ArrayList<CDictDetailPo>();
                ddpol.addAll(ddPage);
                
                if (ddpol==null||ddpol.size()==0) return _cd;
                List<CDictDetail> ddl=new ArrayList<CDictDetail>();
                for (CDictDetailPo ddp: ddpol) {
                    CDictDetail dd=new CDictDetail();
                    dd.buildFromPo(ddp);
                    ddl.add(dd);
                }
                _cd.cddlist=(ddl.size()==0?null:ddl);//字典项列表，按照层级结果，按照排序的广度遍历树

                List<CDictDetail> templ=new ArrayList<CDictDetail>();
                if (_cd.cddlist!=null&&_cd.cddlist.size()>0) {
                    for (CDictDetail dd: _cd.cddlist) {
                        if (tempDmId.equals(dd.getMid())) templ.add(dd);
                        else {
                            buildCDictTree(templ, _cd);
                            templ.clear();
                            templ.add(dd);
                            tempDmId=dd.getMid();
                        }
                    }
                    //最后一个记录的后处理
                    buildCDictTree(templ, _cd);
                }
            }
            //处理空树
            return _cd;
        } catch(Exception e) {
            e.printStackTrace();
            throw new Wtcm1000CException("加载Session中的字典信息", e);
        }
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
		List<CDictDetailPo> dds = cDictDDao.queryForList("getList", m);
		if (dds == null || dds.size() == 0)
			return null;
		for (CDictDetailPo cd : dds) {
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
	
	/*
     * 以ddList为数据源(同一字典组的所有字典项的列表)，构造所有者字典数据中的dictModelMap中的dictModel对象中的dictTree
     * @param ddList 同一字典组的所有字典项的列表
     * @param od 所有者字典数据
     */
    @SuppressWarnings("unchecked")
	private void buildCDictTree(List<CDictDetail> ddList, _CacheCDictionary cd) {
        if (ddList.size()>0) {//组成树
            CDictModel dModel=cd.cdictModelMap.get(ddList.get(0).getMid());
            if (dModel!=null) {
                Map<String, Object> m=TreeUtils.convertFromList(ddList);
                dModel.cdictTree.setChildren((List<TreeNode<? extends TreeNodeBean>>)m.get("forest"));
                //暂不处理错误记录
            }
        }
    }

	/**
	 * 添加资源库与抓取库字典映射关系
	 * @param dictmid
	 * @param dictdid
	 * @param cdictmid
	 * @param cdictdid
	 * @return 0：创建成功   1：已存在  2：创建失败
	 */
	public Map<String, Object> addCDDAndDDRef(String dictmid, String dictdid, String cdictmid, String cdictdid) {
		String refname = "";
		Map<String, Object> map = new HashMap<>();
		if (dictmid.equals("3") && cdictmid.equals("3"))
			refname = "外部分类-内容分类";
		Map<String, Object> m = new HashMap<>();
		m.put("refName", "外部分类-内容分类");
		m.put("resTableName", "hotspot_DictD");
		m.put("resId", cdictdid);
		m.put("dictMid", dictmid);
		m.put("dictDid", dictdid);
		if(dictContentService.getDictRefResInfo(m)!=null) {
			map.put("ReturnType", "1012");
			map.put("Message", "已存在");
			return map;
		}
		map = dictContentService.insertResDictRef(refname, "hotspot_DictD", cdictdid, dictmid, dictdid);
		if (map!=null) {
			CDictDetailPo cdd = cDictDDao.getInfoObject("getInfo", cdictdid);
			map.put("ReturnType", "1001");
			map.put("Message", "添加成功");
			map.put("Publisher", cdd.getPublisher());
			DateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			map.put("CTime", sd.format(System.currentTimeMillis()));
			return map;
		}
		return null;
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
		CDictDetailPo cd = new CDictDetailPo();
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
			m.put("orderByClause", "cTime desc");
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
					mm.put("Publisher", cd.getPublisher());
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
			m.put("orderByClause", "cTime desc");
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
					mm.put("Publisher", "我听");
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
	public CDictDetailPo getCDictDInfo(String id) {
		Map<String, Object> m = new HashMap<>();
		m.put("id", id);
		List<CDictDetailPo> cds = cDictDDao.queryForList("getList", m);
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
	
	public boolean saveCrawlerFile() {
		Map<String, Object> m = new HashMap<>();
		List<Map<String, Object>> craw = new ArrayList<>();
		m.put("refName", "外部分类-内容分类");
		m.put("resTableName", "hotspot_DictD");
		m.put("orderByClause", "cTime desc");
		List<DictRefResPo> drfs = dictContentService.getDictRefList(m);
		if(drfs!=null&&drfs.size()>0) {
			for (DictRefResPo drf : drfs) {
				DictDetailPo dd = dictContentService.getDictDetailInfo(drf.getDictDid());
				if(dd!=null) {
					CDictDetailPo cdd = getCDictDInfo(drf.getResId());
					if(cdd!=null) {
						Map<String, Object> mm = new HashMap<>();
						mm.put("crawlerDictmId", cdd.getmId());
						String crawlerDictmName = "";
						if(cdd.getmId().equals("3"))
							crawlerDictmName = "内容分类";
						mm.put("crawlerDictmName",crawlerDictmName);
						mm.put("crawlerDictdId", cdd.getId());
						mm.put("crawlerDictdName", cdd.getDdName());
						mm.put("publisher", cdd.getPublisher());
						mm.put("dictmId", dd.getMId());
						String dictmName = "";
						if(dd.getMId().equals("3"))
							dictmName = "内容分类";
						mm.put("dictmName", dictmName);
						mm.put("dictdId", drf.getDictDid());
						mm.put("dictdName", dd.getDdName());
						craw.add(mm);
					}
				}
			}
		}
		if (craw.size()>0) {
			CacheUtils.writeFile(JsonUtils.objToJson(craw), "/opt/WtCrawlerHotSpot/conf/craw.txt");
			return true;
		}
		return false;
	}
}
