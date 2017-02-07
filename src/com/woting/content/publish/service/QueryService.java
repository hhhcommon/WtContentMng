package com.woting.content.publish.service;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.springframework.stereotype.Service;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.StringUtils;
import com.woting.cm.core.broadcast.persis.po.BroadcastPo;
import com.woting.cm.core.channel.persis.po.ChannelAssetPo;
import com.woting.cm.core.channel.service.ChannelService;
import com.woting.cm.core.media.persis.po.MediaAssetPo;
import com.woting.cm.core.media.persis.po.SeqMaRefPo;
import com.woting.cm.core.media.persis.po.SeqMediaAssetPo;
import com.woting.cm.core.media.service.MediaService;
import com.woting.cm.core.person.service.PersonService;
import com.woting.cm.core.utils.ContentUtils;
import com.woting.content.broadcast.service.BroadcastProService;
import com.woting.content.manage.channel.service.ChannelContentService;
import com.woting.content.manage.keyword.service.KeyWordProService;
import com.woting.content.manage.media.service.MediaContentService;
import com.woting.content.publish.utils.CacheUtils;

@Service
public class QueryService {
	@Resource(name = "dataSource")
	private DataSource DataSource;
	@Resource
	private MediaService mediaService;
	@Resource
	private PersonService personService;
	@Resource
	private KeyWordProService keyWordProService;
	@Resource
	private ChannelContentService chaService;
	@Resource
	private ChannelService chService;
	@Resource
	private BroadcastProService broadcastProService;
	@Resource
	private MediaContentService mediaContentService;

