package com.woting.content.broadcast.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.core.model.Page;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.core.model.tree.TreeNodeBean;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.TreeUtils;
import com.woting.WtContentMngConstants;
import com.woting.cm.core.broadcast.persis.po.BCLiveFlowPo;
import com.woting.cm.core.broadcast.persis.po.BCProgrammePo;
import com.woting.cm.core.broadcast.persis.po.BroadcastPo;
import com.woting.cm.core.dict.mem._CacheDictionary;
import com.woting.cm.core.dict.model.DictDetail;
import com.woting.cm.core.dict.model.DictModel;
import com.woting.cm.core.dict.persis.po.DictRefResPo;
import com.woting.cm.core.media.service.MediaService;
import com.woting.content.publish.utils.CacheUtils;

public class BroadcastProService {
	@Resource(name = "defaultDAO")
	private MybatisDAO<BroadcastPo> broadcastDao;
	@Resource(name = "defaultDAO")
	private MybatisDAO<BCLiveFlowPo> bcLiveFlowDao;
	@Resource(name = "defaultDAO")
	private MybatisDAO<DictRefResPo> dictRefResDao;
	@Resource(name = "defaultDAO")
	private MybatisDAO<BCProgrammePo> bcProDao;
	@Resource
	private MediaService mediaService;
//	private _CacheDictionary _cd;
	private Map<String, Integer> WeekDay = new HashMap<String, Integer>() {
		{
			put("get1", 7);
			put("get2", 1);
			put("get3", 2);
			put("get4", 3);
			put("get5", 4);
			put("get6", 5);
			put("get7", 6);
			put("update1", 2);
			put("update2", 3);
			put("update3", 4);
			put("update4", 5);
			put("update5", 6);
			put("update6", 7);
			put("update7", 1);
		}
	};
	
	
	@PostConstruct
	public void initParam() {
		broadcastDao.setNamespace("A_BROADCAST");
		bcLiveFlowDao.setNamespace("A_BCLIVEFLOW");
		dictRefResDao.setNamespace("A_DREFRES");
		bcProDao.setNamespace("A_BCPROGRAMME");
		
	}

	@SuppressWarnings("unchecked")
	public boolean addBroadcast(String userId, String bcTitle, String bcImg, String bcAreaId, String bcTypeId,
			List<Map<String, Object>> bcPlayPaths, String bcPublisher, String bcDescn) {
		boolean isok = true;
		for (Map<String, Object> map : bcPlayPaths) {
			if (map.get("IsMain").equals("1") && isok==true) {
				isok = false;
				continue;
			}
			if (map.get("IsMain").equals("1") && isok==false) {
				return false;
			}
		}
		if (isok) {
			return false;
		}
		BroadcastPo bPo = new BroadcastPo();
		bPo.setId(SequenceUUID.getUUIDSubSegment(4));
		bPo.setBcTitle(bcTitle);
		bPo.setBcPubType(2);
		bPo.setBcImg(bcImg);
		bPo.setBcPublisher(bcPublisher);
		if (bcDescn != null) {
			bPo.setDescn(bcDescn);
		}
		broadcastDao.insert(bPo);

		// 字典
		com.woting.cm.core.dict.mem._CacheDictionary _cd = ((CacheEle<_CacheDictionary>) SystemCache
				.getCache(WtContentMngConstants.CACHE_DICT)).getContent();
		// 字典--地区
		DictModel tempDictM = null;
		TreeNode<DictDetail> tempNode = null;
		String areaids[] = bcAreaId.split(",");
		for (String arid : areaids) {
			tempDictM = _cd.getDictModelById("2");
			tempNode = (TreeNode<DictDetail>) tempDictM.dictTree.findNode(arid);
			if (tempNode == null) {
				tempDictM = _cd.getDictModelById("9");
				tempNode = (TreeNode<DictDetail>) tempDictM.dictTree.findNode(arid);
			}
			if (tempNode != null) {
				DictRefResPo drrPo = new DictRefResPo();
				drrPo.setId(SequenceUUID.getUUIDSubSegment(4));
				drrPo.setRefName("电台所属地区");
				drrPo.setResTableName("wt_Broadcast");
				drrPo.setResId(bPo.getId());
				drrPo.setDictMid(tempDictM.getId());
				drrPo.setDictDid(arid);
				dictRefResDao.insert(drrPo);
			}
		}

		// 字典--分类
		String ids[] = bcTypeId.split(",");
		tempDictM = _cd.getDictModelById("1");
		for (int i = 0; i < ids.length; i++) {
			tempNode = (TreeNode<DictDetail>) tempDictM.dictTree.findNode(ids[i]);
			if (tempNode != null) {
				DictRefResPo drrPo = new DictRefResPo();
				drrPo.setId(SequenceUUID.getUUIDSubSegment(4));
				drrPo.setRefName("电台分类");
				drrPo.setResTableName("wt_Broadcast");
				drrPo.setResId(bPo.getId());
				drrPo.setDictMid(tempDictM.getId());
				drrPo.setDictDid(ids[i]);
				dictRefResDao.insert(drrPo);
			}
		}

		// 直播流
		for (Map<String, Object> ps : bcPlayPaths) {
			BCLiveFlowPo lfp = new BCLiveFlowPo();
		    lfp.setId(SequenceUUID.getUUIDSubSegment(4));
		    lfp.setBcId(bPo.getId());
		    lfp.setBcSrcType(2);
		    lfp.setBcSource(ps.get("BcSource")+"");
		    lfp.setFlowURI(ps.get("BcPlayPath")+"");
		    lfp.setIsMain(Integer.valueOf(ps.get("IsMain")+""));
		    bcLiveFlowDao.insert(lfp);
		}
		return true;
	}

