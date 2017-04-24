package com.woting.crawlerdb.dict.service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.core.model.tree.TreeNodeBean;
import com.spiritdata.framework.ext.spring.redis.RedisOperService;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.TreeUtils;
import com.woting.WtContentMngConstants;
import com.woting.cm.core.channel.mem._CacheChannel;
import com.woting.cm.core.channel.model.Channel;
import com.woting.cm.core.channel.persis.po.ChannelMapRefPo;
import com.woting.cm.core.channel.service.ChannelMapService;
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
import com.woting.crawlerdb.dict.persis.po.DictRefPo;
import com.woting.crawlerdb.service.CrawlerService;
import com.woting.exceptionC.Wtcm1000CException;


@Service
public class CDictService {
	@Resource
	private DictContentService dictContentService;
	@Resource(name = "defaultDAO_DB")
	private MybatisDAO<CDictDetailPo> cDictDDao;
	@Resource(name = "defaultDAO_DB")
	private MybatisDAO<CDictMasterPo> cDictMDao;
	@Resource(name = "defaultDAO_DB")
	private MybatisDAO<DictRefPo> dictRefDao;
	@Resource
	private ChannelMapService channelMapService;
	@Resource
	private CrawlerService crawlerService;
	private _CacheCDictionary _cd = null;
	private _CacheChannel _cc=null;