	/**
	 * 查询列表
	 * 
	 * @param flowFlag
	 * @param page
	 * @param pagesize
	 * @param catalogsid
	 * @param source
	 * @param beginpubtime
	 * @param endpubtime
	 * @param beginctime
	 * @param endctime
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getContent(int flowFlag, int page, int pagesize, String channelId, String publisherId,
			Timestamp beginpubtime, Timestamp endpubtime, Timestamp beginctime, Timestamp endctime) {
		Map<String, Object> mapall = new HashMap<String, Object>();
		List<Map<String, Object>> list2seq = new ArrayList<Map<String, Object>>();
		long numall = 0;

//		Map<String, Object> m = new HashMap<String, Object>();
//		m.put("channelId", channelId);
//		m.put("publisherId", publisherId);
//		m.put("beginPubtime", beginpubtime);
//		m.put("endPubtime", endpubtime);
//		m.put("begincTime", beginctime);
//		m.put("endcTime", endctime);
//		m.put("flowFlag", flowFlag);
//		numall = mediaService.getCountInCha(m);
//
//		m.clear();
//		m.put("channelId", channelId);
//		m.put("publisherId", publisherId);
//		m.put("beginPubtime", beginpubtime);
//		m.put("endPubtime", endpubtime);
//		m.put("begincTime", beginctime);
//		m.put("endcTime", endctime);
//		m.put("flowFlag", flowFlag);
//		m.put("beginNum", (page-1) * pagesize);
//		m.put("size", pagesize);
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select count(*) from wt_ChannelAsset ch "
				+ "where ch.flowFlag = "+flowFlag;
		if (channelId!=null) {
			sql += " and ch.channelId = '"+channelId+"'";
		}
		if (publisherId!=null) {
			sql += " and ch.publisherId = '"+publisherId+"'";
		}
		try {
			conn = DataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				numall = rs.getLong(1);
			}
			
			rs.close(); rs=null;
            ps.close(); ps=null;
            
		} catch (Exception e) {
			e.printStackTrace();
		}
		sql = "SELECT ch.id,(CASE ch.assetType WHEN 'wt_MediaAsset' then ma.maPublisher when 'wt_SeqMediaAsset' then sma.smaPublisher end) publisher,"
				+ "(CASE ch.assetType WHEN 'wt_MediaAsset' then ma.descn when 'wt_SeqMediaAsset' then sma.descn end) descn,"
				+ "(CASE ch.assetType WHEN 'wt_MediaAsset' then ma.id when 'wt_SeqMediaAsset' then sma.id end) resId,"
				+ "(CASE ch.flowFlag WHEN 2 then ch.pubTime ELSE ch.cTime end) time,"
				+ "ch.assetId,c.channelName,ch.sort,ch.channelId,ch.flowFlag,ch.publisherId,ch.assetType,ch.pubName,ch.pubImg,per.id personId,per.pName "
				+ "from wt_ChannelAsset ch "
				+ "LEFT JOIN wt_Person_Ref perf "
				+ "ON ch.assetId = perf.resId and ch.assetType = perf.resTableName "
				+ "LEFT JOIN wt_Person per "
				+ "on perf.personId = per.Id "
				+ "LEFT JOIN wt_Channel c "
				+ "ON ch.channelId = c.id "
				+ "LEFT JOIN wt_MediaAsset ma "
				+ "ON ch.assetId = ma.id and ch.assetType = 'wt_MediaAsset' "
				+ "LEFT JOIN wt_SeqMediaAsset sma "
				+ "ON ch.assetId = sma.id and ch.assetType = 'wt_SeqMediaAsset' "
				+ "where ch.flowFlag = "+flowFlag;
		if (channelId!=null) {
			sql += " and ch.channelId = '"+channelId+"'";
		}
		if (publisherId!=null) {
			sql += " and ch.publisherId = '"+publisherId+"'";
		}
		sql += " ORDER BY ch.sort DESC, ch.pubTime DESC ,ch.cTime DESC LIMIT ";
		if (page>0 && pagesize>0) {
			sql += (page-1)*pagesize +","+pagesize;
		} else {
			sql += 0 +","+pagesize;
		}
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String ids = "";
			while (rs != null && rs.next()) {
				Map<String, Object> oneDate = new HashMap<String, Object>();
				oneDate.put("id", rs.getString("id"));
				oneDate.put("ChannelName", rs.getString("channelName"));
				oneDate.put("ChannelId", rs.getString("channelId"));
				oneDate.put("ContentName", rs.getString("pubName"));
				oneDate.put("ContentId", rs.getString("assetId"));
				oneDate.put("MediaType", rs.getString("assetType"));
				oneDate.put("ContentDesc", rs.getString("descn"));
				oneDate.put("ContentImg", rs.getString("pubImg"));
				oneDate.put("ContentFlowFlag", rs.getInt("flowFlag"));
				oneDate.put("ContentSort", rs.getInt("sort"));
				oneDate.put("ContentTime", rs.getTimestamp("time"));
				oneDate.put("PersonId", null);
				oneDate.put("PersonName", null);
				ids += " or persf.resId = '"+rs.getString("assetId")+"'";
				list2seq.add(oneDate);
			}
			ids = ids.substring(3);
			
			rs.close(); rs=null;
            ps.close(); ps=null;
            
			sql = "SELECT pers.id,pers.pName,persf.resId from wt_Person pers LEFT JOIN wt_Person_Ref persf ON pers.id = persf.personId "
					+ "where "+ids;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				for (Map<String, Object> map : list2seq) {
					if (map.get("ContentId").equals(rs.getString("resId"))) {
						map.put("PersonId", rs.getString("id"));
						map.put("PersonName", rs.getString("pName"));
					}
				}
			}
			
			rs.close(); rs=null;
            ps.close(); ps=null;
            
			sql = "SELECT kws.id,kws.kwName,kwsf.resId from wt_KeyWord kws LEFT JOIN wt_Kw_Res kwsf ON kws.id = kwsf.kwId "
					+ "where"+ids.replace("persf", "kwsf");
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				for (Map<String, Object> map : list2seq) {
					if (map.get("ContentId").equals(rs.getString("resId"))) {
						Map<String, Object> kwmap = new HashMap<>();
						kwmap.put("TagName", rs.getString("kwName"));
						kwmap.put("TagId", rs.getString("id"));
						if (map.containsKey("KeyWords")) {
							List<Map<String, Object>> kws = (List<Map<String, Object>>) map.get("KeyWords");
							kws.add(kwmap);
						} else {
							List<Map<String, Object>> kws = new ArrayList<>();
							kws.add(kwmap);
							map.put("KeyWords", kws);
						}
					}
				}
			}
			rs.close(); rs=null;
            ps.close(); ps=null;
			conn.close(); conn=null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
        }

		// 查询显示的节目名称，发布组织和描述信息
//		for (Map<String, Object> map : list2seq) {
//			if (map.get("MediaType").equals("wt_SeqMediaAsset")) {
//				try {
//				    List<Map<String, Object>> kwlist = keyWordProService.getKeyWordListByAssetId(map.get("ContentId")+"", "wt_SeqMediaAsset");
//				    map.put("KeyWords",kwlist);
//				} catch (Exception e) {
//					e.printStackTrace();
//					continue;
//				}
//			} else {
//				if (map.get("MediaType").equals("wt_MediaAsset")) {
//					try {
//					    List<Map<String, Object>> kwlist = keyWordProService.getKeyWordListByAssetId(map.get("ContentId")+"", "wt_MediaAsset");
//					    map.put("KeyWords",kwlist);
//					} catch (Exception e) {
//						e.printStackTrace();
//						continue;
//					}
//				}
//			}
//		}
		mapall.put("List", list2seq);
		mapall.put("Count", numall);
		return mapall;
	}

	/**
	 * 查询已显示的节目信息
	 * 
	 * @param pagesize
	 * @param page
	 * @param id
	 * @param acttype
	 * @return
	 */
	public Map<String, Object> getContentInfo(int pagesize, int page, String id, String acttype) {
		Map<String, Object> map = new HashMap<String, Object>();
		switch (acttype) {
		case "wt_SeqMediaAsset":
			map = getSeqInfo(pagesize, page, id, acttype);
			break;
		case "wt_MediaAsset":
			map = getAudioInfo(id, acttype);
			break;
		case "wt_Broadcast":
			map = getBroadcastInfo(id, acttype);
			break;
		default:
			break;
		}
		return map;
	}