	/**
	 * 新增内容
	 * 
	 * @param m
	 */
	@SuppressWarnings("unchecked")
	public void add(Map<String, Object> m) {
		BroadcastPo bPo = new BroadcastPo();
		bPo.setId(SequenceUUID.getUUIDSubSegment(4));
		bPo.setBcTitle(m.get("bcTitle") + "");
		bPo.setBcPubType(2);
		bPo.setBcPublisher(m.get("bcPublisher") + "");
		bPo.setBcURL(m.get("bcUrl") + "");
		bPo.setDescn(m.get("descn") + "");
		broadcastDao.insert(bPo);
		_CacheDictionary _cd = ((CacheEle<_CacheDictionary>) SystemCache.getCache(WtContentMngConstants.CACHE_DICT)).getContent();
		
		
		// 字典--地区
		String tempIds = m.get("bcArea") + "";
		DictModel tempDictM = _cd.getDictModelById("2");
		TreeNode<DictDetail> tempNode = (TreeNode<DictDetail>) tempDictM.dictTree.findNode(tempIds);
		if (tempNode != null) {
			DictRefResPo drrPo = new DictRefResPo();
			drrPo.setId(SequenceUUID.getUUIDSubSegment(4));
			drrPo.setRefName("电台所属地区");
			drrPo.setResTableName("wt_Broadcast");
			drrPo.setResId(bPo.getId());
			drrPo.setDictMid(tempDictM.getId());
			drrPo.setDictDid(tempIds);
			dictRefResDao.insert(drrPo);
		}
		// 字典--分类
		tempIds = m.get("cType") + "";
		String ids[] = tempIds.split(",");
		tempDictM = _cd.getDictModelById("1");
		for (int i = 0; i < ids.length; i++) {
			tempNode = (TreeNode<DictDetail>) tempDictM.dictTree.findNode(ids[i]);
			if (tempNode != null) {
				DictRefResPo drrPo = new DictRefResPo();
				drrPo.setId(SequenceUUID.getUUIDSubSegment(4));
				drrPo.setRefName("电台分类");
				drrPo.setResTableName("wt_Broadcast");
				drrPo.setResId(bPo.getId());
				drrPo.setDictMid(tempDictM.getId());
				drrPo.setDictDid(ids[i]);
				dictRefResDao.insert(drrPo);
			}
		}

		// 直播流
		String lfs = m.get("bcLiveFlows") + "";
		String[] fla = lfs.split(";;");
		boolean hasMain = false;
		for (int i = 0; i < fla.length; i++) {
			String[] _s = fla[i].split("=");
			BCLiveFlowPo lfp = new BCLiveFlowPo();
			lfp.setId(SequenceUUID.getUUIDSubSegment(4));
			lfp.setBcId(bPo.getId());
			lfp.setBcSrcType(Integer.parseInt(_s[2]));
			lfp.setBcSource(_s[0]);
			lfp.setFlowURI(_s[1]);
			lfp.setIsMain(0);
			if (Integer.parseInt(_s[3]) == 1 && !hasMain) {
				hasMain = true;
				lfp.setIsMain(1);
			}
			bcLiveFlowDao.insert(lfp);
		}
	}

