package com.woting.content.publish.service;

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

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy(true)
@Service
public class QueryService {
	@Resource
	private DataSource DataSource;

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
	public Map<String, Object> getContent(int flowFlag, int page, int pagesize, String catalogsid, String source,
			Timestamp beginpubtime, Timestamp endpubtime, Timestamp beginctime, Timestamp endctime) {
		Map<String, Object> mapall = new HashMap<String, Object>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Map<String, Object>> list2seq = new ArrayList<Map<String, Object>>();
		String numall = null;
		
		// 查询需要显示的节目数目
		String sql = "select count(id) num from wt_ChannelAsset where flowFlag=?";
		if (catalogsid != null)
			sql += " and channelId='" + catalogsid + "'";
		if (source != null)
			sql += " and publisherId='" + source + "'";
		if (beginpubtime != null && endpubtime != null)
			sql += " and pubTime>'" + beginpubtime + "' and pubTime<'" + endpubtime + "'";
		if (beginctime != null && endctime != null)
			sql += " and cTime>'" + beginctime + "' and cTime<'" + endctime + "'";
		try {
			conn = DataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, flowFlag);
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				numall = rs.getString("num");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(conn, ps, rs);
		}
		
		// 按条件查询需要显示的节目
		sql = "select id,assetType,assetId,pubImg,cTime,sort,flowFlag,pubTime from wt_ChannelAsset where flowFlag=?";
		if (catalogsid != null)
			sql += " and channelId='" + catalogsid + "'";
		if (source != null)
			sql += " and publisherId='" + source + "'";
		if (beginpubtime != null && endpubtime != null)
			sql += " and pubTime>'" + beginpubtime + "' and pubTime<'" + endpubtime + "'";
		if (beginctime != null && endctime != null)
			sql += " and cTime>'" + beginctime + "' and cTime<'" + endctime + "'";
		sql += " order by sort desc,pubTime desc limit ?,?";
		try {
			conn = DataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, flowFlag);
			ps.setInt(2, (page - 1) * pagesize);
			ps.setInt(3, pagesize);
			ps.setQueryTimeout(10000);
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				Map<String, Object> oneData = new HashMap<String, Object>();
				oneData.put("Id", rs.getString("id"));// 栏目ID修改排序功能时使用
				oneData.put("MediaType", rs.getString("assetType"));
				oneData.put("ContentId", rs.getString("assetId"));// 内容ID获得详细信息时使用
				System.out.println(rs.getString("assetId"));
				oneData.put("ContentImg", rs.getString("pubImg"));
				oneData.put("ContentCTime", rs.getTimestamp("cTime"));
				oneData.put("ContentPubTime", rs.getTimestamp("pubTime"));
				oneData.put("ContentSort", rs.getString("sort"));
				oneData.put("ContentFlowFlag", rs.getString("flowFlag"));
				list2seq.add(oneData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(conn, ps, rs);
		}
		
