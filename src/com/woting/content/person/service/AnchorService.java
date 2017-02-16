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
import com.woting.content.manage.dict.service.DictContentService;

public class AnchorService {
	@Resource(name = "dataSource")
	private DataSource DataSource;
	@Resource
	private DictContentService dictContentService;

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

}