	/**
	 * 修改电台
	 * 
	 * @param userId
	 * @param bcId
	 * @param bcTitle
	 * @param bcImg
	 * @param bcAreaId
	 * @param bcTypeId
	 * @param bcPlayPath
	 * @param bcPublisher
	 * @param isMain
	 * @param bcDescn
	 */
	@SuppressWarnings("unchecked")
	public boolean updateBroadcast(String userId, String bcId, String bcTitle, String bcImg, String bcAreaId,
			String bcTypeId, List<Map<String, Object>> bcPlayPaths, String bcPublisher, String bcDescn) {
		boolean isok = true;
		for (Map<String, Object> map : bcPlayPaths) {
			if (map.get("IsMain").equals("1") && isok==true) {
				isok = false;
				continue;
			}
			if (map.get("IsMain").equals("1") && isok==false) {
				return false;
			}
		}
		if (isok) {
			return false;
		}
		BroadcastPo bPo = new BroadcastPo();
		bPo.setId(bcId);
		bPo.setBcTitle(bcTitle);
		bPo.setBcPubType(2);
		bPo.setBcImg(bcImg);
		bPo.setBcPublisher(bcPublisher);
		if (bcDescn != null) {
			bPo.setDescn(bcDescn);
		}
		broadcastDao.update(bPo);

		// 字典
		dictRefResDao.delete("multiDelResIds", "'" + bcId + "'");// 先删除
		com.woting.cm.core.dict.mem._CacheDictionary _cd = ((CacheEle<_CacheDictionary>) SystemCache.getCache(WtContentMngConstants.CACHE_DICT)).getContent();
		// 字典--地区
		DictModel tempDictM = null;
		TreeNode<DictDetail> tempNode = null;
		String areaids[] = bcAreaId.split(",");
		for (String arid : areaids) {
			tempDictM = _cd.getDictModelById("2");
			tempNode = (TreeNode<DictDetail>) tempDictM.dictTree.findNode(arid);
			if (tempNode == null) {
				tempDictM = _cd.getDictModelById("9");
				tempNode = (TreeNode<DictDetail>) tempDictM.dictTree.findNode(arid);
			}
			if (tempNode != null) {
				DictRefResPo drrPo = new DictRefResPo();
				drrPo.setId(SequenceUUID.getUUIDSubSegment(4));
				drrPo.setRefName("电台所属地区");
				drrPo.setResTableName("wt_Broadcast");
				drrPo.setResId(bPo.getId());
				drrPo.setDictMid(tempDictM.getId());
				drrPo.setDictDid(arid);
				dictRefResDao.insert(drrPo);
			}
		}
		// 字典--分类
		String ids[] = bcTypeId.split(",");
		tempDictM = _cd.getDictModelById("1");
		for (int i = 0; i < ids.length; i++) {
			tempNode = (TreeNode<DictDetail>) tempDictM.dictTree.findNode(ids[i]);
			if (tempNode != null) {
				DictRefResPo drrPo = new DictRefResPo();
				drrPo.setId(SequenceUUID.getUUIDSubSegment(4));
				drrPo.setRefName("电台分类");
				drrPo.setResTableName("wt_Broadcast");
				drrPo.setResId(bPo.getId());
				drrPo.setDictMid(tempDictM.getId());
				drrPo.setDictDid(ids[i]);
				dictRefResDao.insert(drrPo);
			}
		}

		// 直播流
		bcLiveFlowDao.delete("multiDelBclf", "'"+bcId+"'");
		for (Map<String, Object> ps : bcPlayPaths) {
			BCLiveFlowPo lfp = new BCLiveFlowPo();
		    lfp.setId(SequenceUUID.getUUIDSubSegment(4));
			lfp.setBcId(bPo.getId());
			lfp.setBcSrcType(2);
			lfp.setBcSource(ps.get("BcSource")+"");
			lfp.setFlowURI(ps.get("BcPlayPath")+"");
			lfp.setIsMain(Integer.valueOf(ps.get("IsMain")+""));
			bcLiveFlowDao.insert(lfp);
		}
		return true;
	}

