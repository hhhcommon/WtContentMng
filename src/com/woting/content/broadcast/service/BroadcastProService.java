package com.woting.content.broadcast.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.print.attribute.standard.Media;

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
import com.woting.cm.core.dict.mem._CacheDictionary;
import com.woting.cm.core.dict.model.DictDetail;
import com.woting.cm.core.dict.model.DictModel;
import com.woting.cm.core.dict.persis.po.DictRefResPo;
import com.woting.cm.core.media.service.MediaService;
import com.woting.content.broadcast.persis.pojo.BroadcastPo;
import com.woting.content.broadcast.persis.pojo.FrequncePo;
import com.woting.content.broadcast.persis.pojo.LiveFlowPo;
import com.woting.content.publish.utils.CacheUtils;

public class BroadcastProService {
	@Resource(name = "defaultDAO")
	private MybatisDAO<BroadcastPo> broadcastDao;
	@Resource(name = "defaultDAO")
	private MybatisDAO<LiveFlowPo> bc_liveflowDao;
	@Resource(name = "defaultDAO")
	private MybatisDAO<FrequncePo> bc_frequnceDao;
	@Resource(name = "defaultDAO")
	private MybatisDAO<DictRefResPo> dictRefResDao;
	@Resource
	private MediaService mediaService;

	@PostConstruct
	public void initParam() {
		broadcastDao.setNamespace("BROADCAST");
		bc_liveflowDao.setNamespace("BC_LF");
		bc_frequnceDao.setNamespace("BC_F");
		dictRefResDao.setNamespace("A_DREFRES");
	}