	/**
	 * 查看专辑及级下节目的信息
	 * 
	 * @param pagesize
	 * @param page
	 * @param id
	 * @param acttype
	 * @return
	 */
	public Map<String, Object> getSeqInfo(int pagesize, int page, String id, String acttype) {
		List<Map<String, Object>> listaudio = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> seqData = new HashMap<String, Object>();// 存放专辑信息
		SeqMediaAssetPo sma = mediaService.getSmaInfoById(id);
		if (sma != null) {
			List<Map<String, Object>> catalist = mediaService.getResDictRefByResId("'" + sma.getId() + "'",
					"wt_SeqMediaAsset");
			List<Map<String, Object>> chlist = mediaService.getCHAByAssetId("'" + sma.getId() + "'",
					"wt_SeqMediaAsset");
			List<SeqMaRefPo> listseqmaref = mediaService.getSeqMaRefBySid(sma.getId());
			Map<String, Object> smap = sma.toHashMap();
			smap.put("count", listseqmaref.size());
			smap.put("smaPublishTime", chlist.get(0).get("pubTime"));
			seqData = ContentUtils.convert2Sma(smap, null, catalist, chlist, null);

			// 查询专辑和单体的联系
			List<String> listaudioid = new ArrayList<String>();

			for (SeqMaRefPo seqMaRefPo : listseqmaref) {
				listaudioid.add(seqMaRefPo.getMId());
			}

			// 查询单体信息
			for (String audid : listaudioid) {
				listaudio.add(getAudioInfo(audid, "wt_MediaAsset"));
			}

			if (listaudio.size() == 0) {
				map.put("audio", null);
			} else {
				map.put("audio", listaudio);
			}
			map.put("sequ", seqData);
			return map;
		}
		return null;
	}

	/**
	 * 查询单体信息
	 * 
	 * @param contentid
	 * @param acttype
	 * @return
	 */
	public Map<String, Object> getAudioInfo(String contentid, String acttype) {
		List<ChannelAssetPo> chas = mediaService.getCHAListByAssetId("'"+contentid+"'", acttype);
		if (chas != null && chas.size() > 0) {
			MediaAssetPo ma = mediaService.getMaInfoById(contentid);
			if (ma != null) {
				List<MediaAssetPo> mas = new ArrayList<>();
				mas.add(ma);
				List<Map<String, Object>> rem = mediaService.makeMaListToReturn(mas);
				if (rem != null && rem.size() > 0) {
					return rem.get(0);
				}
			}
		}
		return null;
	}

	/**
	 * 查询电台信息
	 * 
	 * @param contentid
	 * @param acttype
	 * @return
	 */
	public Map<String, Object> getBroadcastInfo(String contentid, String acttype) {
		Map<String, Object> broadcastData = new HashMap<String, Object>();// 单体信息
		BroadcastPo bc = broadcastProService.getBroadcastList(contentid);
		if (bc == null)
			return null;
		broadcastData.put("ContentId", bc.getId());
		broadcastData.put("ContentName", bc.getBcTitle());
		broadcastData.put("MediaType", bc.getBcTitle());
		broadcastData.put("ContentImg", bc.getBcImg());
		broadcastData.put("ContentCTime", bc.getcTime());
		broadcastData.put("ContentDesc", bc.getDescn());
		broadcastData.put("ContentSource", bc.getBcPublisher());
		broadcastData.put("ContentPersons", null);
		return broadcastData;
	}

