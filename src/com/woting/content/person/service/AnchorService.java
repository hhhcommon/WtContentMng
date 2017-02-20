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
import com.woting.cm.core.media.service.MediaService;
import com.woting.cm.core.person.persis.po.PersonPo;
import com.woting.cm.core.person.persis.po.PersonRefPo;
import com.woting.cm.core.person.service.PersonService;
import com.woting.content.manage.dict.service.DictContentService;
import com.woting.content.manage.keyword.service.KeyWordProService;

public class AnchorService {
	@Resource(name = "dataSource")
	private DataSource DataSource;
	@Resource
	private DictContentService dictContentService;
	@Resource
	private PersonService personService;
	@Resource
	private KeyWordProService keyWordProService;
	@Resource
	private MediaService mediaService;

	public Map<String, Object> getPersonList(String searchWord,String sourceId, String statusType, int page, int pageSize) {
		Map<String, Object> mapall = new HashMap<>();
		List<Map<String, Object>> retLs = new ArrayList<>();
		int numall = 0;
		try {
            Map<String, Object> param = new HashMap<>();
            if (sourceId!=null) {
				param.put("pSource", sourceId);
			}
			if (statusType!=null) {
				param.put("dictDid", statusType);
			}
			if (searchWord!=null) {
				param.put("LikeByClause", "pers.id LIKE '%"+searchWord+"%' OR pers.pName LIKE '%"+searchWord+"%' OR pers.phoneNum LIKE '%"+searchWord+"%'");
			}
            numall = personService.getPersonsNum(param);

            param.put("OrderByClause", " pers.cTime DESC");
            param.put("LimitByClause", " LIMIT "+(page-1)*pageSize+","+(page*pageSize));
            List<Map<String, Object>> ls = personService.getPersons(param);
            if(ls!=null) {
            	for (Map<String, Object> map : ls) {
            		Map<String, Object> m = new HashMap<>();
    				m.put("PersonId", map.get("id"));
    				m.put("PersonName", map.get("pName"));
    				m.put("PersonSource", map.get("pSource"));
    				m.put("PhoneNum", map.get("phoneNum"));
    				m.put("RealName", map.get("pName"));
    				m.put("IDNumber", "479XXXX456XXXXX456XXX");
    				m.put("PersonStatusId", map.get("did"));
    				m.put("PersonStatus", map.get("ddName"));
    				m.put("PersonImg", map.get("portrait"));
    				m.put("CTime", map.get("cTime"));
    				retLs.add(m);
				}
            } else return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		mapall.put("List", retLs);
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

	@SuppressWarnings("unchecked")
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
				conn = DataSource.getConnection();
				if (sortType==1) {
					 ids = "";
			         Map<String, Object> param = new HashMap<>();
			         param.put("personId", personId);
			         param.put("mediaType", (mediaType.equals("SEQU")?"wt_SeqMediaAsset":"wt_MediaAsset"));
			         param.put("OrderByClause", "mapc.playCount DESC");
			         param.put("LimitByClause", (page-1)*pageSize+","+(page*pageSize));
			         List<Map<String, Object>> pls = personService.getPersonContents(param);
			         if (pls!=null && pls.size()>0) {
						for (Map<String, Object> pmp : pls) {
							 Map<String, Object> m = new HashMap<>();
					    	 m.put("ContentId", pmp.get("resId"));
					    	 m.put("ContentName", pmp.get("title"));
					    	 m.put("ContentImg", pmp.get("img"));
					    	 m.put("ContentPlayCount", pmp.get("playCount"));
					    	 m.put("ContentPublisher", pmp.get("publisher"));
					    	 m.put("ContentDesc", pmp.get("descn"));
					    	 m.put("CTime", pmp.get("cTime"));
					    	 ids += " or persf.resId = '"+pmp.get("resId")+"'";
					    	 ls.add(m);
						}
					}
			         
			         if (mediaType.equals("AUDIO")) {
			        	param.clear();
				        param.put("ids", ids.replace("persf.resId", "smaf.mId").substring(3));
				        List<Map<String, Object>> mals = mediaService.getMaPlayAndSeqNameByIds(param);
				        if (mals!=null) {
				        	for (Map<String, Object> map : ls) {
								for (Map<String, Object> mam : mals) {
									if (map.get("ContentId").equals(mam.get("mId"))) {
										map.put("ContentPlayUrl", mam.get("playURI"));
										map.put("ContentSeqId", mam.get("id"));
										map.put("ContentSeqName", mam.get("smaTitle"));
									}
								}
							}
						}
					} else if (mediaType.equals("SEQU")) {
				        param.clear();
				        param.put("ids", ids.replace("persf.resId", "sId").substring(3));
				        List<Map<String, Object>> smas = mediaService.getSmaMediaSize(param);
				        if (smas!=null) {
				        	for (Map<String, Object> smam : smas) {
								for (Map<String, Object> map : ls) {
									if (map.get("ContentId").equals(smam.get("sId"))) {
										map.put("MediaSize", smam.get("num"));
									}
								}
							}
						}
					}
			    } else { //TODO
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
						ps = conn.prepareStatement(sql);
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

	            List<Map<String, Object>> dictrefs = dictContentService.getDictRefListByIdsAndMeidaType(ids.replace("persf.resId", "resd.resId").substring(3), mediaType.equals("SEQU")?"wt_SeqMediaAsset":"wt_MediaAsset");
				if (dictrefs!=null) {
					for (Map<String, Object> m1 : ls) {
						for (Map<String, Object> m2 : dictrefs) {
							if (m1.get("ContentId").equals(m2.get("resId"))) {
								Map<String, Object> df = new HashMap<>();
								df.put("CataDid", m2.get("dictDid"));
								df.put("CataMName", "内容分类");
								df.put("CataTitle", m2.get("ddName"));
								df.put("CataMId", m2.get("dictMid"));
								if (m1.containsKey("ContentCatalogs")) {
									List<Map<String, Object>> dfls = (List<Map<String, Object>>) m1.get("ContentCatalogs");
									dfls.add(df);
								} else {
									List<Map<String, Object>> dfls = new ArrayList<>();
									dfls.add(df);
									m1.put("ContentCatalogs", dfls);
								}
							}
						}
					}
				}
				
				List<Map<String, Object>> kws = keyWordProService.getKeyWordsByIds(ids.replace("persf", "kws").substring(3), mediaType.equals("SEQU")?"wt_SeqMediaAsset":"wt_MediaAsset");
				if (kws!=null) {
					for (Map<String, Object> m1 : ls) {
						for (Map<String, Object> m2 : kws) {
							if (m1.get("ContentId").equals(m2.get("resId"))) {
								Map<String, Object> kw = new HashMap<>();
								kw.put("KwName", m2.get("kwName"));
								kw.put("CTime", m2.get("cTime"));
								if (m1.containsKey("ContentKeyWords")) {
									List<Map<String, Object>> kwls = (List<Map<String, Object>>) m1.get("ContentKeyWords");
									kwls.add(kw);
								} else {
									List<Map<String, Object>> kwls = new ArrayList<>();
									kwls.add(kw);
									m1.put("ContentKeyWords", kwls);
								}
							}
						}
					}
				}
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
			m.put("PersonSex", null);
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