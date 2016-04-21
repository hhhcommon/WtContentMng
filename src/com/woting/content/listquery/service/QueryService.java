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
	private Connection conn = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;

	public List<Map<String, Object>> queryList(int flowFlag, int currentpage, int pagesize) {
		List<Map<String, Object>> list2seq = new ArrayList<Map<String, Object>>();
		int count = 0;
		String sql = "select id,channelId,assetType,assetId,pubImg,cTime from wt_ChannelAsset where flowFlag=? order by sort desc";
		try {
			conn = DataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, flowFlag);
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				Map<String, Object> oneData = new HashMap<String, Object>();
				oneData.put("Id", rs.getString("id"));
				oneData.put("ChannelId", rs.getString("channelId"));
				oneData.put("ActType", rs.getString("assetType"));
				oneData.put("AssetId", rs.getString("assetId"));
				oneData.put("ActThunb", rs.getString("pubImg"));
				oneData.put("CTime", rs.getTimestamp("cTime"));
				oneData.put("ActDesn", "");
				list2seq.add(oneData);
				count++;
				System.out.println(oneData + "####" + count);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
					rs = null;
				} catch (Exception e) {
					rs = null;
				} finally {
					rs = null;
				}
			;
			if (ps != null)
				try {
					ps.close();
					ps = null;
				} catch (Exception e) {
					ps = null;
				} finally {
					ps = null;
				}
			;
			if (conn != null)
				try {
					conn.close();
					conn = null;
				} catch (Exception e) {
					conn = null;
				} finally {
					conn = null;
				}
			;
		}

		// 栏目表查询
		sql = "select channelName from wt_Channel where id=?";
		for (Map<String, Object> map : list2seq) {
			try {
				conn = DataSource.getConnection();
				ps = conn.prepareStatement(sql);
				ps.setString(1, (String) map.get("AssetId"));
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					map.put("ActTitle", rs.getString("channelName"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (rs != null)
					try {
						rs.close();
						rs = null;
					} catch (Exception e) {
						rs = null;
					} finally {
						rs = null;
					}
				;
				if (ps != null)
					try {
						ps.close();
						ps = null;
					} catch (Exception e) {
						ps = null;
					} finally {
						ps = null;
					}
				;
				if (conn != null)
					try {
						conn.close();
						conn = null;
					} catch (Exception e) {
						conn = null;
					} finally {
						conn = null;
					}
				;
			}
		}
		return list2seq;
	}

	public Map<String, Object> queryDetail(int pagesize, int page, String id, String acttype) {
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
	 * @param acttype
	 * @return
	 */
	public Map<String, Object> getSeq(int pagesize, int page, String id, String acttype) {
		String sql = "select id,smaTitle,smaImg,smaAllCount,smaPublisher,keyWords,descn,CTime,smaPublishTime,smaPubId from wt_SeqMediaAsset where id = ?";
		List<Map<String, Object>> listaudio = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> seqData = new HashMap<String, Object>();// 存放专辑信息
		try {
			conn = DataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();
			int count = 0;
			while (rs != null && rs.next()) {
				seqData.put("Id", rs.getString("id"));
				seqData.put("ActTitle", rs.getString("smaTitle"));
				seqData.put("ActType", acttype);
				seqData.put("ActThumb", rs.getString("smaImg"));
				seqData.put("ActAllCount", rs.getString("smaAllCount"));
				seqData.put("ActPubTime", rs.getTimestamp("smaPublishTime"));
				seqData.put("ActSource", rs.getString("smaPubId"));
				seqData.put("ActCTime", rs.getTimestamp("CTime"));
				seqData.put("ActVjName", rs.getString("smaPublisher"));
				seqData.put("KeyWords", rs.getString("keyWords"));
				seqData.put("ActDesn", rs.getString("descn"));
				count++;
				System.out.println(seqData);
			}
			System.out.println(count);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
        }

		// 专辑和单体的联系
		sql = "select sId,mId from wt_SeqMA_Ref where sid = ?";
		List<String> listaudioid = new ArrayList<String>();
		try {
			conn = DataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, (String) seqData.get("Id"));
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

		// 获取单体信息
		sql = "select id,maTitle,maPubId,maPublishTime,maImg,timeLong,maPublisher,descn,cTime from wt_MediaAsset  where id = ?";
		for (String audid : listaudioid) {
			try {
				conn = DataSource.getConnection();
				ps = conn.prepareStatement(sql);
				ps.setString(1, audid);
				rs = ps.executeQuery();
				Map<String, Object> audioData = new HashMap<String, Object>();// 单体信息
				while (rs != null && rs.next()) {
					audioData.put("Id", rs.getString("id"));
					audioData.put("ItemName", rs.getString("maTitle"));
					audioData.put("ItemType", acttype);
					audioData.put("ItemThumb", rs.getString("maImg"));
					audioData.put("ItemCTime", rs.getTimestamp("cTime"));
					audioData.put("ItemPubTime", rs.getTimestamp("maPublishTime"));
					audioData.put("ItemDesc", rs.getString("descn"));
					audioData.put("ItemLong", rs.getLong("timeLong"));
					audioData.put("ItemSource", rs.getString("maPubId"));
					audioData.put("ItemVjName", rs.getString("maPublisher"));
					System.out.println(audioData);
				}
				listaudio.add(audioData);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
	            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
	            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
	        }
		}
		
		//获得专辑字典信息
		sql = "select dictMName from wt_ResDict_Ref where resId = ?";
		for (Map<String, Object> audmap : listaudio) {
			try {
				conn = DataSource.getConnection();
				ps = conn.prepareStatement(sql);
				ps.setString(1, (String) seqData.get("Id"));
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					seqData.put("ActColumn", rs.getString("dictMName"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
	            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
	            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
	        }
		}

		// 获得单体字典名
		sql = "select dictMName from wt_ResDict_Ref where resId = ?";
		for (Map<String, Object> audmap : listaudio) {
			try {
				conn = DataSource.getConnection();
				ps = conn.prepareStatement(sql);
				ps.setString(1, (String) audmap.get("Id"));
				rs = ps.executeQuery();
				while (rs != null && rs.next()) {
					audmap.put("ItemColumn", rs.getString("dictMName"));
				}
				System.out.println(listaudioid.size());
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
	            if (rs!=null) try {rs.close();rs=null;} catch(Exception e) {rs=null;} finally {rs=null;};
	            if (ps!=null) try {ps.close();ps=null;} catch(Exception e) {ps=null;} finally {ps=null;};
	            if (conn!=null) try {conn.close();conn=null;} catch(Exception e) {conn=null;} finally {conn=null;};
	        }
		}
		if(listaudio.size()==0){
			map.put("audio", null);
			map.put("count", 0);
		} else {
			map.put("audio", listaudio);
			map.put("count", listaudio.size());
		}
		map.put("sequ", seqData);
		return map;
	}

	public Map<String, Object> getAudio(String id, String acttype) {
		return null;
	}
}