	@PostConstruct
	public void initParam() {
		cDictDDao.setNamespace("A_CDICTD");
		cDictMDao.setNamespace("A_CDICTM");
		dictRefDao.setNamespace("A_DICTREF");
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
                param.put("isValidate", 1);
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

	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> addCDDAndDDRef(String applyType, String id, String refIds) {
		_cd = (_CacheCDictionary) (SystemCache.getCache(WtContentMngConstants.CACHE_CDICT)==null?null:SystemCache.getCache(WtContentMngConstants.CACHE_CDICT)).getContent();
        _cc=(SystemCache.getCache(WtContentMngConstants.CACHE_CHANNEL)==null?null:((CacheEle<_CacheChannel>)SystemCache.getCache(WtContentMngConstants.CACHE_CHANNEL)).getContent());
		String[] ids = refIds.split(",");
		if (ids!=null && ids.length>0) {
//			RedisOperService redis = null;
//			ServletContext sc=(SystemCache.getCache(FConstants.SERVLET_CONTEXT)==null?null:(ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent());
//	        if (WebApplicationContextUtils.getWebApplicationContext(sc)!=null) {
//	            JedisConnectionFactory js =(JedisConnectionFactory) WebApplicationContextUtils.getWebApplicationContext(sc).getBean("connectionFactory123");
//	            redis = new RedisOperService(js, 6);
//	        }
	        List<Map<String, Object>> retLs = new ArrayList<>();
			if (applyType.equals("1")) {
				if (_cc!=null) {
					TreeNode<Channel> _c=(TreeNode<Channel>)_cc.channelTree.findNode(id);
					if (_c!=null && _cd!=null) {
						List<ChannelMapRefPo> chamaps = new ArrayList<>();
						for (String did : ids) {
							TreeNode<CDictDetail> cdd = _cd.getCDictDetail("3", did);
							if (cdd!=null) {
								ChannelMapRefPo chamapref = new ChannelMapRefPo();
								chamapref.setId(SequenceUUID.getPureUUID());
								chamapref.setChannelId(id);
								chamapref.setSrcDid(did);
								chamapref.setSrcMid("3");
								chamapref.setSrcName(cdd.getTnEntity().getPublisher());
								chamapref.setcTime(new Timestamp(System.currentTimeMillis()));
								List<ChannelMapRefPo> chaps = channelMapService.getList(chamapref.getChannelId(), "3", chamapref.getSrcDid(), null, null);
								if (chaps==null || chaps.size()==0) {
									chamaps.add(chamapref);
									crawlerService.addCCateResRef(chamapref.getId());
									Map<String, Object> mapRef = new HashMap<>();
									mapRef.put("ReqId", chamapref.getChannelId());
									mapRef.put("ReqRefId", chamapref.getSrcDid());
									mapRef.put("Message", "信息处理成功");
									retLs.add(mapRef);
								} else {
									Map<String, Object> mapRef = new HashMap<>();
									mapRef.put("ReqId", chamapref.getChannelId());
									mapRef.put("ReqRefId", chamapref.getSrcDid());
									mapRef.put("Message", "信息已存在");
									retLs.add(mapRef);
								}
							}
						}
						channelMapService.insertList(chamaps);
						return retLs;
					}
				}
			} else {
				if (_cd!=null) {
					TreeNode<CDictDetail> cdd = _cd.getCDictDetail("3", id);
					if (cdd!=null && _cd!=null) {
						List<ChannelMapRefPo> chamaps = new ArrayList<>();
						for (String chaid  : ids) {
							TreeNode<Channel> _c=(TreeNode<Channel>)_cc.channelTree.findNode(chaid);
							if (_c!=null) {
								ChannelMapRefPo chamapref = new ChannelMapRefPo();
								chamapref.setId(SequenceUUID.getPureUUID());
								chamapref.setChannelId(chaid);
								chamapref.setSrcDid(id);
								chamapref.setSrcMid("3");
								chamapref.setSrcName(cdd.getTnEntity().getPublisher());
								chamapref.setcTime(new Timestamp(System.currentTimeMillis()));
								List<ChannelMapRefPo> chaps = channelMapService.getList(chamapref.getChannelId(), "3", chamapref.getSrcDid(), null, null);
								if (chaps==null || chaps.size()==0) {
									chamaps.add(chamapref);
									crawlerService.addCCateResRef(chamapref.getId());
									Map<String, Object> mapRef = new HashMap<>();
									mapRef.put("ReqId", chamapref.getChannelId());
									mapRef.put("ReqRefId", chamapref.getSrcDid());
									mapRef.put("Message", "信息处理成功");
									retLs.add(mapRef);
								} else {
									Map<String, Object> mapRef = new HashMap<>();
									mapRef.put("ReqId", chamapref.getChannelId());
									mapRef.put("ReqRefId", chamapref.getSrcDid());
									mapRef.put("Message", "信息已存在");
									retLs.add(mapRef);
								}
							}
						}
						channelMapService.insertList(chamaps);
						return retLs;
					}
				}
			}
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
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getCCateResRef(String applyType, String id, String publishers) {
        _cc=(SystemCache.getCache(WtContentMngConstants.CACHE_CHANNEL)==null?null:((CacheEle<_CacheChannel>)SystemCache.getCache(WtContentMngConstants.CACHE_CHANNEL)).getContent());
		List<Map<String, Object>> retLs = new ArrayList<>();
		if (applyType.equals("1")) {
			TreeNode<Channel> _c=(TreeNode<Channel>)_cc.channelTree.findNode(id);
			if (_c!=null) {
				String whereSql = null;
				if (publishers!=null) {
					String[] ps = publishers.split(",");
					for (String pstr : ps) {
						whereSql += ",'"+pstr+"'";
					}
					whereSql = " srcName not in "+"("+whereSql.substring(1)+")";
				}
				List<ChannelMapRefPo> chamaprefs = channelMapService.getList(id, null, null, null, whereSql);
				if (chamaprefs!=null && chamaprefs.size()>0) {
					_cd = (_CacheCDictionary) (SystemCache.getCache(WtContentMngConstants.CACHE_CDICT)==null?null:SystemCache.getCache(WtContentMngConstants.CACHE_CDICT)).getContent();
					for (ChannelMapRefPo chamaps : chamaprefs) {
						Map<String, Object> m = new HashMap<>();
						m.put("Id", chamaps.getId());
						m.put("SrcDid", chamaps.getSrcDid());
						TreeNode<CDictDetail> cdd = _cd.getCDictDetail("3", chamaps.getSrcDid());
						m.put("SrcName", cdd.getTreePathName().replace("外源内容分类/喜马拉雅/", "").replace("外源内容分类/蜻蜓/", ""));
						m.put("SrcSource", cdd.getTnEntity().getPublisher());
						m.put("ChannelId", id);
						m.put("ChannelSource", "我听科技");
						m.put("ChannelName", _c.getTreePathName().replace("栏目根/", ""));
						retLs.add(m);
					}
				}
			}
		} else {
			List<ChannelMapRefPo> chamaprefs = channelMapService.getList(null, "3", id, null, null);
			if (chamaprefs!=null && chamaprefs.size()>0) {
				_cd = (_CacheCDictionary) (SystemCache.getCache(WtContentMngConstants.CACHE_CDICT)==null?null:SystemCache.getCache(WtContentMngConstants.CACHE_CDICT)).getContent();
				TreeNode<CDictDetail> cdd = _cd.getCDictDetail("3", id);
				if (cdd!=null) {
					for (ChannelMapRefPo chamaps : chamaprefs) {
						Map<String, Object> m = new HashMap<>();
						m.put("Id", chamaps.getId());
						m.put("SrcDid", chamaps.getSrcDid());
						m.put("SrcName", cdd.getTreePathName().replace("外源内容分类/喜马拉雅/", "").replace("外源内容分类/蜻蜓/", ""));
						m.put("SrcSource", cdd.getTnEntity().getPublisher());
						m.put("ChannelId", chamaps.getChannelId());
						TreeNode<Channel> _c=(TreeNode<Channel>)_cc.channelTree.findNode(chamaps.getChannelId());
						m.put("ChannelSource", "我听科技");
						m.put("ChannelName", _c.getTreePathName().replace("栏目根/", ""));
						retLs.add(m);
					}
				}
			}
		}
		if (retLs!=null && retLs.size()>0) {
			return retLs;
		}
		return null;
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
	public List<Map<String, Object>> delDictResRef(String ids, boolean isOrNoRemove) {
		String[] chamapids = ids.split(",");
		if (chamapids!=null && chamapids.length>0) {
			List<Map<String, Object>> retLs = new ArrayList<>();
			RedisOperService redis = null;
			ServletContext sc=(SystemCache.getCache(FConstants.SERVLET_CONTEXT)==null?null:(ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent());
	        if (WebApplicationContextUtils.getWebApplicationContext(sc)!=null) {
	            JedisConnectionFactory js =(JedisConnectionFactory) WebApplicationContextUtils.getWebApplicationContext(sc).getBean("connectionFactory123");
	            redis = new RedisOperService(js, 6);
	        }
			for (String id : chamapids) {
				if (redis.get("wt_ChannelMap_Ref_"+id)!=null) {
					Map<String, Object> m = new HashMap<>();
					m.put("PerId", id);
					m.put("Message", "删除失败，关系正在处理");
					retLs.add(m);
				} else {
					if (isOrNoRemove) { // 删除栏目关系表数据
						crawlerService.deleteCCateResRef(id);
						Map<String, Object> m = new HashMap<>();
						m.put("PerId", id);
						m.put("Message", "开始关系执行关系删除");
						retLs.add(m);
					} else { // 只删除对应关系
						channelMapService.deleteById(id);
						Map<String, Object> m = new HashMap<>();
						m.put("PerId", id);
						m.put("Message", "关系删除成功");
						retLs.add(m);
					}
				}
			}
			return retLs;
		}
		return null;
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
						if(cdd.getmId().equals("3")) crawlerDictmName = "内容分类";
						mm.put("crawlerDictmName",crawlerDictmName);
						mm.put("crawlerDictdId", cdd.getId());
						mm.put("crawlerDictdName", cdd.getDdName());
						mm.put("publisher", cdd.getPublisher());
						mm.put("dictmId", dd.getMId());
						String dictmName = "";
						if(dd.getMId().equals("3")) dictmName = "内容分类";
						mm.put("dictmName", dictmName);
						mm.put("dictdId", drf.getDictDid());
						mm.put("dictdName", dd.getDdName());
						craw.add(mm);
					}
				}
			}
		}
		if (craw.size()>0) {
			CacheUtils.writeFile(JsonUtils.objToJson(craw), "/opt/WtCrawlerHotSpot/conf/craw.txt", true);
			return true;
		}
		return false;
	}
	
	public DictRefPo getDictRef(String resId, String resTableName, String dictDId) {
		Map<String, Object> m = new HashMap<>();
		if (resId!=null) {
			m.put("resId", resId);
		}
		if (resTableName!=null) {
			m.put("resTableName", resTableName);
		}
		if (dictDId!=null) {
			m.put("cdictDid", dictDId);
		}
		return dictRefDao.getInfoObject("getList", m);
	}
	
	public List<DictRefPo> getDictRefs(String resTableName, String dictDId) {
		Map<String, Object> m = new HashMap<>();
		if (resTableName!=null) {
			m.put("resTableName", resTableName);
		}
		if (dictDId!=null) {
			m.put("cdictDid", dictDId);
		}
		return dictRefDao.queryForList("getList", m);
	}
}
