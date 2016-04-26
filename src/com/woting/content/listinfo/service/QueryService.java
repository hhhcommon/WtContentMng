package com.woting.content.listinfo.service;

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
	public Map<String, Object> getList(int flowFlag, int page, int pagesize, String catalogsid, String source,
			Timestamp beginpubtime, Timestamp endpubtime, Timestamp beginctime, Timestamp endctime) {
		Map<String, Object> mapall = new HashMap<String,Object>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Map<String, Object>> list2seq = new ArrayList<Map<String, Object>>();
		int count = 0;
		String numall = null;
		//查询需要显示的节目数目
		String sql = "select count(id) num from wt_ChannelAsset where flowFlag=?";
		if (catalogsid != null)sql += " and channelId='"+catalogsid+"'";
		if (source != null)sql += " and publisherId='"+source+"'";
		if (beginpubtime != null && endpubtime != null)sql += " and pubTime>'"+beginpubtime+"' and pubTime<'"+endpubtime+"'";
		if (beginctime != null && endctime != null)sql += " and cTime>'"+beginctime+"' and cTime<'"+endctime+"'";
		sql += " order by sort desc";
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
		}finally {
            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
        }
		//按条件查询需要显示的节目
		sql = "select a.id,a.assetType,a.assetId,a.pubImg,a.cTime,a.channelId,a.sort,a.publisherId,a.flowFlag,a.pubTime,a.pubName from (select id,assetType,assetId,pubImg,cTime,sort,publisherId,flowFlag,pubTime,pubName,channelId from wt_ChannelAsset where flowFlag=?";
		if (catalogsid != null)sql += " and channelId='"+catalogsid+"'";
		if (source != null)sql += " and publisherId='"+source+"'";
		if (beginpubtime != null && endpubtime != null)sql += " and pubTime>'"+beginpubtime+"' and pubTime<'"+endpubtime+"'";
		if (beginctime != null && endctime != null)sql += " and cTime>'"+beginctime+"' and cTime<'"+endctime+"'";
		sql += " order by sort desc) a limit ?,?";
		System.out.println(sql);
		try {
			conn = DataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, flowFlag);
			ps.setInt(2, (page - 1) * pagesize);
			ps.setInt(3, page * pagesize);
			ps.setQueryTimeout(10000);
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				Map<String, Object> oneData = new HashMap<String, Object>();
				oneData.put("Id", rs.getString("id"));// 栏目ID修改排序功能时使用
				oneData.put("MediaType", rs.getString("assetType"));
				oneData.put("ContentId", rs.getString("assetId"));// 内容ID获得详细信息时使用
				oneData.put("ContentImg", rs.getString("pubImg"));
				oneData.put("ContentCTime", rs.getTimestamp("cTime"));
				oneData.put("ContentPubTime", rs.getTimestamp("pubTime"));
				oneData.put("ContentSort", rs.getString("sort"));
				oneData.put("ContentFlowFlag", rs.getString("flowFlag"));
				count++;
				if (count <= pagesize)
					list2seq.add(oneData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
        }
		//查询显示的节目名称，发布组织和描述信息
		for (Map<String, Object> map : list2seq) {
			sql = "select smaTitle,smaPublisher,descn from wt_SeqMediaAsset where id = ?";
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
			}finally {
	            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
	            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
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
	public Map<String, Object> getListInfo(int pagesize, int page, String id, String acttype) {
		Map<String, Object> map = new HashMap<String, Object>();
		switch (acttype) {
		case "wt_SeqMediaAsset":
			map = getSeq(pagesize, page, id, acttype);
			break;
		case "wt_MediaAsset":
			break;
		case "wt_Broadcast":
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
	 *            内容id
	 * @param acttype
	 * @return
	 */
	public Map<String, Object> getSeq(int pagesize, int page, String id, String acttype) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select id,smaTitle,smaImg,smaAllCount,smaPublisher,keyWords,descn,CTime,smaPublishTime,smaPubId,langDid from wt_SeqMediaAsset where id = ?";
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
				seqData.put("ContentSource", rs.getString("smaPubId"));
				seqData.put("ContentCTime", rs.getTimestamp("CTime"));
				seqData.put("ContentPersons", null);
				seqData.put("ContentKeyWord", rs.getString("keyWords"));
				seqData.put("ContentCatalogs", rs.getString("langDid"));
				seqData.put("ContentDesc", rs.getString("descn"));
				System.out.println(seqData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
        }

		// 查询专辑和单体的联系
		sql = "select sId,mId from wt_SeqMA_Ref where sid = ?";
		List<String> listaudioid = new ArrayList<String>();
		try {
			conn = DataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, (String) seqData.get("ContentId"));
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				listaudioid.add(rs.getString("mId"));
			}
			System.out.println(listaudioid.size());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
        }

		// 查询单体信息
		sql = "select id,maTitle,maPubId,maPublishTime,maImg,timeLong,maPublisher,descn,cTime from wt_MediaAsset  where id = ?";
		for (String audid : listaudioid) {
			try {
				conn = DataSource.getConnection();
				ps = conn.prepareStatement(sql);
				ps.setString(1, audid);
				rs = ps.executeQuery();
				Map<String, Object> audioData = new HashMap<String, Object>();// 单体信息
				while (rs != null && rs.next()) {
					audioData.put("ContentId", rs.getString("id"));
					audioData.put("ContentName", rs.getString("maTitle"));
					audioData.put("MediaType", acttype);
					audioData.put("ContentImg", rs.getString("maImg"));
					audioData.put("ContentCTime", rs.getTimestamp("cTime"));
					audioData.put("ContentPubTime", rs.getTimestamp("maPublishTime"));
					audioData.put("ContentDesc", rs.getString("descn"));
					audioData.put("ContentTimes", rs.getLong("timeLong"));
					audioData.put("ContentSource", rs.getString("maPubId"));
					audioData.put("ContentPersons", rs.getString("maPublisher"));
					listaudio.add(audioData);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
	            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
	            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
	        }
		}

		// 查询专辑字典信息
		sql = "select dictMName from wt_ResDict_Ref where resId = ?";
		try {
			conn = DataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, (String) seqData.get("Id"));
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				seqData.put("ContentCatalogs", rs.getString("dictMName"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
        }

		// 查询单体字典名
		sql = "select dictMName from wt_ResDict_Ref where resId = ?";
		for (Map<String, Object> audmap : listaudio) {
			try {
				conn = DataSource.getConnection();
				ps = conn.prepareStatement(sql);
				ps.setString(1, (String) audmap.get("ContentId"));
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					audmap.put("ContentCatalogs", rs.getString("dictMName"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
	            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
	            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
	        }
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
	 * 修改显示节目排序号和审核状态
	 * @param id
	 * @param number
	 * @param flowFlag
	 * @param OpeType
	 * @return
	 */
	public Map<String, Object> modifInfo(String id, String number, int flowFlag, String OpeType) {
		Map<String, Object> map = new HashMap<String,Object>();
		switch (OpeType) {
		case "sort":
			map = modifSort(id, number, flowFlag); // 修改排序号
			break;
		case "pass":
			number = "2";
			map = modifStatus(id, number); // 修改审核状态为通过
			break;
		case "nopass":
			number = "3";
			map = modifStatus(id, number); // 修改审核状态为未通过
			break;
		case "revoke":
			number = "4";
			map = modifStatus(id, number); // 修改审核状态为撤回
			break;
		default:
			break;
		}
		return map;
	}

	/**
	 * 修改审核状态
	 * @param id
	 * @param number
	 * @return
	 */
	public Map<String, Object> modifStatus(String id, String number) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, Object> map = new HashMap<String, Object>();
		String sql = "update wt_ChannelAsset set flowFlag = ? where id = ?";
		id = id.replaceAll("%2C", ",");
		id = id.substring(0,id.length()-1);
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
	            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
	            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
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
	 * @param flowFlag
	 * @return
	 */
	public Map<String, Object> modifSort(String id, String sort, int flowFlag) {
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
			System.out.println(num);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
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
	public Map<String, Object> getCriteriaInfo() {
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
            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
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
            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
        }
		map.put("Catalogs", listcatalogs);
		map.put("Source", listorganize);
		return map;
	}
}
