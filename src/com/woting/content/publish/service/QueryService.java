package com.woting.content.publish.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.StringUtils;
import com.woting.WtContentMngConstants;
import com.woting.cm.cachedb.cachedb.service.CacheDBService;
import com.woting.cm.cachedb.playcountdb.persis.po.PlayCountDBPo;
import com.woting.cm.cachedb.playcountdb.service.PlayCountDBService;
import com.woting.cm.core.broadcast.persis.po.BroadcastPo;
import com.woting.cm.core.channel.mem._CacheChannel;
import com.woting.cm.core.channel.model.Channel;
import com.woting.cm.core.channel.persis.po.ChannelAssetPo;
import com.woting.cm.core.channel.persis.po.ChannelAssetProgressPo;
import com.woting.cm.core.channel.service.ChannelAssetProgressService;
import com.woting.cm.core.channel.service.ChannelLoopImgService;
import com.woting.cm.core.channel.service.ChannelService;
import com.woting.cm.core.media.MediaType;
import com.woting.cm.core.media.persis.po.MediaAssetPo;
import com.woting.cm.core.media.persis.po.SeqMaRefPo;
import com.woting.cm.core.media.persis.po.SeqMediaAssetPo;
import com.woting.cm.core.media.service.MediaService;
import com.woting.cm.core.oss.utils.OssUtils;
import com.woting.cm.core.person.service.PersonService;
import com.woting.cm.core.subscribe.SubscribeThread;
import com.woting.cm.core.utils.ContentUtils;
import com.woting.content.broadcast.service.BroadcastProService;
import com.woting.content.manage.channel.service.ChannelContentService;
import com.woting.content.manage.dict.service.DictContentService;
import com.woting.content.manage.keyword.service.KeyWordProService;
import com.woting.content.manage.media.service.MediaContentService;

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
    private ChannelLoopImgService clmService;
	@Resource
	private BroadcastProService broadcastProService;
	@Resource
	private MediaContentService mediaContentService;
	@Resource
	private ChannelAssetProgressService channelAssetProgressService;
	@Resource
	private DictContentService dictContentService;
	@Resource
	private CacheDBService cacheDBService;
	@Resource
	private PlayCountDBService playCountDBService;

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
	public Map<String, Object> getContent(String flowFlag, int page, int pagesize, String mediaType, String channelId, String publisherId,
			Timestamp beginpubtime, Timestamp endpubtime, Timestamp beginctime, Timestamp endctime) {
		Map<String, Object> mapall = new HashMap<String, Object>();
		List<Map<String, Object>> list2seq = new ArrayList<Map<String, Object>>();
		long numall = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT COUNT(*) FROM(select DISTINCT assetId from wt_ChannelAsset"
				+ " where ";
		if (mediaType!=null) {
			if (mediaType.equals("SEQU")) sql += " assetType = 'wt_SeqMediaAsset'";
			else if(mediaType.equals("AUDIO")) sql += " assetType = 'wt_MediaAsset'";
		} else {
			sql += " (assetType = 'wt_MediaAsset' or assetType = 'wt_SeqMediaAsset')";
		}
		if (flowFlag!=null) {
			sql += " and flowFlag = "+flowFlag;
		}
		if (channelId!=null) {
			sql += " and channelId = '"+channelId+"'";
		}
		if (publisherId!=null) {
			sql += " and publisherId = '"+publisherId+"'";
		}
		if (beginpubtime!=null) {
			sql += " and pubTime >= '"+beginpubtime+"'";
		}
		if (endpubtime!=null) {
			sql += " and pubTime <= '"+endpubtime+"'";
		}
		if (beginctime!=null) {
			sql += " and cTime >= '"+beginctime+"'";
		}
		if (endctime!=null) {
			sql += " and cTime <= '"+endctime+"'";
		}
		sql += " ) ch";
		try {
			conn = DataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				numall = rs.getLong(1);
			}
			if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
		} catch (Exception e) {
			e.printStackTrace();
		}
		sql = "SELECT DISTINCT ch.id,(CASE ch.assetType WHEN 'wt_MediaAsset' then ma.maPublisher when 'wt_SeqMediaAsset' then sma.smaPublisher end) publisher,"
				+ "(CASE ch.assetType WHEN 'wt_MediaAsset' then ma.descn when 'wt_SeqMediaAsset' then sma.descn end) descn,"
				+ "(CASE ch.assetType WHEN 'wt_MediaAsset' then ma.id when 'wt_SeqMediaAsset' then sma.id end) resId,"
				+ "(CASE ch.flowFlag WHEN 2 then ch.pubTime ELSE ch.cTime end) time,"
				+ " ch.assetId,ch.channelName,ch.sort,ch.channelId,ch.flowFlag,ch.publisherId,ch.assetType,ch.pubName,ch.pubImg"
				+ " from ("
				+ " SELECT ch.*,c.channelName"
				+ " from wt_ChannelAsset ch"
				+ " LEFT JOIN wt_Channel c"
				+ " ON ch.channelId = c.id"
				+ " where (";
		if (mediaType!=null) {
			if (mediaType.equals("SEQU")) sql += " ch.assetType = 'wt_SeqMediaAsset' )";
			else if(mediaType.equals("AUDIO")) sql += " ch.assetType = 'wt_MediaAsset' )";
		} else sql += " ch.assetType = 'wt_MediaAsset' or ch.assetType = 'wt_SeqMediaAsset' ) ";
		if (flowFlag!=null) {
			sql += " and ch.flowFlag = "+flowFlag;
		}
		if (channelId!=null) {
			sql += " and ch.channelId = '"+channelId+"'";
		}
		if (publisherId!=null) {
			sql += " and ch.publisherId = '"+publisherId+"'";
		}
		if (beginpubtime!=null) {
			sql += " and ch.pubTime >= '"+beginpubtime+"'";
		}
		if (endpubtime!=null) {
			sql += " and ch.pubTime <= '"+endpubtime+"'";
		}
		if (beginctime!=null) {
			sql += " and ch.cTime >= '"+beginctime+"'";
		}
		if (endctime!=null) {
			sql += " and ch.cTime <= '"+endctime+"'";
		}
		sql += " ORDER BY ch.sort DESC, ch.pubTime DESC LIMIT ";
		if (page>0 && pagesize>0) {
			sql += (page-1)*pagesize +","+pagesize;
		} else {
			sql += 0 +","+pagesize;
		}
		sql += " ) ch ";
		sql += " LEFT JOIN wt_MediaAsset ma ON (ch.assetId=ma.id and ch.assetType = 'wt_MediaAsset')"
				+ " LEFT JOIN wt_SeqMediaAsset sma ON (ch.assetId = sma.id and ch.assetType = 'wt_SeqMediaAsset')";
		sql += " GROUP BY ch.assetId,ch.assetType";
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String ids = "";
			String maids = "";
			String smaids = "";
			while (rs != null && rs.next()) {
				Map<String, Object> oneDate = new HashMap<String, Object>();
				oneDate.put("id", rs.getString("id"));
				oneDate.put("ChannelName", rs.getString("channelName"));
				oneDate.put("ChannelId", rs.getString("channelId"));
				oneDate.put("ContentName", rs.getString("pubName"));
				oneDate.put("ContentId", rs.getString("assetId"));
				oneDate.put("ContentPublisher", rs.getString("publisher"));
				oneDate.put("MediaType", rs.getString("assetType"));
				oneDate.put("ContentDesc", rs.getString("descn"));
				oneDate.put("ContentImg", rs.getString("pubImg"));
				oneDate.put("ContentFlowFlag", rs.getInt("flowFlag"));
				oneDate.put("ContentSort", rs.getInt("sort"));
				oneDate.put("ContentTime", rs.getTimestamp("time"));
				oneDate.put("MediaSize", 1);
				ids += " or persf.resId = '"+rs.getString("assetId")+"'";
				if (rs.getString("assetType").equals("wt_MediaAsset")) {
					maids += " or mId = '"+rs.getString("assetId")+"'";
				}
				if (rs.getString("assetType").equals("wt_SeqMediaAsset")) {
					smaids += " or sId = '"+rs.getString("assetId")+"'";
				}
				list2seq.add(oneDate);
			}
			
			if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
            
            if (maids.length()>0) {
				maids = maids.substring(3);
				sql = "SELECT s.*,mas.playURI FROM "
						+ "(SELECT sma.id,sma.smaTitle,smaf.mId from wt_SeqMediaAsset sma,wt_SeqMA_Ref smaf where sma.id = smaf.sId and ("+maids+")) s"
						+ " LEFT JOIN wt_MaSource mas"
						+ " ON s.mId = mas.maId and mas.isMain = 1";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					for (Map<String, Object> map : list2seq) {
						if (map.get("MediaType").equals("wt_MediaAsset") && map.get("ContentId").equals(rs.getString("mId"))) {
							map.put("ContentPlayUrl", rs.getString("playURI"));
							map.put("ContentSeqId", rs.getString("id"));
							map.put("ContentSeqName", rs.getString("smaTitle"));
							
						}
					}
				}
				if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
			}
            if (smaids.length()>0) { //查询专辑下级节目总数
				smaids = smaids.substring(3);
				sql = "SELECT sId,COUNT(*) num from wt_SeqMA_Ref "
						+ "where "+smaids
						+ " GROUP BY sId";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					for (Map<String, Object> map : list2seq) {
						if (map.get("MediaType").equals("wt_SeqMediaAsset") && map.get("ContentId").equals(rs.getString("sId"))) {
							map.put("MediaSize", rs.getString("num"));
						}
					}
				}
				if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
			}
			
			if (ids.length()>3) {
				ids = ids.substring(3);
				sql = "SELECT pers.id,pers.pName,persf.resId from wt_Person pers LEFT JOIN wt_Person_Ref persf ON pers.id = persf.personId "
						+ " where "+ids;
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					for (Map<String, Object> map : list2seq) {
						if (map.get("ContentId").equals(rs.getString("resId"))) {
							Map<String, Object> permap = new HashMap<>();
							permap.put("PerId", rs.getString("id"));
							permap.put("PerName", rs.getString("pName"));
							permap.put("RefName", "主播");
							if (map.containsKey("ContentPersons")) {
								List<Map<String, Object>> pers = (List<Map<String, Object>>) map.get("ContentPersons");
								pers.add(permap);
							} else {
								List<Map<String, Object>> pers = new ArrayList<>();
								pers.add(permap);
								map.put("ContentPersons", pers);
							}
						}
					}
				}
				
				if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
	            
				sql = "SELECT kws.id,kws.kwName,kwsf.resId from wt_KeyWord kws LEFT JOIN wt_Kw_Res kwsf ON kws.id = kwsf.kwId "
						+ "where "+ids.replace("persf", "kwsf")
						+ " order by kwsf.cTime ASC";
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
				
				if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
	            
				sql = "SELECT ch.id,cha.assetId,cha.pubName,ch.channelName,cha.flowFlag,cha.pubTime from wt_ChannelAsset cha LEFT JOIN wt_Channel ch ON cha.channelId = ch.id "
						+ " where "+ids.replace("persf.resId", "cha.assetId")
						+ " ORDER BY cha.pubTime asc";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					for (Map<String, Object> map : list2seq) {
						if (map.get("ContentId").equals(rs.getString("assetId"))) {
							Map<String, Object> chamap = new HashMap<>();
							chamap.put("FlowFlag", rs.getInt("flowFlag"));
							chamap.put("ChannelName", rs.getString("channelName"));
							chamap.put("ChannelId", rs.getString("id"));
							chamap.put("PubTime", rs.getTimestamp("pubTime"));
							if (map.containsKey("ContentPubChannels")) {
								List<Map<String, Object>> chas = (List<Map<String, Object>>) map.get("ContentPubChannels");
								chas.add(chamap);
							} else {
								List<Map<String, Object>> chas = new ArrayList<>();
								chas.add(chamap);
								map.put("ContentPubChannels", chas);
							}
						}
					}
				}
				if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
	            
	            sql = "SELECT resd.resId,resd.dictMid,resd.dictDid,dd.ddName,resd.refName from wt_ResDict_Ref resd , plat_DictD dd where resd.dictDid = dd.id and resd.dictMid = '3' "
						+ " and ("+ids.replace("persf.resId", "resd.resId")
						+ ") ORDER BY resd.cTime asc";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					for (Map<String, Object> map : list2seq) {
						if (map.get("ContentId").equals(rs.getString("resId"))) {
							Map<String, Object> catamap = new HashMap<>();
							catamap.put("CataDid", rs.getString("dictDid"));
							catamap.put("CataMName", rs.getString("refName"));
							catamap.put("CataTitle", rs.getString("ddName"));
							catamap.put("CataMid", rs.getString("dictMid"));
							if (map.containsKey("ContentCatalogs")) {
								List<Map<String, Object>> catas = (List<Map<String, Object>>) map.get("ContentCatalogs");
								catas.add(catamap);
							} else {
								List<Map<String, Object>> catas = new ArrayList<>();
								catas.add(catamap);
								map.put("ContentCatalogs", catas);
							}
						}
					}
				}
				if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
			}
			conn.close(); conn=null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
        }
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
			List<Map<String, Object>> catalist = mediaService.getResDictRefByResId("'" + sma.getId() + "'", "wt_SeqMediaAsset");
			List<Map<String, Object>> chlist = mediaService.getCHAByAssetId("'" + sma.getId() + "'", "wt_SeqMediaAsset");
			List<SeqMaRefPo> listseqmaref = mediaService.getSeqMaRefBySid(sma.getId(),page,pagesize);
			List<Map<String, Object>> pmaps = personService.getPersonByPId(id, acttype);
			Map<String, Object> smap = sma.toHashMap();
			smap.put("count", listseqmaref.size());
			smap.put("smaPublishTime", chlist.get(0).get("pubTime"));
			seqData = ContentUtils.convert2Sma(smap, pmaps, catalist, chlist, null);

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
	 * @param reDescn 
	 * @return
	 */
	public boolean modifyInfo(List<Map<String, Object>> contentIds, int number, String OpeType, String reDescn) {
		int flowFlag = 0;
		boolean isok = false;
		switch (OpeType) {
		case "sort":
			isok = modifySort(contentIds, number); // 修改排序号
			break;
		case "pass":
			flowFlag = 2;
			isok = modifyStatus(contentIds, flowFlag, reDescn); // 修改审核状态为通过
			//订阅推送
			for (Map<String, Object> map : contentIds) {
				if (map.get("MediaType").equals("AUDIO")) {
					new SubscribeThread(map.get("Id")+"").start();
				}
			}
			break;
		case "nopass":
			flowFlag = 3;
			isok = modifyStatus(contentIds, flowFlag, reDescn); // 修改审核状态为未通过
			break;
		case "revoke":
			flowFlag = 4;
			isok = modifyStatus(contentIds, flowFlag, reDescn); // 修改审核状态为撤回
			break;
		case "revocation": //TODO
			isok = modifyRevocation(contentIds, 1, reDescn); // 通过撤回
			break;
		case "norevocation": //TODO
			isok = modifyRevocation(contentIds, 2, reDescn); // 不通过撤回
			break;
		default:
			break;
		}
		return isok;
	}

	/**
	 * 修改审核状态
	 * @param reDescn 
	 * 
	 * @param id
	 *            栏目发布表id
	 * @param number
	 * @return
	 */
	public boolean modifyStatus(List<Map<String, Object>> contentIds, int flowFlag, String reDescn) {
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
				    if (flowFlag==2) channelAssetPo.setPubTime(new Timestamp(System.currentTimeMillis()));
				    mediaService.updateCha(channelAssetPo);
				    // 修改栏目发布状态
				    if (flowFlag==2) updateChannelAssetProgress(channelAssetPo.getId(), null, 1, reDescn);
				    else if(flowFlag==3) updateChannelAssetProgress(channelAssetPo.getId(), null, 2, reDescn);
				    else if(flowFlag==4) updateChannelAssetProgress(channelAssetPo.getId(), null, 1, reDescn);
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
		ResultSet rs = null;
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
		String path = "shareH5/mweb/zj/" + zjId + "/";
		// 2-判断是否有page所对应的数据
//		File thisPage, nextPage;
//		thisPage = new File(path + "P" + page + ".json");
		int nextpage = Integer.valueOf(page) + 1;
//		nextPage = new File(path + "P" + nextpage + ".json");
		if (!OssUtils.exists(path + "P" + page + ".json")) {
			map.put("ReturnType", "1011");
			map.put("Message", "没有相关内容 ");
		} else {// 组织本页数据
			String jsonstr = OssUtils.getObjectToString(path + "P" + page + ".json");//CacheUtils.readFile(path + "P" + page + ".json");
			List<Map<String, Object>> listaudios = (List<Map<String, Object>>) JsonUtils.jsonToObj(jsonstr, List.class);
			if (listaudios != null) {
				map.put("ResultList", listaudios);
				map.put("ReturnType", "1001");
				map.put("NextPage", OssUtils.exists(path + "P" + nextpage + ".json"));// 判断是否有下一页，并组织到返回数据中
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

	@SuppressWarnings("unchecked")
	public Map<String, Object> getSearchContentList(String searchWord, String flowFlag, int page, int pageSize,
			String mediaType, String channelId, String publisherId, Timestamp begincontentpubtime,
			Timestamp endcontentpubtime, Timestamp begincontentctime, Timestamp endcontentctime) {
		Map<String, Object> mapall = new HashMap<>();
		List<Map<String, Object>> ls = new ArrayList<>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		long numall = 0;
		try {
			conn = DataSource.getConnection();
			sql = "SELECT ress.* FROM(SELECT res.* from (";
			if (mediaType == null || mediaType.equals("AUDIO")) {
				sql += " (SELECT cha.assetId,cha.assetType type,cha.pubTime,CASE WHEN ma.maTitle LIKE '%"+searchWord+"%' THEN '1' ELSE '4' END sort "
					+ " FROM wt_MediaAsset ma,wt_ChannelAsset cha where (ma.maTitle like '%"+searchWord+"%' OR ma.descn LIKE '%"+searchWord+"%') #FlowFlagCase#  #ChannelIdCase# #PublisherId# and cha.assetId = ma.id #BeginPubTimeCase# #EndPubTime#)"
					+ " UNION ALL";
			}
			if (mediaType ==null  || mediaType.equals("SEQU")) {
				sql += " (SELECT cha.assetId,cha.assetType type,cha.pubTime,CASE WHEN sma.smaTitle LIKE '%"+searchWord+"%' THEN '1' ELSE '4' END sort "
					+ " FROM wt_SeqMediaAsset sma,wt_ChannelAsset cha where (sma.smaTitle like '%"+searchWord+"%' OR sma.descn LIKE '%"+searchWord+"%') #FlowFlagCase# #ChannelIdCase# #PublisherId# and cha.assetId = sma.id #BeginPubTimeCase# #EndPubTime#)"
					+ " UNION ALL";
			}
			sql +=  " (SELECT cha.assetId,cha.assetType type,cha.pubTime,'2' sort from wt_Person pers,wt_Person_Ref persf,wt_ChannelAsset cha"
					+ " WHERE pers.pName LIKE '%"+searchWord+"%' and persf.personId = pers.id AND cha.assetType = persf.resTableName AND cha.assetId = persf.resId #FlowFlagCase# #ChannelIdCase# #PublisherId# #MediaType# #BeginPubTimeCase# #EndPubTime#)"
					+ " UNION ALL"
					+ " (SELECT cha.assetId,cha.assetType type,cha.pubTime,'3' sort from wt_KeyWord kws,wt_Kw_Res kwsf,wt_ChannelAsset cha"
					+ " where kws.kwName LIKE '%"+searchWord+"%' and kwsf.kwId = kws.id and cha.assetType = kwsf.resTableName and kwsf.resId = cha.assetId #FlowFlagCase# #ChannelIdCase# #PublisherId# #MediaType# #BeginPubTimeCase# #EndPubTime#)) res"
					+ " ORDER BY res.sort ,res.pubTime DESC) ress"
					+ " GROUP BY ress.assetId ORDER BY ress.sort ,ress.pubTime DESC";
			if (flowFlag!=null) sql = sql.replace("#FlowFlagCase#", " and cha.flowFlag = "+flowFlag);
			else sql = sql.replace("#FlowFlagCase#", "");
			if (channelId!=null) sql = sql.replace("#ChannelIdCase#", " and cha.channelId = '"+channelId+"'");
			else sql = sql.replace("#ChannelIdCase#", "");
			if (publisherId!=null) sql = sql.replace("#PublisherId#", " and cha.publisherId = '"+publisherId+"'");
			else sql = sql.replace("#PublisherId#", "");
			if (begincontentpubtime!=null) sql = sql.replace("#BeginPubTimeCase#", " and cha.pubTime > '"+begincontentctime+"'");
			else sql = sql.replace("#BeginPubTimeCase#", "");
			if (endcontentpubtime!=null) sql = sql.replace("#EndPubTime#", " and cha.pubTime < '"+endcontentpubtime+"'");
			else sql = sql.replace("#EndPubTime#", "");
			if (mediaType==null) sql = sql.replace("#MediaType#", "");
			else if(mediaType.equals("AUDIO")) sql = sql.replace("#MediaType#", " and cha.assetType='wt_MediaAsset'");
			else if(mediaType.equals("SEQU")) sql = sql.replace("#MediaType#", " and cha.assetType='wt_SeqMediaAsset'");
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String maIds = "";
			String smaIds = "";
			String ids = "";
			while (rs != null && rs.next()) {
				Map<String, Object> m = new HashMap<>();
				m.put("ContentId", rs.getString("assetId"));
				m.put("MediaType", rs.getString("type"));
				m.put("ContentPubTime", rs.getTimestamp("pubTime"));
				ls.add(m);
			}
			if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
            numall = ls.size();
			if ((page-1)*pageSize>=ls.size()) return null;
			else ls = ls.subList((page-1)*pageSize, page*pageSize>ls.size()?ls.size():page*pageSize);
			if (ls.size()>0) {
				for (Map<String, Object> map : ls) {
					if (map.get("MediaType").equals("wt_MediaAsset")) {
						maIds += " or mId = '"+map.get("ContentId")+"'";
					}
					if (map.get("MediaType").equals("wt_SeqMediaAsset")) {
						smaIds += " or smaf.sId = '"+map.get("ContentId")+"'";
					}
					ids += " or perf.resId = '"+map.get("ContentId")+"'";
				}
			}
			if (maIds.length()>0) {
				maIds = maIds.substring(3);
				sql = "SELECT s.*,mas.playURI FROM "
						+ "(SELECT sma.id,sma.smaTitle,smaf.mId,ma.maTitle,ma.maPublisher,ma.maImg,ma.descn,ma.timeLong,ma.cTime from wt_SeqMediaAsset sma,wt_SeqMA_Ref smaf,wt_MediaAsset ma where ma.id = smaf.mId and sma.id = smaf.sId and ("+maIds+")) s"
						+ " LEFT JOIN wt_MaSource mas"
						+ " ON s.mId = mas.maId and mas.isMain = 1";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					for (Map<String, Object> map : ls) {
						if (map.get("MediaType").equals("wt_MediaAsset") && map.get("ContentId").equals(rs.getString("mId"))) {
							map.put("ContentPlayUrl", rs.getString("playURI"));
							map.put("ContentSeqId", rs.getString("id"));
							map.put("ContentSeqName", rs.getString("smaTitle"));
							map.put("ContentName", rs.getString("maTitle"));
							map.put("ContentPublisher", rs.getString("maPublisher"));
							map.put("ContentTimes", rs.getLong("timeLong"));
							map.put("ContentDesc", rs.getString("descn"));
							map.put("ContentImg", rs.getString("maImg"));
						}
					}
				}
				if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
			}
            if (smaIds.length()>0) { //查询专辑下级节目总数
				smaIds = smaIds.substring(3);
				sql = "SELECT sma.id,sma.smaTitle,sma.smaPublisher,sma.smaImg,sma.descn,sma.cTime,COUNT(*) num from wt_SeqMA_Ref smaf,wt_SeqMediaAsset sma "
						+ " where sma.id = smaf.sId and ("+smaIds
						+ ") GROUP BY sma.id";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					for (Map<String, Object> map : ls) {
						if (map.get("MediaType").equals("wt_SeqMediaAsset") && map.get("ContentId").equals(rs.getString("id"))) {
							map.put("MediaSize", rs.getString("num"));
							map.put("ContentName", rs.getString("smaTitle"));
							map.put("ContentImg", rs.getString("smaImg"));
							map.put("ContentDesc", rs.getString("descn"));
							map.put("ContentPublisher", rs.getString("smaPublisher"));
							map.put("ContentSubCount", rs.getLong("num"));
							map.put("CTime", rs.getTimestamp("cTime"));
							map.put("PlayCount", "1234");
						}
					}
				}
				if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
			}
            if (ids.length()>3) {
				ids = ids.substring(3);
				List<Map<String, Object>> perls = personService.getPersonsByResIdsAndResTableName(ids, mediaType.equals("SEQU")?"wt_SeqMediaAsset":"wt_MediaAsset");
				if (perls!=null) {
					for (Map<String, Object> m1 : ls) {
						for (Map<String, Object> m2 : perls) {
							if (m1.get("ContentId").equals(m2.get("resId"))) {
								Map<String, Object> df = new HashMap<>();
								df.put("RefName", "主播");
								df.put("PerId", m2.get("resId"));
								df.put("PerName", m2.get("pName"));
								if (m1.containsKey("ContentPersons")) {
									List<Map<String, Object>> dfls = (List<Map<String, Object>>) m1.get("ContentPersons");
									dfls.add(df);
								} else {
									List<Map<String, Object>> dfls = new ArrayList<>();
									dfls.add(df);
									m1.put("ContentPersons", dfls);
								}
							}
						}
					}
				}
	            
				List<Map<String, Object>> kws = keyWordProService.getKeyWordsByIds(ids.replace("perf", "kws"), mediaType.equals("SEQU")?"wt_SeqMediaAsset":"wt_MediaAsset");
				if (kws!=null) {
					for (Map<String, Object> m1 : ls) {
						for (Map<String, Object> m2 : kws) {
							if (m1.get("ContentId").equals(m2.get("resId"))) {
								Map<String, Object> kw = new HashMap<>();
								kw.put("KwName", m2.get("kwName"));
								kw.put("CTime", m2.get("cTime"));
								if (m1.containsKey("ContentKeyWords")) {
									List<Map<String, Object>> kwls = (List<Map<String, Object>>) m1.get("ContentKeyWords");
									kwls.add(kw);
								} else {
									List<Map<String, Object>> kwls = new ArrayList<>();
									kwls.add(kw);
									m1.put("ContentKeyWords", kwls);
								}
							}
						}
					}
				}
	            
				sql = "SELECT ch.id,cha.assetId,cha.pubName,ch.channelName,cha.flowFlag,cha.pubTime from wt_ChannelAsset cha LEFT JOIN wt_Channel ch ON cha.channelId = ch.id "
						+ " where "+ids.replace("perf.resId", "cha.assetId")
						+ " ORDER BY cha.pubTime asc";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					for (Map<String, Object> map : ls) {
						if (map.get("ContentId").equals(rs.getString("assetId"))) {
							Map<String, Object> chamap = new HashMap<>();
							chamap.put("FlowFlag", rs.getInt("flowFlag"));
							chamap.put("ChannelName", rs.getString("channelName"));
							chamap.put("ChannelId", rs.getString("id"));
							chamap.put("PubTime", rs.getTimestamp("pubTime"));
							if (map.containsKey("ContentPubChannels")) {
								List<Map<String, Object>> chas = (List<Map<String, Object>>) map.get("ContentPubChannels");
								chas.add(chamap);
							} else {
								List<Map<String, Object>> chas = new ArrayList<>();
								chas.add(chamap);
								map.put("ContentPubChannels", chas);
							}
						}
					}
				}
				if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
	            
	            List<Map<String, Object>> dictrefs = dictContentService.getDictRefListByIdsAndMeidaType(ids.replace("perf.resId", "resd.resId"), mediaType.equals("SEQU")?"wt_SeqMediaAsset":"wt_MediaAsset");
				if (dictrefs!=null) {
					for (Map<String, Object> m1 : ls) {
						for (Map<String, Object> m2 : dictrefs) {
							if (m1.get("ContentId").equals(m2.get("resId"))) {
								Map<String, Object> df = new HashMap<>();
								df.put("CataDid", m2.get("dictDid"));
								df.put("CataMName", "内容分类");
								df.put("CataTitle", m2.get("ddName"));
								df.put("CataMId", m2.get("dictMid"));
								if (m1.containsKey("ContentCatalogs")) {
									List<Map<String, Object>> dfls = (List<Map<String, Object>>) m1.get("ContentCatalogs");
									dfls.add(df);
								} else {
									List<Map<String, Object>> dfls = new ArrayList<>();
									dfls.add(df);
									m1.put("ContentCatalogs", dfls);
								}
							}
						}
					}
				}
			}
			conn.close(); conn=null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
        }
		mapall.put("List", ls);
		mapall.put("Count", numall);
		return mapall;
	}
	
	private void updateChannelAssetProgress(String channelAssetId, String checkId, int reFlowFlag, String reDescn) {
		Map<String, Object> m = new HashMap<>();
		m.put("chaId", channelAssetId);
		m.put("reFloeFlag", 0);
		ChannelAssetProgressPo chap = channelAssetProgressService.getChannelAssetProgress(m);
		if (chap!=null) {
			chap.setCheckerId(null);
			chap.setReFlowFlag(reFlowFlag);
			chap.setReDescn(reDescn);
			chap.setModifyTime(new Timestamp(System.currentTimeMillis()));
			channelAssetProgressService.updateChannelAssetProgress(chap);
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getAppRevocationList(int applyFlowFlag, int reFlowFlag, int page, int pageSize,
			String mediaType, String channelId, String publisherId, Timestamp begincontentpubtime,
			Timestamp endcontentpubtime, Timestamp begincontentctime, Timestamp endcontentctime) {
		String mediastr = " (cha.assetType = 'wt_SeqMediaAsset' or cha.assetType = 'wt_MediaAsset') ";
		if (mediaType!=null && mediaType.equals("SEQU")) mediastr = " cha.assetType = 'wt_SeqMediaAsset'"; 
		else if (mediaType!=null && mediaType.equals("AUDIO")) mediastr = " cha.assetType = 'wt_MediaAsset'";
		Map<String, Object> retM = new HashMap<>();
		long nums = channelAssetProgressService.getChannelAssetProgresByCount(channelId, publisherId, mediastr, applyFlowFlag, reFlowFlag);
		retM.put("AllCount", nums);
		List<Map<String, Object>> ls = channelAssetProgressService.getChannelAssetProgresBy(channelId, publisherId, mediastr, applyFlowFlag, reFlowFlag, page, pageSize);
		if (ls!=null) {
			List<Map<String, Object>> retLs = new ArrayList<>();
			Map<String, Object> channelMap = new HashMap<>();
			String assetIds = "";			
			for (Map<String, Object> m : ls) {
				Map<String, Object> chm = new HashMap<>();
				chm.put("FlowFlage", m.get("flowFlag"));
				chm.put("ChannelName",m.get("channelName"));
				chm.put("PubTime", m.get("pubTime"));
				chm.put("ChannelId", m.get("channelId"));
				String assetId = m.get("assetId")+"";
				if (!assetIds.contains(assetId)) {
					assetIds += " or id = '"+assetId+"'";
					Map<String, Object> reM = new HashMap<>();
					reM.put("ContentId", assetId);
					reM.put("ContentPubTime", m.get("pubTime"));
					retLs.add(reM);
				}
				if (channelMap.containsKey(assetId)) {
					List<Map<String, Object>> chal = (List<Map<String, Object>>) channelMap.get(assetId);
					chal.add(chm);
				} else {
					List<Map<String, Object>> chal = new ArrayList<>();
					chal.add(chm);
					channelMap.put(assetId, chal);
				}
			}
			if (mediaType.equals("AUDIO")) {
				List<Map<String, Object>> mas = mediaService.getMaInfosByIds(assetIds.substring(3).replace("id", "ma.id"));
				if (mas!=null) {
					for (Map<String, Object> m1 : retLs) {
						for (Map<String, Object> m2 : mas) {
							if (m1.get("ContentId").equals(m2.get("id"))) {
								m1.put("ContentName", m2.get("maTitle"));
								m1.put("ContentImg", m2.get("maImg"));
								m1.put("ContentPublisher", m2.get("maPublisher"));
								m1.put("ContentPlayURI", m2.get("playURI"));
								m1.put("ContentPlayCount", m2.get("playCount"));
								m1.put("ContentDesc", m2.get("descn"));
								m1.put("CTime", m2.get("cTime"));
								m1.put("MediaType", "wt_MediaAsset");
							}
						}
					}
				}
			} else if (mediaType.equals("SEQU")) {
				List<Map<String, Object>> smas = mediaService.getSmaInfosByIds(assetIds.substring(3).replace("id", "sma.id"));
				if (smas!=null) {
					for (Map<String, Object> m1 : retLs) {
						for (Map<String, Object> m2 : smas) {
							if (m1.get("ContentId").equals(m2.get("id"))) {
								m1.put("ContentName", m2.get("smaTitle"));
								m1.put("ContentImg", m2.get("smaImg"));
								m1.put("ContentPublisher", m2.get("smaPublisher"));
								m1.put("MediaSize", m2.get("num"));
								m1.put("ContentPlayCount", m2.get("playCount"));
								m1.put("ContentDesc", m2.get("descn"));
								m1.put("CTime", m2.get("cTime"));
								m1.put("MediaType", "wt_SeqMediaAsset");
							}
						}
					}
				}
			}
			for (Map<String, Object> map : retLs) {
				map.put("ContentPubChannels", channelMap.get(map.get("ContentId")));
			}
			List<Map<String, Object>> kws = keyWordProService.getKeyWordsByIds(assetIds.substring(3).replace("id", "kws.resId"), mediaType.equals("SEQU")?"wt_SeqMediaAsset":"wt_MediaAsset");
			if (kws!=null) {
				for (Map<String, Object> m1 : retLs) {
					for (Map<String, Object> m2 : kws) {
						if (m1.get("ContentId").equals(m2.get("resId"))) {
							Map<String, Object> kw = new HashMap<>();
							kw.put("KwName", m2.get("kwName"));
							kw.put("CTime", m2.get("cTime"));
							if (m1.containsKey("ContentKeyWords")) {
								List<Map<String, Object>> kwls = (List<Map<String, Object>>) m1.get("ContentKeyWords");
								kwls.add(kw);
							} else {
								List<Map<String, Object>> kwls = new ArrayList<>();
								kwls.add(kw);
								m1.put("ContentKeyWords", kwls);
							}
						}
					}
				}
			}
			
			List<Map<String, Object>> dictrefs = dictContentService.getDictRefListByIdsAndMeidaType(assetIds.replace("id", "resd.resId").substring(3), mediaType.equals("SEQU")?"wt_SeqMediaAsset":"wt_MediaAsset");
			if (dictrefs!=null) {
				for (Map<String, Object> m1 : retLs) {
					for (Map<String, Object> m2 : dictrefs) {
						if (m1.get("ContentId").equals(m2.get("resId"))) {
							Map<String, Object> df = new HashMap<>();
							df.put("CataDid", m2.get("dictDid"));
							df.put("CataMName", "内容分类");
							df.put("CataTitle", m2.get("ddName"));
							df.put("CataMId", m2.get("dictMid"));
							if (m1.containsKey("ContentCatalogs")) {
								List<Map<String, Object>> dfls = (List<Map<String, Object>>) m1.get("ContentCatalogs");
								dfls.add(df);
							} else {
								List<Map<String, Object>> dfls = new ArrayList<>();
								dfls.add(df);
								m1.put("ContentCatalogs", dfls);
							}
						}
					}
				}
			}
			List<Map<String, Object>> perls = personService.getPersonsByResIdsAndResTableName(assetIds.replace("id", "perf.resId").substring(3), mediaType.equals("SEQU")?"wt_SeqMediaAsset":"wt_MediaAsset");
			if (perls!=null) {
				for (Map<String, Object> m1 : retLs) {
					for (Map<String, Object> m2 : perls) {
						if (m1.get("ContentId").equals(m2.get("resId"))) {
							Map<String, Object> df = new HashMap<>();
							df.put("RefName", "主播");
							df.put("PerId", m2.get("resId"));
							df.put("PerName", m2.get("pName"));
							if (m1.containsKey("ContentPersons")) {
								List<Map<String, Object>> dfls = (List<Map<String, Object>>) m1.get("ContentPersons");
								dfls.add(df);
							} else {
								List<Map<String, Object>> dfls = new ArrayList<>();
								dfls.add(df);
								m1.put("ContentPersons", dfls);
							}
						}
					}
				}
			}
			retM.put("List", retLs);
			return retM;
		}
		return null;
	}
	
	private boolean modifyRevocation(List<Map<String, Object>> contentIds, int flowFlag, String reDescn) {
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
					    chaid += " or cha.channelId = '"+str+"'";
				    }
				    chaid = chaid.substring(3);
				    chaid = " (" + chaid + ")";
				    
				    conid += " or ( cha.assetId = '" + map.get("Id") + "'";
				    if (mediaType!=null) {
						conid +=  " and cha.assetType = '" + mediaType + "'";
					}
				    conid += " and "+ chaid +")";
			    } else {
			    	conid += " or ( cha.assetId = '" + map.get("Id") + "'";
			    	if (mediaType!=null) {
						conid += " and cha.assetType = '" + mediaType + "' )";
					} else conid += ")" ;
			    }
			} else {
				conid += " or ( cha.assetId = '" + map.get("Id") + "'";
		    	if (mediaType!=null) {
					conid += " and cha.assetType = '" + mediaType + "' )";
				} else conid += ")" ;
			}
		}
		if (conid.length()>3) {
			conid = conid.substring(3);
			conid = " (" + conid + ")";
			wheresql += " and" + conid;
			List<Map<String, Object>> ls = channelAssetProgressService.getChannelAssetProgresBy(2, 0, wheresql, null);
			if (ls!=null && ls.size()>0) {
				if (flowFlag==1) {
					for (Map<String, Object> map : ls) {
						ChannelAssetProgressPo cProgressPo = new ChannelAssetProgressPo();
						cProgressPo.setId(map.get("chapId")+"");
						cProgressPo.setReFlowFlag(1);
						cProgressPo.setReDescn(reDescn);
						cProgressPo.setModifyTime(new Timestamp(System.currentTimeMillis()));
						channelAssetProgressService.updateChannelAssetProgress(cProgressPo);
						ChannelAssetPo cAssetPo = new ChannelAssetPo();
						cAssetPo.setId(map.get("id")+"");
						cAssetPo.setFlowFlag(4);
						mediaService.updateCha(cAssetPo);
					}
				} else {
					for (Map<String, Object> map : ls) {
						ChannelAssetProgressPo cProgressPo = new ChannelAssetProgressPo();
						cProgressPo.setId(map.get("chapId")+"");
						cProgressPo.setReFlowFlag(2);
						cProgressPo.setReDescn(reDescn);
						cProgressPo.setModifyTime(new Timestamp(System.currentTimeMillis()));
						channelAssetProgressService.updateChannelAssetProgress(cProgressPo);
					}
				}
			}
		}
		return true;
	}
	
	/**
     * 获得内容快照
     * @param cacheDBIds 例如 AUDIO_00022bbcc4b349f582587c2ef579ae5f_INFO
     * @param page 当cacheDBId为专辑时，才有效;
     * @param pageSize 当cacheDBId为专辑时，才有效;
     * @param isOrNoToLoad 当cacheDB未包含id信息时是否查询
     * @return
     *         返回结果包含内容信息，栏目信息，字典信息，播放次数，专辑下级节目信息
     */
    public List<Map<String, Object>> getCacheDBList(List<String> cacheDBIds, int page, int pageSize, boolean isOrNoToLoad) {
    	if (cacheDBIds!=null && cacheDBIds.size()>0) {
    		List<Map<String, Object>> retList = new ArrayList<>();
    		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(cacheDBIds.size());
    		for (int i = 0; i < cacheDBIds.size(); i++) {
    			int f = i;
				fixedThreadPool.execute(new Runnable() {
					public void run() {
						try {
							String cacheDBId = cacheDBIds.get(f);
							retList.add(null);
							if (cacheDBId!=null && cacheDBId.length()>0) {
								String[] params = cacheDBId.split("_");
								String type = params[0];
								String id = params[1];
								Map<String, Object> retM = getCacheDBInfo(id, type, page, pageSize);
								if (retM==null) {
									if (isOrNoToLoad) {
										if (type.equals("SEQU")) retM = getContentInfo(5, 1, id, "wt_SeqMediaAsset");//getSeqMaInfo(id, pageSize, page, 1, null);
										else if (type.equals("AUDIO")) retM = getContentInfo(0, 0, id, "wt_MediaAsset");//getMaInfo(id, null);
									}
//									addCacheDBInfo(id, type); //往CacheDB表里添加数据
								}
								if (retM!=null && retM.size()>0) {
									retList.set(f, retM);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
    		
    		fixedThreadPool.shutdown();
			while (true) {
				try {
					Thread.sleep(20);
				} catch (Exception e) {e.printStackTrace();}
				if (fixedThreadPool.isTerminated()) {
					break;
				}
			}
			if (retList!=null && retList.size()>0) {
				Iterator<Map<String, Object>> it = retList.iterator();
				while (it.hasNext()) {
					Map<String, Object> m = it.next();
					if (m==null) {
						it.remove();
					}
				}
				return retList;
			}
		}
		return null;
    }
    
    /**
     * 
     * @param id 内容Id，目前只限专辑和节目,
     * @param type 内容类型，SEQU，AUDIO
     * @param page 
     *            当type='SEQU'时有效，专辑下级节目页码，默认为0
     * @param pageSize
     *            当type='SEQU'时有效，专辑下级节目每页页数，默认为0
     * @return
     */
    public Map<String, Object> getCacheDBInfo(String id, String type, int page, int pageSize) {
    	if (id!=null && type!=null) {
			if (type.equals("wt_SeqMediaAsset")) type = "SEQU";
			if (type.equals("wt_MediaAsset")) type = "AUDIO";
			String cacheDBInfostr = cacheDBService.getCacheDBInfo(type+"_"+id+"_INFO");
			if (cacheDBInfostr!=null && cacheDBInfostr.length()>0) {
				Map<String, Object> cacheDBMap = (Map<String, Object>) JsonUtils.jsonToObj(cacheDBInfostr, Map.class);
				long playcount = playCountDBService.getPlayCountNum(type+"_"+id+"_PLAYCOUNT");
				cacheDBMap.put("PlayCount", playcount);
				if (type.equals("SEQU") && page>0 && pageSize>0) {
					String smaSubList = cacheDBService.getCacheDBInfo(type+"_"+id+"_SUBLIST");
					if (smaSubList!=null && smaSubList.length()>0) {
						List<String> smaSubs = (List<String>) JsonUtils.jsonToObj(smaSubList, List.class);
						if (smaSubs!=null && smaSubs.size()>0) {
							int begNum = (page-1)*pageSize;
							int endNum = page*pageSize;
							if (smaSubs.size()>=begNum) {
								List<String> maIds = smaSubs.subList(begNum, smaSubs.size()>endNum?endNum:(smaSubs.size()-1));
								if (maIds!=null && maIds.size()>0) {
									List<Map<String, Object>> audios = cacheDBService.getCacheDBAudios(maIds);
									if (audios!=null && audios.size()>0) {
										cacheDBMap.put("SubList", audios);
									}
								}
							}
							if (!cacheDBMap.containsKey("SubList")) {
								cacheDBMap.put("SubList", null);
							}
						}
					}
				} else {
					if (type.equals("AUDIO")) {
						try {
							Map<String, Object> smamap = (Map<String, Object>) cacheDBMap.get("SeqInfo");
						    String smaId = smamap.get("ContentId").toString();
						    String smastr = cacheDBService.getCacheDBInfo("SEQU_"+smaId+"_INFO");
						    smamap = (Map<String, Object>) JsonUtils.jsonToObj(smastr, Map.class);
						    cacheDBMap.put("SeqInfo", smamap);
						} catch (Exception e) {}
					}
				}
				return cacheDBMap;
			}
		}
		return null;
    }

	public Map<String, Object> getPlayCounts(String contentIds) {
		String[] ids = contentIds.split(",");
		if (ids!=null && ids.length>0) {
			String sqlIds = "";
			Map<String, Object> pcdbmap = new HashMap<>();
			Map<String, Object> alaumap = new HashMap<>();
			for (String id : ids) {
				String[] ssids = id.split("_");
				String sId = ssids[0];
				if (ssids.length>1) {
					String mId = ssids[1];
					if (!sqlIds.contains(mId)) sqlIds += " or id = 'AUDIO_"+mId+"_PLAYCOUNT'";
					alaumap.put(mId, sId);
				} else if (!sqlIds.contains(sId)) sqlIds += " or id = 'SEQU_"+sId+"_PLAYCOUNT'";
				pcdbmap.put(id, null);
			}
			if (sqlIds.length()>3) {
				sqlIds = sqlIds.substring(3);
				List<PlayCountDBPo> pos = playCountDBService.getPlayCountsBySql(sqlIds);
				if (pos!=null && pos.size()>0) {
					for (PlayCountDBPo playCountDBPo : pos) {
						if (playCountDBPo.getResTableName().equals("AUDIO")) {
							String sId = alaumap.get(playCountDBPo.getResId()).toString();
							pcdbmap.put(sId+"_"+playCountDBPo.getResId(), playCountDBPo.getPlayCount());
						} else if (playCountDBPo.getResTableName().equals("SEQU")) {
							pcdbmap.put(playCountDBPo.getResId(), playCountDBPo.getPlayCount());
						}
					}
				}
				Set<String> sets = pcdbmap.keySet();
				if (sets!=null && sets.size()>0) {
					for (String id : sets) {
						if (pcdbmap.get(id)==null) {
							pcdbmap.put(id, 0);
						}
					}
					return pcdbmap;
				}
			}
		}
		return null;
	}

	/**
	 * 给栏目下的某个资源设置置顶
	 * @param mediaType 资源类型
	 * @param contentId 资源ID
	 * @param channelId 所属栏目ID
	 * @param onlyTop 是否该栏目下仅允许一个资源置顶
	 * @param topSort 置顶排序号，越大约考前
	 * @return
	 */
    @SuppressWarnings("unchecked")
    public Map<String, Object> setTop(String mediaType, String contentId, String channelId, int onlyTop, int topSort) {
        String _err=","+(StringUtils.isNullOrEmptyOrSpace(mediaType)?"媒体类型为空":"");
        _err+=","+(StringUtils.isNullOrEmptyOrSpace(mediaType)?"内容Id为空":"");
        _err+=","+(StringUtils.isNullOrEmptyOrSpace(mediaType)?"栏目Id为空":"");
        _err=_err.substring(1);

        Map<String, Object> map=new HashMap<String, Object>();
        if (StringUtils.isNullOrEmptyOrSpace(_err)) {
            map.put("ReturnType", "0000");
            map.put("Message", "无法获取需要的参数:"+_err);
            return map;
        }
        MediaType mt=MediaType.buildByTypeName(mediaType);
        if (mt==MediaType.ERR) {
            map.put("ReturnType", "0000");
            map.put("Message", "参数不合法：资源类型设置错误");
            return map;
        }

        //获得栏目信息
        _CacheChannel _cc=(SystemCache.getCache(WtContentMngConstants.CACHE_CHANNEL)==null?null:((CacheEle<_CacheChannel>)SystemCache.getCache(WtContentMngConstants.CACHE_CHANNEL)).getContent());
        TreeNode<Channel> c=(TreeNode<Channel>)_cc.channelTree.findNode(channelId);
        if (c==null) {
            map.put("ReturnType", "1004");
            map.put("Message", "栏目不存在");
            return map;
        }
        //获取栏目下的资源
        String tableName=mt.getTabName();
        ChannelAssetPo caPo=null;
        try {
            caPo=chService.getContentByBizKey(channelId, tableName, contentId);
        } catch(Exception e) {
        }
        if (caPo==null) {
            map.put("ReturnType", "1003");
            map.put("Message", "内容不存在");
            return map;
        }
        if (caPo.getFlowFlag()!=2||caPo.getIsValidate()!=1) {
            map.put("ReturnType", "1006");
            map.put("Message", "该内容在此栏目下未发布或无效，不必设置");
            return map;
        }
        map.put("ReturnType", "1001");
        if (topSort==0) {//取消置顶，直接设置就可以了
            if (!chService.cancelTop(channelId, tableName, contentId)) {
                map.put("ReturnType", "1005");
                map.put("Message", "设置失败");
            }
        } else {
            int _topSort=(onlyTop==1?topSort:1);
            if (!chService.setTop(channelId, tableName, contentId, _topSort, onlyTop)) {
                map.put("ReturnType", "1005");
                map.put("Message", "设置失败");
            }
        }
        return map;
    }

    /**
     * 获取某栏目下的轮播图列表
     * @param mediaType 过滤条件，按类型过滤
     * @param channelId 栏目Id
     * @param pageSize 每页有几条记录
     * @param pageIndex 页码，若为0,则得到所有内容
     * @return 轮播图列表
     */
    public List<Map<String, Object>> getLoopImgList(String mediaType, String channelId, int pageSize, int pageIndex) {
        if (StringUtils.isNullOrEmptyOrSpace(channelId)) return null;

        return clmService.getLoopImgList(mediaType, channelId, pageSize, pageIndex);
    }
    
    /**
     * 某一栏目下轮播图排序
     * @param mediaType 过滤条件，按类型过滤
     * @param channelId 栏目Id
     * @param contentId 内容Id
     * @param loopSort  =-1:下移; =1:上移
     */
    public boolean updateLoopSort(String mediaType, String channelId, String contentId, int loopSort) {
        if (StringUtils.isNullOrEmptyOrSpace(channelId) || StringUtils.isNullOrEmptyOrSpace(contentId) || loopSort==0) return false;

        return clmService.updateLoopSort(mediaType, channelId, contentId, loopSort);
    }
}