	public void addBroadcast(String userId, String bcTitle, String bcImg, String bcAreaId, String bcTypeId,
			String bcPlayPath, String bcPublisher, String isMain, String bcDescn) {
		BroadcastPo bPo = new BroadcastPo();
		bPo.setId(SequenceUUID.getUUIDSubSegment(4));
		bPo.setBcTitle(bcTitle);
		bPo.setBcPubType(2);
		bPo.setBcImg(bcImg);
		bPo.setBcPublisher(bcPublisher);
		if (bcDescn != null) {
			bPo.setDesc(bcDescn);
		}
		broadcastDao.insert(bPo);

		// 字典
		com.woting.cm.core.dict.mem._CacheDictionary _cd = ((CacheEle<_CacheDictionary>) SystemCache
				.getCache(WtContentMngConstants.CACHE_DICT)).getContent();
		// 字典--地区
		DictModel tempDictM = null;
		tempDictM = _cd.getDictModelById("2");
		TreeNode<DictDetail> tempNode = null;
		tempNode = (TreeNode<DictDetail>) tempDictM.dictTree.findNode(bcAreaId);
		if (tempNode == null) {
			tempDictM = _cd.getDictModelById("9");
			tempNode = (TreeNode<DictDetail>) tempDictM.dictTree.findNode(bcAreaId);
		}

		if (tempNode != null) {
			DictRefResPo drrPo = new DictRefResPo();
			drrPo.setId(SequenceUUID.getUUIDSubSegment(4));
			drrPo.setRefName("电台所属地区");
			drrPo.setResTableName("wt_Broadcast");
			drrPo.setResId(bPo.getId());
			drrPo.setDictMid(tempDictM.getId());
			drrPo.setDictDid(bcAreaId);
			dictRefResDao.insert(drrPo);
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
		LiveFlowPo lfp = new LiveFlowPo();
		lfp.setId(SequenceUUID.getUUIDSubSegment(4));
		lfp.setBcId(bPo.getId());
		lfp.setBcSrcType(2);
		lfp.setBcSource("管理端录入");
		lfp.setFlowURI(bcPlayPath);
		lfp.setIsMain(Integer.valueOf(isMain));
		bc_liveflowDao.insert(lfp);
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
		bPo.setBcUrl(m.get("bcUrl") + "");
		bPo.setDesc(m.get("descn") + "");
		broadcastDao.insert(bPo);

		// 字典
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
		String lfs = m.get("bcLiveFlows") + "";
		String[] fla = lfs.split(";;");
		boolean hasMain = false;
		for (int i = 0; i < fla.length; i++) {
			String[] _s = fla[i].split("::");
			LiveFlowPo lfp = new LiveFlowPo();
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
			bc_liveflowDao.insert(lfp);
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
	public void updateBroadcast(String userId, String bcId, String bcTitle, String bcImg, String bcAreaId,
			String bcTypeId, String bcPlayPath, String bcPublisher, String isMain, String bcDescn) {
		BroadcastPo bPo = new BroadcastPo();
		bPo.setId(bcId);
		bPo.setBcTitle(bcTitle);
		bPo.setBcPubType(2);
		bPo.setBcImg(bcImg);
		bPo.setBcPublisher(bcPublisher);
		if (bcDescn != null) {
			bPo.setDesc(bcDescn);
		}
		broadcastDao.update(bPo);

		// 字典
		dictRefResDao.delete("multiDelBc", "'" + bcId + "'");// 先删除
		com.woting.cm.core.dict.mem._CacheDictionary _cd = ((CacheEle<_CacheDictionary>) SystemCache
				.getCache(WtContentMngConstants.CACHE_DICT)).getContent();
		// 字典--地区
		DictModel tempDictM = null;
		tempDictM = _cd.getDictModelById("2");
		TreeNode<DictDetail> tempNode = null;
		tempNode = (TreeNode<DictDetail>) tempDictM.dictTree.findNode(bcAreaId);
		if (tempNode == null) {
			tempDictM = _cd.getDictModelById("9");
			tempNode = (TreeNode<DictDetail>) tempDictM.dictTree.findNode(bcAreaId);
		}

		if (tempNode != null) {
			DictRefResPo drrPo = new DictRefResPo();
			drrPo.setId(SequenceUUID.getUUIDSubSegment(4));
			drrPo.setRefName("电台所属地区");
			drrPo.setResTableName("wt_Broadcast");
			drrPo.setResId(bPo.getId());
			drrPo.setDictMid(tempDictM.getId());
			drrPo.setDictDid(bcAreaId);
			dictRefResDao.insert(drrPo);
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
		bc_liveflowDao.delete("multiDelBc", "'" + bcId + "'");// 先删除
		LiveFlowPo lfp = new LiveFlowPo();
		lfp.setId(SequenceUUID.getUUIDSubSegment(4));
		lfp.setBcId(bPo.getId());
		lfp.setBcSrcType(2);
		lfp.setBcSource("管理端录入");
		lfp.setFlowURI(bcPlayPath);
		lfp.setIsMain(Integer.valueOf(isMain));
		bc_liveflowDao.insert(lfp);
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
		bPo.setBcUrl(m.get("bcUrl") + "");
		bPo.setDesc(m.get("descn") + "");
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
		bc_liveflowDao.delete("multiDelBc", "'" + id + "'");// 先删除
		String lfs = m.get("bcLiveFlows") + "";
		String[] fla = lfs.split(";;");
		boolean hasMain = false;
		for (int i = 0; i < fla.length; i++) {
			String[] _s = fla[i].split("::");
			LiveFlowPo lfp = new LiveFlowPo();
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
			bc_liveflowDao.insert(lfp);
		}
	}

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
			com.woting.cm.core.dict.mem._CacheDictionary _cd = ((CacheEle<_CacheDictionary>) SystemCache
					.getCache(WtContentMngConstants.CACHE_DICT)).getContent();
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
		bc_liveflowDao.delete("multiDelBc", ids);
		dictRefResDao.delete("multiDelBc", ids);
	}

	/**
	 * 
	 * @param bcId
	 * @return
	 */
	public List<Map<String, Object>> getBroadcastInfo(String bcId) {
		Map<String, Object> ret = new HashMap<String, Object>();
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

	// public List<Map<String, Object>> getSqlList(){
	// List<BroadcastPo> listbp = broadcastDao.queryForList();
	// for (BroadcastPo broadcastPo : listbp) {
	// LiveFlowPo liveFlowPo = bc_liveflowDao.getInfoObject("getInfoByBcId",
	// broadcastPo.getId());
	// if(liveFlowPo!=null){
	// Map<String, Object> m = new HashMap<String,Object>();
	// m.put("channelName", broadcastPo.getBcTitle());
	// List<Map<String, Object>> l = new ArrayList<Map<String,Object>>();
	// Map<String, Object> m2 = new HashMap<String,Object>();
	// m2.put("streamName", "蜻蜓资源");
	// }
	// }
	// return null;
	// }
}