	/**
	 * 修改内容，子表都删除掉，再入库
	 * 
	 * @param m
	 */
	public void update(Map<String, Object> m) {
		String id = m.get("id") + "";
		BroadcastPo bPo = new BroadcastPo();
		bPo.setId(id);
		bPo.setBcTitle(m.get("bcTitle") + "");
		bPo.setBcPubType(2);
		bPo.setBcPublisher(m.get("bcPublisher") + "");
		bPo.setBcURL(m.get("bcUrl") + "");
		bPo.setDescn(m.get("descn") + "");
		broadcastDao.update(bPo);

		// 字典
		dictRefResDao.delete("multiDelBc", "'" + id + "'");// 先删除
		com.woting.cm.core.dict.mem._CacheDictionary _cd = ((CacheEle<_CacheDictionary>) SystemCache
				.getCache(WtContentMngConstants.CACHE_DICT)).getContent();
		// 字典--地区
		String tempIds = m.get("bcArea") + "";
		DictModel tempDictM = _cd.getDictModelById("2");
		TreeNode<DictDetail> tempNode = (TreeNode<DictDetail>) tempDictM.dictTree.findNode(tempIds);
		if (tempNode != null) {
			DictRefResPo drrPo = new DictRefResPo();
			drrPo.setId(SequenceUUID.getUUIDSubSegment(4));
			drrPo.setRefName("电台所属地区");
			drrPo.setResTableName("wt_Broadcast");
			drrPo.setResId(bPo.getId());
			drrPo.setDictMid(tempDictM.getId());
			drrPo.setDictDid(tempIds);
			dictRefResDao.insert(drrPo);
		}
		// 字典--分类
		tempIds = m.get("cType") + "";
		String ids[] = tempIds.split(",");
		tempDictM = _cd.getDictModelById("1");
		for (int i = 0; i < ids.length; i++) {
			tempNode = (TreeNode<DictDetail>) tempDictM.dictTree.findNode(ids[i]);
			if (tempNode != null) {
				DictRefResPo drrPo = new DictRefResPo();
				drrPo.setId(SequenceUUID.getUUIDSubSegment(4));
				drrPo.setRefName("电台分类");
				drrPo.setResTableName("wt_Broadcast");
				drrPo.setResId(bPo.getId());
				drrPo.setDictMid(tempDictM.getId());
				drrPo.setDictDid(ids[i]);
				dictRefResDao.insert(drrPo);
			}
		}

		// 直播流
		bcLiveFlowDao.delete("multiDelBc", "'" + id + "'");// 先删除
		String lfs = m.get("bcLiveFlows") + "";
		String[] fla = lfs.split(";;");
		boolean hasMain = false;
		for (int i = 0; i < fla.length; i++) {
			String[] _s = fla[i].split("=");
			BCLiveFlowPo lfp = new BCLiveFlowPo();
			lfp.setId(SequenceUUID.getUUIDSubSegment(4));
			lfp.setBcId(bPo.getId());
			lfp.setBcSrcType(Integer.parseInt(_s[2]));
			lfp.setBcSource(_s[0]);
			lfp.setFlowURI(_s[1]);
			lfp.setIsMain(0);
			if (Integer.parseInt(_s[3]) == 1 && !hasMain) {
				hasMain = true;
				lfp.setIsMain(1);
			}
			bcLiveFlowDao.insert(lfp);
		}
	}