	/**
	 * 修改显示节目排序号和审核状态
	 * 
	 * @param id
	 * @param number
	 * @param flowFlag
	 * @param OpeType
	 * @return
	 */
	public boolean modifyInfo(List<Map<String, Object>> contentIds, int number, String OpeType) {
		int flowFlag = 0;
		boolean isok = false;
		switch (OpeType) {
		case "sort":
			isok = modifySort(contentIds, number); // 修改排序号
			break;
		case "pass":
			flowFlag = 2;
			isok = modifyStatus(contentIds, flowFlag); // 修改审核状态为通过
			break;
		case "nopass":
			flowFlag = 3;
			isok = modifyStatus(contentIds, flowFlag); // 修改审核状态为未通过
			break;
		case "revoke":
			flowFlag = 4;
			isok = modifyStatus(contentIds, flowFlag); // 修改审核状态为撤回
			break;
		default:
			break;
		}
		return isok;
	}

	/**
	 * 修改审核状态
	 * 
	 * @param id
	 *            栏目发布表id
	 * @param number
	 * @return
	 */
	public boolean modifyStatus(List<Map<String, Object>> contentIds, int flowFlag) {
		String wheresql = "";
		String conid = "";
		for (Map<String, Object> map : contentIds) {
			String channelIds = map.get("ChannelIds")+"";
			String mediaType = "";
		    if (map.get("MediaType").equals("SEQU")) {
		    	mediaType = "wt_SeqMediaAsset";
			} else if (map.get("MediaType").equals("AUDIO")) 
				mediaType = "wt_MediaAsset";
			else mediaType = null;
			if (!StringUtils.isNullOrEmptyOrSpace(channelIds) && !channelIds.equals("null")) {
				String chaid = "";
				String[] chaIds = channelIds.split(",");
			    if (chaIds!=null && chaIds.length>0) {
				    for (String str : chaIds) {
					    chaid += " or channelId = '"+str+"'";
				    }
				    chaid = chaid.substring(3);
				    chaid = " (" + chaid + ")";
				    
				    conid += " or ( assetId = '" + map.get("Id") + "'";
				    if (mediaType!=null) {
						conid +=  " and assetType = '" + mediaType + "'";
					}
				    conid += " and "+ chaid +")";
			    } else {
			    	conid += " or ( assetId = '" + map.get("Id") + "'";
			    	if (mediaType!=null) {
						conid += " and assetType = '" + mediaType + "' )";
					} else conid += ")" ;
			    }
			} else {
				conid += " or ( assetId = '" + map.get("Id") + "'";
		    	if (mediaType!=null) {
					conid += " and assetType = '" + mediaType + "' )";
				} else conid += ")" ;
			}
		}
		if (conid.length()<3) {
			return false;
		}
		conid = conid.substring(3);
		conid = " (" + conid + ")";
		
		wheresql += " and" + conid;
		
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("isValidate", 1);
		if (wheresql.length()>3) {
			m.put("wheresql", wheresql);
		}
		List<ChannelAssetPo> chas = mediaService.getChaBy(m);
		if (chas!=null) {
			int num = 0;
			int chaSize = chas.size();
			for (ChannelAssetPo channelAssetPo : chas) {
				try {
					channelAssetPo.setFlowFlag(flowFlag);
				    if (flowFlag==2) {
					    channelAssetPo.setPubTime(new Timestamp(System.currentTimeMillis()));
				    }
				    mediaService.updateCha(channelAssetPo);
				    num++;
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				if (num==chaSize) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 修改排序号
	 * 
	 * @param id
	 * @param sort
	 * @return
	 */
	public boolean modifySort(List<Map<String, Object>> contentIds, int sort) {
		String wheresql = "";
		String conid = "";
		for (Map<String, Object> map : contentIds) {
			String channelIds = map.get("ChannelIds")+"";
			String mediaType = "";
		    if (map.get("MediaType").equals("SEQU")) {
		    	mediaType = "wt_SeqMediaAsset";
			} else if (map.get("MediaType").equals("AUDIO")) 
				mediaType = "wt_MediaAsset";
			else mediaType = null;
			if (!StringUtils.isNullOrEmptyOrSpace(channelIds) && !channelIds.equals("null")) {
				String chaid = "";
				String[] chaIds = channelIds.split(",");
			    if (chaIds!=null && chaIds.length>0) {
				    for (String str : chaIds) {
					    chaid += " or channelId = '"+str+"'";
				    }
				    chaid = chaid.substring(3);
				    chaid = " (" + chaid + ")";
				    
				    conid += " or ( assetId = '" + map.get("Id") + "'";
				    if (mediaType!=null) {
						conid +=  " and assetType = '" + mediaType + "'";
					}
				    conid += "and "+ chaid +")";
			    } else {
			    	conid += " or ( assetId = '" + map.get("Id") + "'";
			    	if (mediaType!=null) {
						conid += " and assetType = '" + mediaType + "' )";
					} else conid += ")" ;
			    }
			} else {
				conid += " or ( assetId = '" + map.get("Id") + "'";
		    	if (mediaType!=null) {
					conid += " and assetType = '" + mediaType + "' )";
				} else conid += ")" ;
			}
		}
		if (conid.length()<3) {
			return false;
		}
		conid = conid.substring(3);
		conid = " (" + conid + ")";
		
		wheresql += " and" + conid;
		
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("isValidate", 1);
		if (wheresql.length()>3) {
			m.put("wheresql", wheresql);
		}
		List<ChannelAssetPo> chas = mediaService.getChaBy(m);
		if (chas!=null) {
			int num = 0;
			int chaSize = chas.size();
			for (ChannelAssetPo channelAssetPo : chas) {
				try {
					channelAssetPo.setSort(sort);
				    mediaService.updateCha(channelAssetPo);
				    num++;
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
			if (num==chaSize) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获得分类和发布组织信息
	 * 
	 * @return
	 */
	public Map<String, Object> getConditionsInfo() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;//TODO
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> listcatalogs = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listorganize = new ArrayList<Map<String, Object>>();

		String sql = "select id,channelName from wt_Channel"; // 获得栏目分类信息
		try {
			conn = DataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				Map<String, Object> oneDate = new HashMap<String, Object>();
				oneDate.put("CatalogsId", rs.getString("id"));
				oneDate.put("CatalogsName", rs.getString("channelName"));
				listcatalogs.add(oneDate);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(conn, ps, rs);
		}

		sql = "select id,oName from wt_Organize"; // 获得发布组织信息
		try {
			conn = DataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				Map<String, Object> oneDate = new HashMap<>();
				oneDate.put("SourceId", rs.getString("id"));
				oneDate.put("SourceName", rs.getString("oName"));
				listorganize.add(oneDate);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(conn, ps, rs);
		}
		map.put("Catalogs", listcatalogs);
		map.put("Source", listorganize);
		return map;
	}

	public List<Map<String, Object>> getPublishedSeqList() {
		return chService.getSeqPublishedList();
	}

	/**
	 * 加载专辑id为zjId的专辑的下属的节目列表，注意是某一页page的列表
	 * 
	 * @param zjId 专辑Id
	 * @param page 第几页
	 * @return 返回该页的数据，以json串的方式，若该页无数据返回null
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getZJSubPage(String zjId, String page) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 1-根据zjId，计算出文件存放目录
		String path = SystemCache.getCache(FConstants.APPOSPATH).getContent() + "" + "mweb/zj/" + zjId + "/";
		// 2-判断是否有page所对应的数据
		File thisPage, nextPage;
		thisPage = new File(path + "P" + page + ".json");
		int nextpage = Integer.valueOf(page) + 1;
		nextPage = new File(path + "P" + nextpage + ".json");
		if (!thisPage.exists()) {
			map.put("ReturnType", "1011");
			map.put("Message", "没有相关内容 ");
		} else {// 组织本页数据
			String jsonstr = CacheUtils.readFile(path + "P" + page + ".json");
			List<Map<String, Object>> listaudios = (List<Map<String, Object>>) JsonUtils.jsonToObj(jsonstr, List.class);
			if (listaudios != null) {
				map.put("ResultList", listaudios);
				map.put("ReturnType", "1001");
				map.put("NextPage", String.valueOf(nextPage.exists()));// 判断是否有下一页，并组织到返回数据中
			} else {
				map.put("ReturnType", "1011");
				map.put("Message", "没有相关内容 ");
			}
		}
		return map;
	}

	/**
	 * 关闭数据库连接
	 * 
	 * @param conn
	 * @param ps
	 * @param rs
	 */
	private void closeConnection(Connection conn, PreparedStatement ps, ResultSet rs) {
		if (rs != null)
			try {
				rs.close();
				rs = null;
			} catch (Exception e) {
				rs = null;
			} finally {
				rs = null;
			}
		;
		if (ps != null)
			try {
				ps.close();
				ps = null;
			} catch (Exception e) {
				ps = null;
			} finally {
				ps = null;
			}
		;
		if (conn != null)
			try {
				conn.close();
				conn = null;
			} catch (Exception e) {
				conn = null;
			} finally {
				conn = null;
			}
		;
	}
}
