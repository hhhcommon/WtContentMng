package com.woting.content.listquery.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy(true)
@Service
public class QueryService {
	@Resource
	private DataSource DataSource;

	public List<Map<String, Object>> querylist(int flowFlag, int currentpage, int pagesize) {
		List<Map<String, Object>> list2seq = new ArrayList<Map<String, Object>>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int count = 0;
		String sql = "select id,channelId,assetType,assetId,pubImg,cTime from wt_ChannelAsset where flowFlag=? group by sort desc";
		try {
			conn = DataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, flowFlag);
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				Map<String, Object> oneData = new HashMap<String, Object>();
				oneData.put("id", rs.getString("id"));
				oneData.put("channelId", rs.getString("channelId"));
				oneData.put("actType", rs.getString("assetType"));
				oneData.put("assetId", rs.getString("assetId"));
				oneData.put("actThunb", rs.getString("pubImg"));
				oneData.put("cTime", rs.getTimestamp("cTime"));
				oneData.put("actDesn", "");
				list2seq.add(oneData);
				count++;
				System.out.println(oneData + "####" + count);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
        }

		// 栏目表查询
		sql = "select channelName from wt_Channel where id=?";
		for (Map<String, Object> map : list2seq) {
			try {
				conn = DataSource.getConnection();
				ps = conn.prepareStatement(sql);
				ps.setString(1, (String) map.get("assetId"));
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					map.put("actTitle",rs.getString("channelName"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally {
	            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
	            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
	        } 
		}
		return list2seq;
	}
}
