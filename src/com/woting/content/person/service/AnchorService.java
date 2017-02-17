package com.woting.content.person.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.woting.cm.core.dict.persis.po.DictRefResPo;
import com.woting.cm.core.person.persis.po.PersonPo;
import com.woting.cm.core.person.persis.po.PersonRefPo;
import com.woting.cm.core.person.service.PersonService;
import com.woting.content.manage.dict.service.DictContentService;

public class AnchorService {
	@Resource(name = "dataSource")
	private DataSource DataSource;
	@Resource
	private DictContentService dictContentService;
	@Resource
	private PersonService personService;

	public Map<String, Object> getPersonList(String searchWord,String sourceId, String statusType, int page, int pageSize) {
		Map<String, Object> mapall = new HashMap<>();
		List<Map<String, Object>> ls = new ArrayList<>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		long numall = 0;
		try {
			conn = DataSource.getConnection();
			sql = "SELECT count(*) from plat_DictD dd,wt_ResDict_Ref refd,wt_Person pers "
					+ " WHERE refd.dictMid = '10' and dd.id = refd.dictDid and pers.id = refd.resId and refd.resTableName = 'wt_Person'";
			if (sourceId!=null) {
				sql += " and pers.pSource = '"+sourceId+"'";
			}
			if (statusType!=null) {
				sql += " and dd.id = '"+statusType+"'";
			}
			if (searchWord!=null) {
				sql += " and (pers.id LIKE '%"+searchWord+"%' OR pers.pName LIKE '%"+searchWord+"%' OR pers.phoneNum LIKE '%"+searchWord+"%')";
			}
			sql += " ORDER BY pers.cTime DESC"
			+" LIMIT "+(page-1)*pageSize+","+(page*pageSize);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				numall = rs.getLong(1);
			}
			if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
            
			sql = "SELECT pers.*,dd.id did,dd.ddName from plat_DictD dd,wt_ResDict_Ref refd,wt_Person pers "
					+ " WHERE refd.dictMid = '10' and dd.id = refd.dictDid and pers.id = refd.resId and refd.resTableName = 'wt_Person'";
			if (sourceId!=null) {
				sql += " and pers.pSource = '"+sourceId+"'";
			}
			if (statusType !=null) {
				sql += " and dd.id = '"+statusType+"'";
			}
			if (searchWord!=null) {
				sql += " and (pers.id LIKE '%"+searchWord+"%' OR pers.pName LIKE '%"+searchWord+"%' OR pers.phoneNum LIKE '%"+searchWord+"%')";
			}
			sql += " ORDER BY pers.cTime DESC"
			+" LIMIT "+(page-1)*pageSize+","+(page*pageSize);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				Map<String, Object> m = new HashMap<>();
				m.put("PersonId", rs.getString("id"));
				m.put("PersonName", rs.getString("pName"));
				m.put("PersonSource", rs.getString("pSource"));
				m.put("PhoneNum", rs.getString("phoneNum"));
				m.put("RealName", rs.getString("pName"));
				m.put("IDNumber", "479XXXX456XXXXX456XXX");
				m.put("PersonStatusId", rs.getString("did"));
				m.put("PersonStatus", rs.getString("ddName"));
				m.put("PersonImg", rs.getString("portrait"));
				m.put("CTime", rs.getTimestamp("cTime"));
				ls.add(m);
			}
			if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
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

	public List<Map<String, Object>> updatePersonStatus(String personIds, String statusType) {
		String[] pIds = personIds.split(",");
		List<Map<String, Object>> ls = new ArrayList<>();
		if (pIds.length>0) {
			for (String pId : pIds) {
				Map<String, Object> map = new HashMap<>();
				map.put("dictMid", "10");
				map.put("resId", pId);
				DictRefResPo dPo = dictContentService.getDictRefResInfo(map);
				if (dPo!=null) {
					dPo.setDictDid(statusType);
					dictContentService.updataDictRefInfo(dPo);
				} else {
					Map<String, Object> m = new HashMap<>();
					m.put("PersonId", pId);
					m.put("NewStatus", statusType);
					m.put("Message", "对应关系不存在");
					ls.add(m);
				}
			}
		}
		if (ls.size()>0) {
			return ls;
		}
		return null;
	}

