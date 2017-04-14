package com.woting.content.share.service;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.ext.spring.redis.RedisOperService;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.cm.cachedb.cachedb.service.CacheDBService;
import com.woting.cm.cachedb.playcountdb.service.PlayCountDBService;
import com.woting.cm.core.channel.persis.po.ChannelAssetPo;
import com.woting.cm.core.media.persis.po.MediaAssetPo;
import com.woting.cm.core.media.persis.po.SeqMediaAssetPo;
import com.woting.cm.core.media.service.MediaService;
import com.woting.content.publish.utils.CacheUtils;
import com.woting.content.share.utils.FileUtils;
import com.woting.content.share.utils.SHA1;

public class ShareService {
	@Resource
	private MediaService mediaService;
	private DataSource DataSource = null;
	@Resource
	private CacheDBService cacheDBService;
	@Resource
	private PlayCountDBService playCountDBService;

	public boolean getShareHtml(String resId, String mediaType) {
		if (!StringUtils.isNullOrEmptyOrSpace(mediaType) && !resId.toLowerCase().equals("null")) {
			if (!StringUtils.isNullOrEmptyOrSpace(mediaType) && !mediaType.toLowerCase().equals("null")) {
				if (mediaType.equals("SEQU")) {
					
					SeqMediaAssetPo sma = mediaService.getSmaInfoById(resId);
					if (sma != null) {
						List<SeqMediaAssetPo> listpo = new ArrayList<>();
						listpo.add(sma);
						List<Map<String, Object>> smam = mediaService.makeSmaListToReturn(listpo);
						if (smam != null && smam.size() > 0) {
							Map<String, Object> map = new HashMap<>();
							map.put("ContentDetail", smam.get(0));
							List<MediaAssetPo> mas = mediaService.getMaListBySmaId(resId, 0, 0);
							if (mas != null && mas.size() > 0) {
								Iterator<MediaAssetPo> it = mas.iterator();
								while (it.hasNext()) {
									MediaAssetPo mediaAssetPo = (MediaAssetPo) it.next();
									String resIds = "'" + mediaAssetPo.getId() + "'";
									List<ChannelAssetPo> chas = mediaService.getCHAListByAssetId(resIds, "wt_MediaAsset");
									if (chas != null && chas.size() > 0) {
										ChannelAssetPo chapo = chas.get(0);
										if (chapo.getFlowFlag() != 2)
											it.remove();
									}
								}
								map.put("SubList", mediaService.makeMaListToReturn(mas));
								CacheUtils.publishZJ(map);
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	public Map<String, Object> getWXConfigInfo(String url) {
		long timestamp = System.currentTimeMillis()/1000;
		String nonceStr = SequenceUUID.getUUIDSubSegment(4);
		Map<String, Object> retM = new HashMap<>();
		try {
			ServletContext sc=(SystemCache.getCache(FConstants.SERVLET_CONTEXT)==null?null:(ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent());
	        if (WebApplicationContextUtils.getWebApplicationContext(sc)!=null) {
	            JedisConnectionFactory conn=(JedisConnectionFactory)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("connectionFactory123");
	            RedisOperService ros=new RedisOperService(conn, 5);
	            if (ros.exist("WX_JSAPI_TICKET")) {
	            	String ticket = ros.get("WX_JSAPI_TICKET");
					Map<String, Object> sha1m = new HashMap<>();
					sha1m.put("jsapi_ticket", "="+ticket+"&");
					sha1m.put("noncestr", "="+nonceStr+"&");
					sha1m.put("timestamp", "="+timestamp+"&");
					sha1m.put("url", "="+url);
					String Signature =  SHA1.SHA1(sha1m);
					retM.put("AppId", "wxd4cb3b422606e373");
					retM.put("TimeStamp", timestamp);
					retM.put("NonceStr", nonceStr);
					retM.put("Signature", Signature);
					return retM;
				}
	        }
		} catch (Exception e) {
			retM.put("ReturnType", 1011);
			retM.put("Message", "获取异常");
		}
		return null;
	}

	public void makeCacheDBInfo(String smaId) {
		addRedia(smaId);
	}
	
	public void addRedia(String smaId) {
		try {
			RedisOperService redis = null;
			ServletContext sc=(SystemCache.getCache(FConstants.SERVLET_CONTEXT)==null?null:(ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent());
	        if (WebApplicationContextUtils.getWebApplicationContext(sc)!=null) {
	            JedisConnectionFactory conn=(JedisConnectionFactory)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("connectionFactory182");
	            redis = new RedisOperService(conn);
	            this.DataSource = (DataSource) WebApplicationContextUtils.getWebApplicationContext(sc).getBean("dataSource");
	        }
			System.out.println("1");
			Map<String, Object> oneDate = makeSeqMediaAssetInfo(smaId); // 获得专辑相关静态信息
			List<String> maIds = getSeqMARef(smaId); // 获得专辑下级节目id列表
			if (oneDate!=null && oneDate.size()>0) {
				if (maIds!=null && maIds.size()>0) {
					oneDate.put("ContentSubCount", maIds.size());
				}
				writeContentInfo("Content=MediaType_CID=[SEQU_"+smaId+"]=INFO", JsonUtils.objToJson(oneDate));
			}
			System.out.println("2");
			List<Map<String, Object>> reply = new ArrayList<>();
			if (maIds!=null && maIds.size()>0) {
				List<String> retMaIds = new ArrayList<>();
				for (String str : maIds) {
					Map<String, Object> m = new HashMap<>();
					m.put("id", str);
					m.put("type", "wt_MediaAsset");
					reply.add(m);
					retMaIds.add("Content=MediaType_CID=[AUDIO_"+str+"]");
				}
				writeContentInfo("Content=MediaType_CID=[SEQU_"+smaId+"]=SUBLIST", JsonUtils.objToJson(retMaIds));
			}
			System.out.println("3");
			List<Map<String, Object>> maLs = getMediaAssetInfos(maIds);
			if (maLs!=null && maLs.size()>0) {
				for (Map<String, Object> map : maLs) {
					Map<String, Object> smam = new HashMap<>();
					smam.put("ContentId", smaId);
					map.put("SeqInfo", smam);
					writeContentInfo("Content=MediaType_CID=[AUDIO_"+map.get("ContentId")+"]=INFO", JsonUtils.objToJson(map));
				}
			}
			System.out.println("4");
			Map<String, Object> m = new HashMap<>();
			m.put("id", smaId);
			m.put("type", "wt_SeqMediaAsset");
			reply.add(m);
			List<Map<String, Object>> playLs = getPlayCountInfo(reply);
			if (playLs!=null && playLs.size()>0) {
				for (Map<String, Object> map : playLs) {
					addRedisInfo(redis,"Content=MediaType_CID=["+map.get("type")+"_"+map.get("id")+"]=PLAYCOUNT", map.get("playcount")+"", 30*24*60*60*1000l);
				}
			}
			redis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Map<String, Object> makeSeqMediaAssetInfo(String smaId) {
		Map<String, Object> oneDate = new HashMap<String, Object>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DataSource.getConnection();
			String sql = "SELECT id,smaTitle,smaPublisher,keyWords,descn,smaImg,cTime FROM wt_SeqMediaAsset where id = '"+smaId+"'";
			try {
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					oneDate.put("ContentName", rs.getString("smaTitle"));
					oneDate.put("ContentId", rs.getString("id"));
					oneDate.put("ContentPub", rs.getString("smaPublisher"));
					oneDate.put("MediaType", "SEQU");
					oneDate.put("ContentDesc", rs.getString("descn"));
					oneDate.put("ContentImg", rs.getString("smaImg"));
					oneDate.put("ContentKeyWord", rs.getString("keyWords"));
					oneDate.put("CTime", rs.getTimestamp("cTime").getTime());
					oneDate.put("ContentPubTime", rs.getTimestamp("cTime").getTime());
					oneDate.put("ContentSubscribe", 0);
					oneDate.put("ContentFavorite", 0);
					oneDate.put("ContentShareURL", "http://www.wotingfm.com/dataCenter/shareH5/mweb/zj/"+oneDate.get("ContentId").toString()+"/content.html");
				}
				if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
			} catch (Exception e) {e.printStackTrace();}
			sql = "SELECT cha.channelId,ch.channelName,cha.publisherId,org.oName,cha.pubTime,cha.flowFlag FROM wt_ChannelAsset cha"
					+ " LEFT JOIN wt_Channel ch ON ch.id = cha.channelId LEFT JOIN wt_Organize org ON org.id = cha.publisherId"
					+ " where cha.assetId = '"+smaId+"' and cha.assetType = 'wt_SeqMediaAsset' and cha.flowFlag = 2";
			try {
				List<Map<String, Object>> chals = new ArrayList<>();
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					Map<String, Object> chamap = new HashMap<>();
					chamap.put("FlowFlag", rs.getInt("flowFlag"));
					chamap.put("ChannelName", rs.getString("channelName"));
					chamap.put("ChannelId", rs.getString("channelId"));
					chamap.put("PubTime", rs.getTimestamp("pubTime"));
					chals.add(chamap);
				}
				oneDate.put("ContentPubChannels", chals);
				if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
			} catch (Exception e) {
				e.printStackTrace();
			}
			sql = "SELECT resf.dictMid mid,dd.id did,dd.ddName,resf.refName FROM wt_ResDict_Ref resf"
					+ " LEFT JOIN plat_DictD dd ON dd.id = resf.dictDid"
					+ " where resf.resId = '"+smaId+"' and resf.resTableName = 'wt_SeqMediaAsset'";
			try {
				List<Map<String, Object>> dictls = new ArrayList<>();
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					Map<String, Object> catamap = new HashMap<>();
					catamap.put("CataDid", rs.getString("did"));
					catamap.put("CataMName", rs.getString("refName"));
					catamap.put("CataTitle", rs.getString("ddName"));
					catamap.put("CataMid", rs.getString("mid"));
					dictls.add(catamap);
				}
				oneDate.put("ContentCatalogs", dictls);
				if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
			} catch (Exception e) {
				e.printStackTrace();
			}
			sql = "SELECT per.id,per.pName,pef.refName FROM wt_Person_Ref pef"
					+ " LEFT JOIN wt_Person per ON per.id = pef.personId"
					+ " where pef.resId = '"+smaId+"' and pef.resTableName = 'wt_SeqMediaAsset'";
			try {
				List<Map<String, Object>> perls = new ArrayList<>();
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					Map<String, Object> permap = new HashMap<>();
					permap.put("PerId", rs.getString("id"));
					permap.put("PerName", rs.getString("pName"));
					permap.put("RefName", rs.getString("refName"));
					perls.add(permap);
				}
				oneDate.put("ContentPersons", perls);
				if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (oneDate!=null && oneDate.size()>0) {
				return oneDate;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
        }
		return null;
	}
	
	private List<String> getSeqMARef(String smaId) {
		if (smaId!=null) {
			List<String> maIds = new ArrayList<>();
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			String sql = "SELECT mId,columnNum FROM wt_SeqMA_Ref"
					+ " where sId = '"+smaId+"'"
					+ " ORDER BY columnNum DESC";
			try {
				conn = DataSource.getConnection();
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					maIds.add(rs.getString("mId"));
				}
				if (maIds!=null && maIds.size()>0) {
					return maIds;
				}
				if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
	            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
	            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
	        }
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getMediaAssetInfos(List<String> maIds) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if (maIds!=null && maIds.size()>0) {
				List<Map<String, Object>> retLs = new ArrayList<>();
				String ids = "";
				for (String str : maIds) {
					ids += " or id = '"+str+"'";
				}
				ids = ids.substring(3);
				ids = "("+ids+")";
				String sql = "SELECT ma.id,ma.maTitle,ma.maPublisher,ma.keyWords,ma.descn,ma.maImg,ma.timeLong,ma.cTime,mas.playURI FROM wt_MediaAsset ma"
						+ " LEFT JOIN wt_MaSource mas ON mas.maId = ma.id and mas.isMain = 1"
						+ " where "+ids.replace("id", "ma.id");
				conn = DataSource.getConnection();
				try {
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while (rs != null && rs.next()) {
						Map<String, Object> oneDate = new HashMap<>();
						oneDate.put("ContentName", rs.getString("maTitle"));
						oneDate.put("ContentId", rs.getString("id"));
						oneDate.put("ContentPub", rs.getString("maPublisher"));
						oneDate.put("MediaType", "AUDIO");
						oneDate.put("ContentDesc", rs.getString("descn"));
						oneDate.put("ContentImg", rs.getString("maImg"));
						oneDate.put("CTime", rs.getTimestamp("cTime").getTime());
						oneDate.put("ContentPlay", rs.getString("playURI"));
						oneDate.put("ContentKeyWord", rs.getString("keyWords"));
						oneDate.put("ContentTimes", rs.getLong("timeLong"));
						oneDate.put("ContentPubTime", rs.getTimestamp("cTime").getTime());
						oneDate.put("ContentShareURL", "http://www.wotingfm.com/dataCenter/shareH5/mweb/jm/"+oneDate.get("ContentId").toString()+"/content.html");
						try {
							String ext = FileNameUtils.getExt(oneDate.containsKey("ContentPlay")?(oneDate.get("ContentPlay").toString()):null);
					        if (ext!=null) {
					        	if (ext.contains("?")) {
								    ext = ext.replace(ext.substring(ext.indexOf("?"), ext.length()), ""); 
							    }
					        	oneDate.put("ContentPlayType", ext.contains("/flv")?"flv":ext.replace(".", ""));
							} else {
								oneDate.put("ContentPlayType", null);
							}
						} catch (Exception e) {
							oneDate.put("ContentPlayType", null);
						}
						retLs.add(oneDate);
					}
					if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
		            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (retLs!=null && retLs.size()==0) {
					return null;
				}
				sql = "SELECT cha.assetId,cha.channelId,ch.channelName,cha.publisherId,org.oName,cha.pubTime,cha.flowFlag FROM wt_ChannelAsset cha"
						+ " LEFT JOIN wt_Channel ch ON ch.id = cha.channelId LEFT JOIN wt_Organize org ON org.id = cha.publisherId"
						+ " where "+ids.replace("id", "cha.assetId")+" and cha.assetType = 'wt_MediaAsset' and cha.flowFlag = 2";
				try {
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while (rs != null && rs.next()) {
						Map<String, Object> chamap = new HashMap<>();
						chamap.put("FlowFlag", rs.getInt("flowFlag"));
						chamap.put("ChannelName", rs.getString("channelName"));
						chamap.put("ChannelId", rs.getString("channelId"));
						chamap.put("PubTime", rs.getTimestamp("pubTime").getTime());
						for (Map<String, Object> map : retLs) {
							if (map.get("ContentId").equals(rs.getString("assetId"))) {
								if (map.containsKey("ContentPubChannels")) {
									List<Map<String, Object>> chamapls = (List<Map<String, Object>>) map.get("ContentPubChannels");
									if (chamapls!=null) {
										chamapls.add(chamap);
									} else {
										chamapls = new ArrayList<>();
										chamapls.add(chamap);
										map.put("ContentPubChannels", chamapls);
									}
								} else {
									List<Map<String, Object>> chamapls = new ArrayList<>();
									chamapls.add(chamap);
									map.put("ContentPubChannels", chamapls);
								}
							}
						}
					}
					if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
		            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
				} catch (Exception e) {
					e.printStackTrace();
				}
				sql = "SELECT resf.dictMid mid,dd.id did,dd.ddName,resf.refName,resf.resId FROM wt_ResDict_Ref resf"
						+ " LEFT JOIN plat_DictD dd ON dd.id = resf.dictDid"
						+ " where "+ids.replace("id", "resf.resId")+" and resf.resTableName = 'wt_MediaAsset'";
				try {
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while (rs != null && rs.next()) {
						Map<String, Object> catamap = new HashMap<>();
						catamap.put("CataDid", rs.getString("did"));
						catamap.put("CataMName", rs.getString("refName"));
						catamap.put("CataTitle", rs.getString("ddName"));
						catamap.put("CataMid", rs.getString("mid"));
						for (Map<String, Object> map : retLs) {
							if (map.get("ContentId").equals(rs.getString("resId"))) {
								if (map.containsKey("ContentCatalogs")) {
									List<Map<String, Object>> catamapls = (List<Map<String, Object>>) map.get("ContentCatalogs");
									if (catamapls!=null) {
										catamapls.add(catamap);
									} else {
										catamapls = new ArrayList<>();
										catamapls.add(catamap);
										map.put("ContentCatalogs", catamapls);
									}
								} else {
									List<Map<String, Object>> catamapls = new ArrayList<>();
									catamapls.add(catamap);
									map.put("ContentCatalogs", catamapls);
								}
							}
						}
					}
					if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
		            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
				} catch (Exception e) {
					e.printStackTrace();
				}
				sql = "SELECT per.id,per.pName,pef.refName,pef.resId FROM wt_Person_Ref pef"
						+ " LEFT JOIN wt_Person per ON per.id = pef.personId"
						+ " where "+ids.replace("id", "pef.resId")+" and pef.resTableName = 'wt_MediaAsset'";
				try {
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while (rs != null && rs.next()) {
						Map<String, Object> permap = new HashMap<>();
						permap.put("PerId", rs.getString("id"));
						permap.put("PerName", rs.getString("pName"));
						permap.put("RefName", rs.getString("refName"));
						for (Map<String, Object> map : retLs) {
							if (map.get("ContentId").equals(rs.getString("resId"))) {
								if (map.containsKey("ContentPersons")) {
									List<Map<String, Object>> pomapls = (List<Map<String, Object>>) map.get("ContentPersons");
									if (pomapls!=null) {
										pomapls.add(permap);
									} else {
										pomapls = new ArrayList<>();
										pomapls.add(permap);
										map.put("ContentPersons", pomapls);
									}
								} else {
									List<Map<String, Object>> pomapls = new ArrayList<>();
									pomapls.add(permap);
									map.put("ContentPersons", pomapls);
								}
							}
						}
					}
					if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
		            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
				} catch (Exception e) {
					e.printStackTrace();
				}
				return retLs;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
        }
		return null;
	}
	
	private List<Map<String, Object>> getPlayCountInfo(List<Map<String, Object>> ls) {
		String sql = "";
		if (ls!=null && ls.size()>0) {
			for (Map<String, Object> map : ls) {
				sql += " UNION SELECT resId,resTableName,playCount FROM wt_MediaPlayCount"
						+ " where resId = '"+map.get("id")+"' AND resTableName = '"+map.get("type")+"'";
			}
			sql = sql.substring(6);
			List<Map<String, Object>> retLs = new ArrayList<>();
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				conn = DataSource.getConnection();
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					Map<String, Object> m = new HashMap<>();
					m.put("id", rs.getString("resId"));
					m.put("playcount", rs.getLong("playCount"));
					m.put("type", rs.getString("resTableName").equals("wt_SeqMediaAsset")?"SEQU":"AUDIO");
					retLs.add(m);
				}
				if (retLs!=null && retLs.size()>0) {
					return retLs;
				}
				if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
	            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
	            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
	        }
		}
		return null;
	}
	
	private void addRedisInfo(RedisOperService redis, String key, String info, long timeout) {
		if (redis!=null && key!=null) {
			try {
				redis.set(key, info, timeout);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void writeContentInfo(String key, String jsonstr) {
		File file = FileUtils.createFile("/opt/dataCenter/contentinfo/"+key+".json");
		FileUtils.writeFile(jsonstr, file);
	}
}