		// 查询显示的节目名称，发布组织和描述信息
		for (Map<String, Object> map : list2seq) {
			if (map.get("MediaType").equals("wt_SeqMediaAsset")) {
				sql = "select smaTitle,smaPublisher,descn from wt_SeqMediaAsset where id = ? limit 1";
				try {
					conn = DataSource.getConnection();
					ps = conn.prepareStatement(sql);
					ps.setString(1, (String) map.get("ContentId"));
					rs = ps.executeQuery();
					while (rs != null && rs.next()) {
						map.put("ContentName", rs.getString("smaTitle"));
						map.put("ContentSource", rs.getString("smaPublisher"));
						map.put("ContentDesc", rs.getString("descn"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					closeConnection(conn, ps, rs);
				}
			} else {
				if (map.get("MediaType").equals("wt_MediaAsset")) {
					sql = "select maTitle,maPublisher,descn from wt_MediaAsset where id = ? limit 1";
					try {
						conn = DataSource.getConnection();
						ps = conn.prepareStatement(sql);
						ps.setString(1, (String) map.get("ContentId"));
						rs = ps.executeQuery();
						while (rs != null && rs.next()) {
							map.put("ContentName", rs.getString("maTitle"));
							map.put("ContentSource", rs.getString("maPublisher"));
							map.put("ContentDesc", rs.getString("descn"));
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						closeConnection(conn, ps, rs);
					}
				} else {
					if (map.get("MediaType").equals("wt_Broadcast")) {
						sql = "select bcTitle,bcPublisher,descn from wt_Broadcast where id = ? limit 1";
						try {
							conn = DataSource.getConnection();
							ps = conn.prepareStatement(sql);
							ps.setString(1, (String) map.get("ContentId"));
							rs = ps.executeQuery();
							while (rs != null && rs.next()) {
								map.put("ContentName", rs.getString("bcTitle"));
								map.put("ContentSource", rs.getString("bcPublisher"));
								map.put("ContentDesc", rs.getString("descn"));
							}
						} catch (SQLException e) {
							e.printStackTrace();
						} finally {
							closeConnection(conn, ps, rs);
						}
					}
				}
			}
		}
		mapall.put("List", list2seq);
		mapall.put("Count", numall);
		return mapall;
	}

	/**
	 * 查询已显示的节目信息
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
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select a.id,a.smaTitle,a.smaImg,a.smaAllCount,a.smaPublisher,a.keyWords,a.descn,a.CTime,a.smaPublishTime,b.title from wt_SeqMediaAsset a,wt_ResDict_Ref b where a.id = ? and a.id = b.resId limit 1";
		List<Map<String, Object>> listaudio = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> seqData = new HashMap<String, Object>();// 存放专辑信息
		try {
			conn = DataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				seqData.put("ContentId", rs.getString("id"));
				seqData.put("ContentName", rs.getString("smaTitle"));
				seqData.put("MediaType", acttype);
				seqData.put("ContentImg", rs.getString("smaImg"));
				seqData.put("ContentSubCount", rs.getString("smaAllCount"));
				seqData.put("ContentPubTime", rs.getTimestamp("smaPublishTime"));
				seqData.put("ContentSource", rs.getString("smaPublisher"));
				seqData.put("ContentCTime", rs.getTimestamp("CTime"));
				seqData.put("ContentPersons", null);
				seqData.put("ContentKeyWord", rs.getString("keyWords"));
				seqData.put("ContentCatalogs", rs.getString("title"));
				seqData.put("ContentDesc", rs.getString("descn"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(conn, ps, rs);
		}

		// 查询专辑和单体的联系
		sql = "select sId,mId from wt_SeqMA_Ref where sid = ? limit 1";
		List<String> listaudioid = new ArrayList<String>();
		try {
			conn = DataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, (String) seqData.get("ContentId"));
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				listaudioid.add(rs.getString("mId"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(conn, ps, rs);
		}

		// 查询单体信息
		for (String audid : listaudioid) {
			listaudio.add(getAudioInfo(audid, acttype));
		}

		if (listaudio.size() == 0) {
			map.put("audio", null);
			map.put("count", 0);
		} else {
			map.put("audio", listaudio);
			map.put("count", listaudio.size());
		}
		map.put("sequ", seqData);
		return map;
	}

	/**
	 * 查询单体信息
	 * 
	 * @param contentid
	 * @param acttype
	 * @return
	 */
	public Map<String, Object> getAudioInfo(String contentid, String acttype) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, Object> audioData = new HashMap<String, Object>();// 单体信息
		String sql = "select a.id,a.maTitle,a.maPublishTime,a.maImg,a.timeLong,a.maPublisher,a.descn,a.cTime,b.title from wt_MediaAsset a,wt_ResDict_Ref b where a.id = ? and a.id = b.resId limit 1";
		try {
			conn = DataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, contentid);
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				audioData.put("ContentId", rs.getString("id"));
				audioData.put("ContentName", rs.getString("maTitle"));
				audioData.put("MediaType", acttype);
				audioData.put("ContentImg", rs.getString("maImg"));
				audioData.put("ContentCTime", rs.getTimestamp("cTime"));
				audioData.put("ContentPubTime", rs.getTimestamp("maPublishTime"));
				audioData.put("ContentDesc", rs.getString("descn"));
				audioData.put("ContentTimes", rs.getLong("timeLong"));
				audioData.put("ContentSource", rs.getString("maPublisher"));
				audioData.put("ContentPersons", null);
				audioData.put("ContentCatalogs", rs.getString("title"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(conn, ps, rs);
		}
		return audioData;
	}

	/**
	 * 查询电台信息
	 * 
	 * @param contentid
	 * @param acttype
	 * @return
	 */
	public Map<String, Object> getBroadcastInfo(String contentid, String acttype) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, Object> broadcastData = new HashMap<String, Object>();// 单体信息
		String sql = "select id,bcTitle,bcImg,bcPublisher,descn,cTime from wt_Broadcast where id = ? limit 1";
		try {
			conn = DataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, contentid);
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				broadcastData.put("ContentId", rs.getString("id"));
				broadcastData.put("ContentName", rs.getString("bcTitle"));
				broadcastData.put("MediaType", acttype);
				broadcastData.put("ContentImg", rs.getString("bcImg"));
				broadcastData.put("ContentCTime", rs.getTimestamp("cTime"));
				broadcastData.put("ContentDesc", rs.getString("descn"));
				broadcastData.put("ContentSource", rs.getString("bcPublisher"));
				broadcastData.put("ContentPersons", null);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(conn, ps, rs);
		}
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
	public Map<String, Object> modifyInfo(String id, String number, int flowFlag, String OpeType) {
		Map<String, Object> map = new HashMap<String, Object>();
		switch (OpeType) {
		case "sort":
			map = modifySort(id, number); // 修改排序号
			break;
		case "pass":
			number = "2";
			map = modifyStatus(id, number); // 修改审核状态为通过
			break;
		case "nopass":
			number = "3";
			map = modifyStatus(id, number); // 修改审核状态为未通过
			break;
		case "revoke":
			number = "4";
			map = modifyStatus(id, number); // 修改审核状态为撤回
			break;
		default:
			break;
		}
		return map;
	}

	/**
	 * 修改审核状态
	 * 
	 * @param id
	 * @param number
	 * @return
	 */
	public Map<String, Object> modifyStatus(String id, String number) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, Object> map = new HashMap<String, Object>();
		String sql = "update wt_ChannelAsset set flowFlag = ? where id = ?";
		id = id.replaceAll("%2C", ",");
		id = id.substring(0, id.length() - 1);
		String[] ids = id.split(",");
		int num = 0;
		for (int i = 0; i < ids.length; i++) {
			try {
				conn = DataSource.getConnection();
				ps = conn.prepareStatement(sql);
				ps.setInt(1, Integer.valueOf(number));
				ps.setString(2, ids[i]);
				num = ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				closeConnection(conn, ps, rs);
			}
		}
		if (num == 1) {
			map.put("ReturnType", "1001");
		} else {
			map.put("ReturnType", "1011");
			map.put("Message", "修改失败");
		}
		return map;
	}

	/**
	 * 修改排序号
	 * @param id
	 * @param sort
	 * @return
	 */
	public Map<String, Object> modifySort(String id, String sort) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, Object> map = new HashMap<String, Object>();
		int num = 0;
		String sql = "update wt_ChannelAsset set sort = ? where id = ?";
		try {
			conn = DataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, Integer.valueOf(sort));
			ps.setString(2, id);
			num = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(conn, ps, rs);
		}
		if (num == 1) {
			map.put("ReturnType", "1001");
		} else {
			map.put("ReturnType", "1011");
			map.put("Message", "修改失败");
		}
		return map;
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

	/**
	 * 关闭数据库连接
	 * @param conn
	 * @param ps
	 * @param rs
	 */
	private void closeConnection(Connection conn, PreparedStatement ps, ResultSet rs) {
		if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
        if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
        if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
	}
}
