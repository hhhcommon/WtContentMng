package com.woting.cm.core.subscribe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import org.springframework.web.context.support.WebApplicationContextUtils;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.woting.WtContentMngConstants;
import com.woting.cm.core.subscribe.service.SubscribeService;
import com.woting.push.core.message.MsgNormal;
import com.woting.push.core.message.content.MapContent;
import com.woting.push.socketclient.oio.SocketClient;

public class SubscribeThread extends Thread {
	private String maId;
	
	public SubscribeThread(String maId) {
		this.maId = maId;
	}

	@Override
	public void run() {
		try {
			SubscribeService subscribeService = null ;
			DataSource DataSource = null ;
			ServletContext sc=(SystemCache.getCache(FConstants.SERVLET_CONTEXT)==null?null:(ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent());
	        if (WebApplicationContextUtils.getWebApplicationContext(sc)!=null) {
	            subscribeService =(SubscribeService) WebApplicationContextUtils.getWebApplicationContext(sc).getBean("subscribeService");
	            DataSource = (javax.sql.DataSource) WebApplicationContextUtils.getWebApplicationContext(sc).getBean("dataSource");
	        }
			List<Map<String, Object>> owners = subscribeService.getSubscribeUsers(maId);
			if (owners!=null) {
				Connection conn = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				
				@SuppressWarnings("unchecked")
				SocketClient sco=((CacheEle<SocketClient>)SystemCache.getCache(WtContentMngConstants.SOCKET_OBJ)).getContent();
				
				try {
					conn = DataSource.getConnection();
					String sql = "SELECT res.* FROM"
							+ " (SELECT ma.id maId,ma.maTitle,ma.maImg,ma.maPublisher,ma.timeLong,mas.playURI,ma.descn,sma.id smaId,sma.smaTitle,sma.smaImg,sma.cTime,(CASE cha.assetType WHEN 'wt_MediaAsset' THEN cha.pubTime ELSE NULL END) pubTime from wt_MediaAsset ma  "
							+ " LEFT JOIN wt_SeqMA_Ref smaf ON ma.id = smaf.mId LEFT JOIN wt_SeqMediaAsset sma ON sma.id = smaf.sId  LEFT JOIN wt_ChannelAsset cha ON ((cha.assetId = ma.id and cha.assetType = 'wt_MediaAsset') OR (cha.assetId = sma.id and cha.assetType = 'wt_SeqMediaAsset')) and cha.flowFlag = 2, wt_MaSource mas"
							+ " where maId = '"+maId+"' and mas.maId = ma.id and mas.isMain = 1) res"
							+ " WHERE res.pubTime IS NOT NULL";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					Map<String, Object> seqMediaInfo = new HashMap<>();
					List<Map<String, Object>> newMediaList = new ArrayList<>();
					while (rs != null && rs.next()) {
						seqMediaInfo.put("ContentId", rs.getString("smaId"));
						seqMediaInfo.put("ContentName", rs.getString("smaTitle"));
						seqMediaInfo.put("ContentImg", rs.getString("smaImg"));
						seqMediaInfo.put("ContentPub", rs.getString("maPublisher"));
						seqMediaInfo.put("ContentURI", "content/getContentInfo.do?MediaType=SEQU&ContentId="+rs.getString("smaId"));
						seqMediaInfo.put("CTime", rs.getString("cTime"));
						seqMediaInfo.put("MediaType", "SEQU");
						Map<String, Object> mediaInfo = new HashMap<>();
						mediaInfo.put("ContentId", rs.getString("maId"));
						mediaInfo.put("ContentName", rs.getString("maTitle"));
						mediaInfo.put("ContentImg", rs.getString("maImg"));
						mediaInfo.put("ContentPlay", rs.getString("playURI"));
						mediaInfo.put("ContentTimes", rs.getString("timeLong"));
						String ext = rs.getString("playURI");
						if (ext.contains("?")) {
							ext = ext.replace(ext.substring(ext.indexOf("?"), ext.length()), ""); 
						}
				        if (ext!=null) mediaInfo.put("ContentPlayType", ext.contains("/flv")?"flv":ext.replace(".", "")); 
				        else  mediaInfo.put("ContentPlayType", null);
						mediaInfo.put("ContentPubTime", rs.getTimestamp("pubTime"));
						seqMediaInfo.put("MediaType", "AUDIO");
						newMediaList.add(mediaInfo);
						
						if (sc!=null) {
							 //通知消息
							int pages = owners.size()/1000+1;
							for (int i = 0; i < pages; i++) {
								List<Map<String, Object>> us = new ArrayList<>();
								if (owners.size()==1) {
									us = owners;
								} else {
									 us = owners.subList(i*1000, owners.size()<(i+1)*1000?owners.size()-1:(i+1)*1000);
								}
								String ownerIds = "";
								for (Map<String, Object> map : us) {
									ownerIds+=","+map.get("ownerId");
								}
								ownerIds = ownerIds.substring(1);
								MsgNormal nMsg=new MsgNormal();
				                nMsg.setMsgId(SequenceUUID.getUUIDSubSegment(4));
				                nMsg.setFromType(1);
				                nMsg.setToType(1);
				                nMsg.setMsgType(0);
				                nMsg.setAffirm(0);
				                nMsg.setBizType(0x04);
				                nMsg.setCmdType(4);
				                nMsg.setCommand(1);
				                Map<String, Object> dataMap = new HashMap<>();
				                dataMap.put("SeqMediaInfo", seqMediaInfo);
				                dataMap.put("NewMediaList", newMediaList);
				                MapContent mc=new MapContent(dataMap);
				                nMsg.setMsgContent(mc);
				                dataMap.put("_AFFIRMTYPE", "3");
				                dataMap.put("_TOUSERS", ownerIds);
				                sco.addSendMsg(nMsg);
				                System.out.println("订阅推送信息"+JsonUtils.objToJson(nMsg));
							}
						}
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
