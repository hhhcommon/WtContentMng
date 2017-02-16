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
		List<PersonRefPo> perfs = new ArrayList<>();
		List<Map<String, Object>> ls = new ArrayList<>();
		long all = 0;
		if (mediaType.equals("SEQU")) perfs = personService.getPersonRefByPIdAndMediaType(personId, "wt_SeqMediaAsset");
		else if (mediaType.equals("AUDIO")) perfs = personService.getPersonRefByPIdAndMediaType(personId, "wt_MediaAsset");
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		String maIds = "";
		String smaIds = "";
		try {
			if (perfs!=null && perfs.size()>0) {
				all = perfs.size();
				for (PersonRefPo personRefPo : perfs) {
					if (mediaType.equals("AUDIO")) maIds += " or ma.id = '"+personRefPo.getResId()+"'";
					else if(mediaType.equals("SEQU")) smaIds += " or sma.id = '"+personRefPo.getResId()+"'";
				}
				if (sortType==1) {
					conn = DataSource.getConnection();
				     sql = "SELECT ret.*,mas.playURI FROM"
				     		+ " (SELECT * FROM (SELECT ma.id,ma.maTitle,ma.maPublisher,ma.maImg,ma.descn,ma.cTime ,CASE mapc.playCount WHEN NULL THEN 0 ELSE mapc.playCount END playCount from wt_MediaAsset ma "
				     		+ " LEFT JOIN wt_MediaPlayCount mapc ON mapc.resId = ma.id and mapc.resTableName = 'wt_MediaAsset'"
				     		+ " where ("+ maIds.substring(3) +")"
				     		+ " ORDER BY playCount DESC) res"
				     		+ " GROUP BY res.id ) ret"
				     		+ " LEFT JOIN wt_MaSource mas"
				     		+ " ON mas.maId = ret.id and mas.isMain = 1"
				     		+ " ORDER BY ret.playCount DESC"
				            + " LIMIT "+(page-1)*pageSize+","+(page*pageSize);
				     ps = conn.prepareStatement(sql);
				     rs = ps.executeQuery();
				     while (rs != null && rs.next()) {
				    	 Map<String, Object> m = new HashMap<>();
				    	 m.put("ContentId", rs.getString("id"));
				    	 m.put("ContentName", rs.getString("maTitle"));
				    	 m.put("ContentImg", rs.getString("maImg"));
				    	 m.put("ContentPlayCount", rs.getLong("playCount"));
				    	 m.put("ContentPublisher", rs.getString("maPublisher"));
				    	 m.put("ContentPlay", rs.getString("playURI"));
				    	 m.put("CTime", rs.getTimestamp("cTime"));
				    	 ls.add(m);
				     }
				     
				     if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
			         if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
			         
//			         sql = "SELECT ch.id,cha.assetId,cha.pubName,ch.channelName,cha.flowFlag,cha.pubTime from wt_ChannelAsset cha LEFT JOIN wt_Channel ch ON cha.channelId = ch.id "
//								+ " where "+ids.replace("persf.resId", "cha.assetId")
//								+ " ORDER BY cha.pubTime asc";
//						ps = conn.prepareStatement(sql);
//						rs = ps.executeQuery();
//						while (rs != null && rs.next()) {
//							for (Map<String, Object> map : ls) {
//								if (map.get("ContentId").equals(rs.getString("assetId"))) {
//									Map<String, Object> chamap = new HashMap<>();
//									chamap.put("FlowFlag", rs.getInt("flowFlag"));
//									chamap.put("ChannelName", rs.getString("channelName"));
//									chamap.put("ChannelId", rs.getString("id"));
//									chamap.put("PubTime", rs.getTimestamp("pubTime"));
//									if (map.containsKey("ContentPubChannels")) {
//										List<Map<String, Object>> chas = (List<Map<String, Object>>) map.get("ContentPubChannels");
//										chas.add(chamap);
//									} else {
//										List<Map<String, Object>> chas = new ArrayList<>();
//										chas.add(chamap);
//										map.put("ContentPubChannels", chas);
//									}
//								}
//							}
//						}
//						if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
//			            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
//			            
//			            sql = "SELECT resd.resId,resd.dictMid,resd.dictDid,dd.ddName,resd.refName from wt_ResDict_Ref resd , plat_DictD dd where resd.dictDid = dd.id and resd.dictMid = '3' "
//								+ " and ("+ids.replace("persf.resId", "resd.resId")
//								+ ") ORDER BY resd.cTime asc";
//						ps = conn.prepareStatement(sql);
//						rs = ps.executeQuery();
//						while (rs != null && rs.next()) {
//							for (Map<String, Object> map : ls) {
//								if (map.get("ContentId").equals(rs.getString("resId"))) {
//									Map<String, Object> catamap = new HashMap<>();
//									catamap.put("CataDid", rs.getString("dictDid"));
//									catamap.put("CataMName", rs.getString("refName"));
//									catamap.put("CataTitle", rs.getString("ddName"));
//									catamap.put("CataMid", rs.getString("dictMid"));
//									if (map.containsKey("ContentCatalogs")) {
//										List<Map<String, Object>> catas = (List<Map<String, Object>>) map.get("ContentCatalogs");
//										catas.add(catamap);
//									} else {
//										List<Map<String, Object>> catas = new ArrayList<>();
//										catas.add(catamap);
//										map.put("ContentCatalogs", catas);
//									}
//								}
//							}
//						}
//						if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
//			            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
//			         
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return null;
	}

}
