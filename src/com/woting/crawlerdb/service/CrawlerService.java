package com.woting.crawlerdb.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
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
	
    public void addCCateResRef(String dictRefId, String crawlerDictdId, String channelId) {
    	new Thread(new Runnable() {
			public void run() {
				Connection conn = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					String redisKey = "wt_ChannelMapRef_"+dictRefId;
					String sql = "";
					long num = 0;
					long smanum = 0;
					RedisOperService redis = null;
					ServletContext sc=(SystemCache.getCache(FConstants.SERVLET_CONTEXT)==null?null:(ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent());
			        if (WebApplicationContextUtils.getWebApplicationContext(sc)!=null) {
			            JedisConnectionFactory js =(JedisConnectionFactory) WebApplicationContextUtils.getWebApplicationContext(sc).getBean("connectionFactory123");
			            redis = new RedisOperService(js, 6);
			        }
			        redis.set(redisKey+"_TYPE", "ADD", 30*60*1000);
			        redis.set(redisKey, "0", 30*60*1000);
			        redis.set(redisKey+"_TIME", System.currentTimeMillis()+"");
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
						int pageSize = 100;
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
												} else {
													String inRuleIds = existcha.getInRuleIds();
													if (!inRuleIds.contains(redisKey)) {
														if (inRuleIds.equals("etl")) inRuleIds = redisKey;
														else if (inRuleIds.length()>32) inRuleIds += ","+redisKey;
														existcha.setInRuleIds(inRuleIds);
														mediaService.updateCha(existcha);
													}
												}
												mediaService.removeCha(channelAssetPo.getAssetId(), channelAssetPo.getAssetType(), "cn36");
												if (channelAssetPo.getAssetType().equals("wt_SeqMediaAsset")) {
													smanum++;
													redis.set(redisKey, ((smanum+0.0)/num)+"", 60*60*1000);
													redis.set(redisKey+"_TYPE", "ADD", 60*60*1000);
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
						redis.set(redisKey, "1", 5*60*1000);
						redis.set(redisKey+"_TYPE", "ADD", 5*60*1000);
						redis.pExpire(redisKey+"_TIME", 5*60*1000);
						redis.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
    
    public void deleteCCateResRef(String dictRefId, String crawlerDictdId, String channelId) {
    	new Thread(new Runnable() {
			public void run() {
				Connection conn = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				Statement st = null;
				try {
					String sql = "";
					long num = 0;
//					long smanum = 0;
					RedisOperService redis = null;
					ServletContext sc=(SystemCache.getCache(FConstants.SERVLET_CONTEXT)==null?null:(ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent());
			        if (WebApplicationContextUtils.getWebApplicationContext(sc)!=null) {
			            JedisConnectionFactory js =(JedisConnectionFactory) WebApplicationContextUtils.getWebApplicationContext(sc).getBean("connectionFactory123");
			            redis = new RedisOperService(js, 6);
			        }
			        String inRuleIdStr = "wt_ChannelAssetMapRef_"+dictRefId;
			        redis.set(inRuleIdStr+"_TYPE", "DELETE", 30*60*1000);
			        redis.set(inRuleIdStr, "0",30*60*1000);
			        redis.set(inRuleIdStr+"_TIME", System.currentTimeMillis()+"");
					conn = dataSource.getConnection();
					try {
						sql = "SELECT count(*) FROM wt_ChannelAsset where inRuleIds LIKE '%"+inRuleIdStr+"%'";
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
						int pages = (int) (num/pageSize + 2);
						for (int i = 1; i < pages; i++) {
							redis.set(inRuleIdStr, (((i-1)+0.0)/pages)+"",60*60*1000);
							redis.set(inRuleIdStr+"_TYPE", "DELETE", 60*60*1000);
							List<String> onlyIdList = new ArrayList<>();
							List<String> removeOnlyIds = new ArrayList<>(); //删除多关系
							String onlyIds = "";
							String moreMultipleIds = "";
							sql = "SELECT id,assetId,assetType,inRuleIds, (CASE inRuleIds WHEN '"+inRuleIdStr+"' THEN 0 ELSE 1 END) multiple FROM wt_ChannelAsset where inRuleIds LIKE '%"+inRuleIdStr+"%'"
									+ " limit "+(i-1)*pageSize+","+pageSize;
							try {
								ps = conn.prepareStatement(sql);
								rs = ps.executeQuery();
								while (rs != null && rs.next()) {
									int multipleNum = rs.getInt("multiple");
									String tempStr = " or ( assetId ='"+rs.getString("assetId")+"' and assetType = '"+rs.getString("assetType")+"' )";
									if (multipleNum<1) {
										if (!onlyIds.contains(tempStr)) {
											onlyIds += tempStr;
											onlyIdList.add(rs.getString("assetId")+"_"+rs.getString("assetType"));
										}
									} else {								
										moreMultipleIds += ",'"+rs.getString("id")+"'";										
									}
								}
								if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
					            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
							} catch (Exception e) {e.printStackTrace();}
							
							if (onlyIds!=null && onlyIds.length()>0) {
								onlyIds = onlyIds.substring(3);
								String removeOnlyChannelIds = "";
								sql = "SELECT id,assetId,assetType FROM wt_ChannelAsset where ("+onlyIds+") and channelId <> '"+channelId+"' and channelId <> 'cn36'";
								try {
									ps = conn.prepareStatement(sql);
									rs = ps.executeQuery();
									while (rs != null && rs.next()) {
										if (onlyIdList!=null && onlyIdList.size()>0) {
											Iterator<String> its = onlyIdList.iterator();
											while (its.hasNext()) {
												removeOnlyIds.add(rs.getString("assetId")+"_"+rs.getString("assetType"));
												removeOnlyChannelIds += ",'"+rs.getString("id")+"'";
											}
										}
									}
									if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
						            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
								} catch (Exception e) {e.printStackTrace();}
								
								//未聚合存在多分类
								if (removeOnlyIds!=null && removeOnlyIds.size()>0) {
									onlyIdList.removeAll(removeOnlyIds);
									removeOnlyChannelIds = removeOnlyChannelIds.substring(1);
									sql = "DELETE FROM wt_ChannelAsset WHERE id in ("+removeOnlyChannelIds+")";
									try {
										st = conn.createStatement();
										st.execute(sql);
										if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
							            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
							            if (st!=null) try {st.close();ps=null;} catch(Exception e) {ps=null;} finally {st=null;};
									} catch (Exception e) {e.printStackTrace();}
								}
								//未聚合存在单分类
								if (onlyIdList!=null && onlyIdList.size()>0) {
									sql = "UPDATE wt_ChannelAsset SET inRuleIds = 'DELETE_"+inRuleIdStr+"', channelId = 'cn36' "
											+ "where ("+onlyIds+") and channelId = '"+channelId+"'";
									try {
										st = conn.createStatement();
										st.execute(sql);
										if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
							            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
							            if (st!=null) try {st.close();st=null;} catch(Exception e) {st=null;} finally {st=null;};
									} catch (Exception e) {e.printStackTrace();}
								}
							}
							
							//聚合多关系处理
							if (moreMultipleIds!=null && moreMultipleIds.length()>0) {
								moreMultipleIds = moreMultipleIds.substring(1);
								sql = "UPDATE wt_ChannelAsset SET inRuleIds = REPLACE(REPLACE(inRuleIds,',"+inRuleIdStr+"',''),'"+inRuleIdStr+",','') "
										+ " where ("+moreMultipleIds+")";
								try {
									st = conn.createStatement();
									st.execute(sql);
									if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
						            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
						            if (st!=null) try {st.close();st=null;} catch(Exception e) {st=null;} finally {st=null;};
								} catch (Exception e) {e.printStackTrace();}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						redis.set(inRuleIdStr, "1", 10*1000);
						redis.pExpire(inRuleIdStr+"_TYPE",  10*1000);
						redis.pExpire(inRuleIdStr+"_TIME", 10*1000);
						redis.close();
						if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
    }
}
