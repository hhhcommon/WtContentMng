package com.woting.crawlerdb.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.ext.spring.redis.RedisOperService;
import com.spiritdata.framework.util.SequenceUUID;
import com.woting.cm.core.channel.persis.po.ChannelAssetPo;
import com.woting.cm.core.media.service.MediaService;
import com.woting.crawlerdb.album.service.AlbumService;
import com.woting.crawlerdb.audio.service.AudioService;
import com.woting.crawlerdb.dict.service.CDictService;

public class CrawlerService {
	@Resource
	private AlbumService albumService;
	@Resource
	private CDictService cdictService;
	@Resource
	private AudioService audioService;
	@Resource
    private DataSource dataSource;
	@Resource
	private MediaService mediaService;
	
    public void makeCCateResRef(String redisKey, String dictRefId, String crawlerDictdId, String channelId) {
    	new Thread(new Runnable() {
			public void run() {
				Connection conn = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					String sql = "";
					long num = 0;
					long smanum = 0;
					RedisOperService redis = null;
					ServletContext sc=(SystemCache.getCache(FConstants.SERVLET_CONTEXT)==null?null:(ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent());
			        if (WebApplicationContextUtils.getWebApplicationContext(sc)!=null) {
			            JedisConnectionFactory js =(JedisConnectionFactory) WebApplicationContextUtils.getWebApplicationContext(sc).getBean("connectionFactory123");
			            redis = new RedisOperService(js, 6);
			        }
					conn = dataSource.getConnection();
					try {
						sql = "SELECT COUNT(*) FROM crawlerDB.c_ResDict_Ref dref where dref.cdictDid = '"+crawlerDictdId+"' and dref.resTableName = 'c_Album'";
						try {
							ps = conn.prepareStatement(sql);
							rs = ps.executeQuery();
							while (rs != null && rs.next()) {
								num = rs.getLong(1);
							}
							if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
				            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
						} catch (Exception e) {e.printStackTrace();}
						int pageSize = 1000;
						int pages = (int) (num/pageSize + 1);
						for (int i = 1; i <= pages; i++) {
							String albumIds = "";
							sql = "SELECT dref.resId id FROM crawlerDB.c_ResDict_Ref dref where dref.cdictDid = '"+crawlerDictdId+"' and dref.resTableName = 'c_Album' "
									+ " limit "+(i-1)*pageSize+","+pageSize;
							try {
								ps = conn.prepareStatement(sql);
								rs = ps.executeQuery();
								while (rs != null && rs.next()) {
									albumIds += ",'"+rs.getString("id")+"'";
								}
								if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
					            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
							} catch (Exception e) {e.printStackTrace();}
							if (albumIds!=null && albumIds.length()>0) {
								albumIds = albumIds.substring(1);
								String smaIds = "";
								sql = "SELECT resId FROM wt_ResOrgAsset_Ref where origId IN ("+albumIds+") and origTableName = 'c_Album'";
								try {
									ps = conn.prepareStatement(sql);
									rs = ps.executeQuery();
									while (rs != null && rs.next()) {
										smaIds += ",'"+rs.getString("resId")+"'";
									}
									if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
						            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
								} catch (Exception e) {e.printStackTrace();}
								if (smaIds!=null && smaIds.length()>0) {
									List<ChannelAssetPo> chas = new ArrayList<>();
									smaIds = smaIds.substring(1);
									sql = "SELECT sma.id,sma.smaTitle title,sma.smaImg img,sma.cTime,sma.smaPubId pubId,sma.smaPublishTime publisherTime,'wt_SeqMediaAsset' tableName FROM wt_SeqMediaAsset sma "
											+ " where sma.id IN ("+smaIds+")"
											+ " UNION"
											+ " SELECT ma.id,ma.maTitle title,ma.maImg img,ma.cTime,ma.maPubId pubId,ma.maPublishTime publisherTime,'wt_MediaAsset' tableName FROM wt_MediaAsset ma"
											+ " where ma.id IN (SELECT mId FROM wt_SeqMA_Ref where sId IN ("+smaIds+"))";
									try {
										ps = conn.prepareStatement(sql);
										rs = ps.executeQuery();
										while (rs != null && rs.next()) {
											ChannelAssetPo cha = new ChannelAssetPo();
											cha.setId(SequenceUUID.getPureUUID());
											cha.setAssetId(rs.getString("id"));
											cha.setAssetType(rs.getString("tableName"));
											cha.setChannelId(channelId);
											cha.setFlowFlag(2);
											cha.setIsValidate(1);
											cha.setPublisherId(rs.getString("pubId"));
											cha.setPubName(rs.getString("title"));
											cha.setPubImg(rs.getString("img"));
											cha.setPubTime(rs.getTimestamp("publisherTime"));
											cha.setCheckerId("2");
											cha.setInRuleIds("wt_ChannelMapRef_"+dictRefId);
											cha.setCheckRuleIds("etl");
											chas.add(cha);
										}
										if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
							            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
									} catch (Exception e) {e.printStackTrace();}
									if (chas!=null && chas.size()>0) {
										for (ChannelAssetPo channelAssetPo : chas) {
											try {
												ChannelAssetPo existcha = mediaService.getChannelAssetByIdTypeAndChannelId(channelAssetPo.getAssetId(), channelAssetPo.getAssetType(), channelAssetPo.getChannelId());
												if (existcha==null) {
													mediaService.saveCha(channelAssetPo);
												}
												mediaService.removeCha(channelAssetPo.getAssetId(), channelAssetPo.getAssetType(), "cn36");
												if (redis!=null && channelAssetPo.getAssetType().equals("wt_SeqMediaAsset")) {
													smanum++;
													redis.set(redisKey, ((smanum+0.0)/num)+"");
												}
											} catch (Exception e) {
												e.printStackTrace();
												continue;
											}
										}
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						redis.set(redisKey, "1");
						redis.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
    	
	}
}