	@SuppressWarnings("unchecked")
	public Page<Map<String, Object>> getViewList(Map<String, Object> m) {
		Map<String, Object> param = new HashMap<String, Object>();
		int pageIndex = Integer.parseInt(m.get("pageNumber") + "");
		int pageSize = Integer.parseInt(m.get("pageSize") + "");
		param.put("orderByClause", "a.CTime desc");
		if (m.get("mId") != null && m.get("rId") != null) {
			String mId = m.get("mId") + "";
			String rId = m.get("rId") + "";
			param.put("mId", mId);

			// 可通过当前节点获得其和下所有字节点列表
			_CacheDictionary _cd = ((CacheEle<_CacheDictionary>) SystemCache.getCache(WtContentMngConstants.CACHE_DICT)).getContent();
			DictModel tempDictM = _cd.getDictModelById(mId);
			TreeNode<DictDetail> root = (TreeNode<DictDetail>) tempDictM.dictTree.findNode(rId);
			// 得到所有下级结点的Id
			List<TreeNode<? extends TreeNodeBean>> allTn = TreeUtils.getDeepList(root);
			// 得到分类id的语句
			String orSql = root.getId();
			if (allTn != null && !allTn.isEmpty()) {
				for (TreeNode<? extends TreeNodeBean> tn : allTn) {
					orSql += ",'" + tn.getId() + "'";
				}
			}
			param.put("rId", orSql);
		}

		Page<Map<String, Object>> retP = broadcastDao.pageQueryAutoTranform(null, "query4ViewTemp", param, pageIndex,
				pageSize);
		// List<Map<String, Object>> retL =
		// broadcastDao.queryForListAutoTranform("query4ViewTemp", null);
		return retP;
	}

	public void del(String ids) {
		ids = ids.replaceAll(",", "','");
		ids = "'" + ids + "'";
		broadcastDao.delete("multiDelBc", ids);
		bcLiveFlowDao.delete("multiDelBclf", ids);
		dictRefResDao.delete("multiDelResIds", ids);
		bcProDao.delete("multiDelBcP", ids);
	}

	/**
	 * 
	 * @param bcId
	 * @return
	 */
	public List<Map<String, Object>> getBroadcastInfo(String bcId) {
		// 基本信息
		BroadcastPo bp = broadcastDao.getInfoObject("getInfoById", bcId);
		List<BroadcastPo> listpo = new ArrayList<>();
		listpo.add(bp);
		List<Map<String, Object>> l = mediaService.makeBcListToReturn(listpo);
		return l;
	}