	public Map<String, Object> getPersonContentList(String personId, String mediaType, int sortType, int page, int pageSize) {
		Map<String, Object> mapall = new HashMap<>();
		List<PersonRefPo> perfs = new ArrayList<>();
		List<Map<String, Object>> ls = new ArrayList<>();
		long numall = 0;
		if (mediaType.equals("SEQU")) perfs = personService.getPersonRefByPIdAndMediaType(personId, "wt_SeqMediaAsset");
		else if (mediaType.equals("AUDIO")) perfs = personService.getPersonRefByPIdAndMediaType(personId, "wt_MediaAsset");
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		String ids = "";
		try {
			if (perfs!=null && perfs.size()>0) {
				numall = perfs.size();
				for (PersonRefPo personRefPo : perfs) {
					ids += " or persf.resId = '"+personRefPo.getResId()+"'";
				}
				if (sortType==1) {
					conn = DataSource.getConnection();
				     sql = "SELECT res.*,mapc.playCount FROM"
				     		+ " (SELECT perf.personId,perf.resId,perf.resTableName,"
				     		+ " (CASE perf.resTableName WHEN 'wt_MediaAsset' THEN ma.maTitle WHEN 'wt_SeqMediaAsset' THEN sma.smaTitle END) title, "
				     		+ " (CASE perf.resTableName WHEN 'wt_MediaAsset' THEN ma.maImg WHEN 'wt_SeqMediaAsset' THEN sma.smaImg END) img,"
				     		+ " (CASE perf.resTableName WHEN 'wt_MediaAsset' THEN ma.maPublisher WHEN 'wt_SeqMediaAsset' THEN sma.smaPublisher END) publisher,"
				     		+ " (CASE perf.resTableName WHEN 'wt_MediaAsset' THEN ma.descn WHEN 'wt_SeqMediaAsset' THEN sma.descn END) descn,"
				     		+ " (CASE perf.resTableName WHEN 'wt_MediaAsset' THEN ma.cTime WHEN 'wt_SeqMediaAsset' THEN sma.cTime END) cTime"
				     		+ " FROM wt_Person_Ref perf LEFT JOIN wt_MediaAsset ma ON ma.id = perf.resId and perf.resTableName = 'wt_MediaAsset'"
				     		+ " LEFT JOIN wt_SeqMediaAsset sma ON sma.id = perf.resId and perf.resTableName = 'wt_SeqMediaAsset'"
				     		+ " where perf.personId = '"+personId+"' and perf.resTableName = "+(mediaType.equals("SEQU")?"'wt_SeqMediaAsset'":"'wt_MediaAsset'")+") res"
				     		+ " ,wt_MediaPlayCount mapc"
				     		+ " where mapc.resId = res.resId and  mapc.resTableName = res.resTableName"
				     		+ " ORDER BY mapc.playCount DESC "
				            + " LIMIT "+(page-1)*pageSize+","+(page*pageSize);
				     ps = conn.prepareStatement(sql);
				     rs = ps.executeQuery();
				     while (rs != null && rs.next()) {
				    	 Map<String, Object> m = new HashMap<>();
				    	 m.put("ContentId", rs.getString("resId"));
				    	 m.put("ContentName", rs.getString("title"));
				    	 m.put("ContentImg", rs.getString("img"));
				    	 m.put("ContentPlayCount", rs.getLong("playCount"));
				    	 m.put("ContentPublisher", rs.getString("publisher"));
//				    	 m.put("ContentPlay", rs.getString("playURI"));
				    	 m.put("ContentDesc", rs.getString("descn"));
				    	 m.put("CTime", rs.getTimestamp("cTime"));
				    	 ls.add(m);
				     }
				     
				     if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
			         if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
			         
			         if (mediaType.equals("AUDIO")) {
			           	sql = "SELECT s.*,mas.playURI FROM "
								+ "(SELECT sma.id,sma.smaTitle,smaf.mId from wt_SeqMediaAsset sma,wt_SeqMA_Ref smaf where sma.id = smaf.sId and ("+ids.replace("persf.resId", "smaf.mId").substring(3)+")) s"
								+ " LEFT JOIN wt_MaSource mas"
								+ " ON s.mId = mas.maId and mas.isMain = 1";
						ps = conn.prepareStatement(sql);
						rs = ps.executeQuery();
						while (rs != null && rs.next()) {
							for (Map<String, Object> map : ls) {
								if (map.get("ContentId").equals(rs.getString("mId"))) {
									map.put("ContentPlayUrl", rs.getString("playURI"));
									map.put("ContentSeqId", rs.getString("id"));
									map.put("ContentSeqName", rs.getString("smaTitle"));
								}
							}
						}
						if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
				        if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
					} else if (mediaType.equals("SEQU")) {
						sql = "SELECT sId,COUNT(*) num from wt_SeqMA_Ref "
								+ "where "+ids.replace("persf.resId", "sId").substring(3)
								+ " GROUP BY sId";
						ps = conn.prepareStatement(sql);
						rs = ps.executeQuery();
						while (rs != null && rs.next()) {
							for (Map<String, Object> map : ls) {
								if (map.get("ContentId").equals(rs.getString("sId"))) {
									map.put("MediaSize", rs.getString("num"));
								}
							}
						}
						if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
				        if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
					}
			    } else {
			    	sql = "SELECT DISTINCT ch.id,(CASE ch.assetType WHEN 'wt_MediaAsset' then ma.maPublisher when 'wt_SeqMediaAsset' then sma.smaPublisher end) publisher,"
							+ "(CASE ch.assetType WHEN 'wt_MediaAsset' then ma.descn when 'wt_SeqMediaAsset' then sma.descn end) descn,"
							+ "(CASE ch.assetType WHEN 'wt_MediaAsset' then ma.id when 'wt_SeqMediaAsset' then sma.id end) resId,"
							+ "(CASE ch.flowFlag WHEN 2 then ch.pubTime ELSE ch.cTime end) time,"
							+ " ch.assetId,c.channelName,ch.sort,ch.channelId,ch.flowFlag,ch.publisherId,ch.pubTime,ch.assetType,ch.pubName,ch.pubImg,per.id personId,per.pName "
							+ " from wt_ChannelAsset ch "
							+ " LEFT JOIN wt_Person_Ref perf "
							+ " ON ch.assetId = perf.resId and ch.assetType = perf.resTableName "
							+ " LEFT JOIN wt_Person per "
							+ " on perf.personId = per.Id "
							+ " LEFT JOIN wt_Channel c "
							+ " ON ch.channelId = c.id "
							+ " LEFT JOIN wt_MediaAsset ma "
							+ " ON ch.assetId = ma.id and ch.assetType = 'wt_MediaAsset' "
							+ " LEFT JOIN wt_SeqMediaAsset sma "
							+ " ON ch.assetId = sma.id and ch.assetType = 'wt_SeqMediaAsset' "
							+ " where ";
					if (mediaType!=null) {
						if (mediaType.equals("SEQU")) sql += " ch.assetType = 'wt_SeqMediaAsset'";
						else if(mediaType.equals("AUDIO")) sql += " ch.assetType = 'wt_MediaAsset'";
					}
					sql += " and "+ids.replace("persf", "cha").substring(3)
							+ " ORDER BY ch.sort DESC, ch.pubTime DESC LIMIT "+(page-1)*pageSize+","+(page*pageSize);
					try {
						rs = ps.executeQuery();
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
							oneDate.put("PersonId", rs.getString("personId"));
							oneDate.put("PersonName", rs.getString("pName"));
							oneDate.put("ContentPubTime", rs.getString("pubTime"));
							oneDate.put("MediaSize", 1);
							ls.add(oneDate);
						}
						if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
			            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
					} catch (Exception e) {}
			    }
				sql = "SELECT ch.id,cha.assetId,cha.pubName,ch.channelName,cha.flowFlag,cha.pubTime from wt_ChannelAsset cha LEFT JOIN wt_Channel ch ON cha.channelId = ch.id "
						+ " where ("+ids.replace("persf.resId", "cha.assetId").substring(3)
						+ ") and  cha.assetType ="+(mediaType.equals("SEQU")?"'wt_SeqMediaAsset'":"'wt_MediaAsset'")
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
							map.put("ContentPubTime", rs.getTimestamp("pubTime"));
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
						+ " and ("+ids.replace("persf.resId", "resd.resId").substring(3)
						+ " ) and  resd.resTableName ="+(mediaType.equals("SEQU")?"'wt_SeqMediaAsset'":"'wt_MediaAsset'")
						+ " ORDER BY resd.cTime asc";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					for (Map<String, Object> map : ls) {
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
				
				sql = "SELECT kws.id,kws.kwName,kwsf.resId from wt_KeyWord kws LEFT JOIN wt_Kw_Res kwsf ON kws.id = kwsf.kwId "
						+ "where "+ids.replace("persf", "kwsf").substring(3)
						+ " order by kwsf.cTime ASC";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					for (Map<String, Object> map : ls) {
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
				mapall.put("List", ls);
				mapall.put("Count", numall);
				return mapall;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, Object> getPersonInfo(String personId) {
		PersonPo po = personService.getPersonPoById(personId);
		if (po!=null) {
			Map<String, Object> m = new HashMap<>();
			int smanum = personService.getPersonRefsByPIdAndResTableName(personId, "wt_SeqMediaAsset");
			int manum = personService.getPersonRefsByPIdAndResTableName(personId, "wt_MediaAsset");
			m.put("PersonId", personId);
			m.put("PersonNick", po.getpName());
			m.put("PersonName", po.getpName());
			m.put("SeqMediaSize", smanum);
			m.put("MediaSize", manum);
			m.put("PersonAge", po.getAge());
			m.put("PersonPhone", po.getPhoneNum());
			m.put("PersonImg", po.getPortrait());
			m.put("PersonEmail", po.getEmail());
			m.put("PersonDesc", po.getDescn());
			m.put("PersonSign", po.getDescn());
			m.put("PersonAddress", "北京");
			m.put("PostCode", "100000");
			m.put("IDNumber", "478XX645X56622X4X454X5");
			m.put("IDImg", null);
			m.put("CTime", po.getcTime());
			m.put("Publisher", po.getpSource());
			return m;
		}
		return null;
	}

}