	public List<DictRefResPo> getCataRefList(String ids) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("resTableName", "wt_Broadcast");
		param.put("resIds", ids);
		param.put("orderByClause", "resId, dictMid, bCode");
		List<DictRefResPo> rcrpL = dictRefResDao.queryForList("getListByResIds", param);
		return rcrpL;
	}

	public BroadcastPo getBroadcastList(String bcid) {
		BroadcastPo bc = broadcastDao.getInfoObject("getInfoById", bcid);
		return bc;
	}

	/**
	 * 分了获取电台列表
	 * 
	 * @param page
	 * @param pagesize
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getBroadcastListInfo(int page, int pagesize) {
		List<Map<String, Object>> bclist = new ArrayList<Map<String, Object>>();
		String bcliststr = CacheUtils.readFile("/opt/tomcat8_CM/webapps/CM/mweb/broadcast/FMInfo.txt");
		List<Map<String, Object>> l = (List<Map<String, Object>>) JsonUtils.jsonToObj(bcliststr, List.class);

		if (page == 0 && pagesize == 0) {
			for (Map<String, Object> m : l) {
				Map<String, Object> mbc = new HashMap<String, Object>();
				if (m.containsKey("streams")) {
					List<Map<String, Object>> ls = (List<Map<String, Object>>) m.get("streams");
					String url = ((Map<String, Object>) ls.get(0)).get("url") + "";
					mbc.put("channelName", m.get("channelName"));
					mbc.put("url", url);
				} else {
					mbc.put("channelName", m.get("channelName"));
					mbc.put("url", m.get("url"));
				}
				bclist.add(mbc);
			}
			return bclist;
		}

		if (l.size() - 1 < (page - 1) * pagesize)
			return null;

		for (int i = (page - 1) * pagesize; i < page * pagesize; i++) {
			if (l.size() - 1 < i && bclist.size() > 0)
				return bclist;
			Map<String, Object> mbc = new HashMap<String, Object>();
			Map<String, Object> m = l.get(i);
			if (m.containsKey("streams")) {
				List<Map<String, Object>> ls = (List<Map<String, Object>>) m.get("streams");
				String url = ((Map<String, Object>) ls.get(0)).get("url") + "";
				mbc.put("channelName", m.get("channelName"));
				mbc.put("url", url);
			} else {
				mbc.put("channelName", m.get("channelName"));
				mbc.put("url", m.get("url"));
			}
			bclist.add(mbc);
		}
		return bclist;
	}

	public List<Map<String, Object>> getBcProgrammes(String bcId) {
		Map<String, Object> m = new HashMap<>();
		m.put("bcId", bcId);
		m.put("sort", 0);
		m.put("orderByClause", "validTime desc");
		m.put("limitNum", 1);
		List<BCProgrammePo> bcps = bcProDao.queryForList("getList", m);
		if (bcps!=null && bcps.size()>0) {
			long vtime = bcps.get(0).getValidTime().getTime();
			m = new HashMap<>();
			m.put("bcId", bcId);
		    m.put("sort", 0);
		    m.put("validTime", new Timestamp(vtime));
		    m.put("orderByClause", "weekDay,BeginTime");
		    bcps = bcProDao.queryForList("getList", m);
		}
		if (bcps!=null && bcps.size()>0) {
			List<Map<String, Object>> bcms = new ArrayList<>();
			for (BCProgrammePo bcProgrammePo : bcps) {
				Map<String, Object> bcm = new HashMap<>();
				bcm.put("BcId", bcProgrammePo.getBcId());
				bcm.put("Title", bcProgrammePo.getTitle());
				bcm.put("BeginTime", bcProgrammePo.getBeginTime());
				bcm.put("EndTime", bcProgrammePo.getEndTime());
				bcm.put("CTime", bcProgrammePo.getcTime());
				bcm.put("WeekDay", WeekDay.get("get"+bcProgrammePo.getWeekDay()));
				bcms.add(bcm);
			}
			return bcms;
		}
		return null;
	}

	public boolean updateBcProgrammes(String userId, String bcId, List<Map<String, Object>> programmes) {
		List<BCProgrammePo> bcps = new ArrayList<>();
		long validTime = (System.currentTimeMillis()/86400000)*86400000-8*3600*1000;
		for (Map<String, Object> m : programmes) {
			BCProgrammePo bcp = new BCProgrammePo();
			bcp.setId(SequenceUUID.getPureUUID());
			bcp.setBcId(bcId);
			bcp.setTitle(m.get("Title")+"");
			bcp.setSort(0);
			bcp.setWeekDay(WeekDay.get("update"+m.get("WeekDay")));
			bcp.setBeginTime(m.get("BeginTime")+"");
			bcp.setEndTime(m.get("EndTime")+"");
			bcp.setValidTime(new Timestamp(validTime));
			bcps.add(bcp);
		}
		if (bcps!=null && bcps.size()>0) {
			Map<String, Object> m = new HashMap<>();
			m.put("bcId", bcId);
		    m.put("validTime", new Timestamp(validTime));
		    bcProDao.update("updateSort", m);
			Map<String, Object> map = new HashMap<>();
		    map.put("list", bcps);
			bcProDao.insert("insertList", map);
		    return true;
		}
		return false;
	}

	public Map<String, Object> getBroadcasts(String catalogType, String catalogId) {
		_CacheDictionary _cd = ((CacheEle<_CacheDictionary>) SystemCache.getCache(WtContentMngConstants.CACHE_DICT)).getContent();
		DictModel tempDictM = _cd.getDictModelById(catalogType);
		TreeNode<DictDetail> root = (TreeNode<DictDetail>) tempDictM.dictTree.findNode(catalogId);
		List<TreeNode<? extends TreeNodeBean>> Dictds = root.getChildren();
		String cataids = getCataIds(root);
		System.out.println(cataids);
		return null;
	}
	
	public String getCataIds(TreeNode<? extends TreeNodeBean> root){
		String cataids = "";
		cataids +=",'"+root.getId()+"'";
		List<TreeNode<? extends TreeNodeBean>> Dictds = root.getChildren();
		if (Dictds!=null && Dictds.size()>0) {
			for (TreeNode<? extends TreeNodeBean> treeNode : Dictds) {
				if (treeNode.getChildCount()>0) {
					cataids += getCataIds(treeNode);
				}else {
					cataids+=",'"+treeNode.getId()+"'";
				}
			}
		}
		cataids = cataids.substring(1);
		return cataids;
	}
}