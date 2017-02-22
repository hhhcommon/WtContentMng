package com.woting.content.person.service;
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
import com.woting.content.manage.channel.service.ChannelContentService;
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
	@Resource
	private ChannelContentService channelContentService;

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
		String ids = "";
		Map<String, Object> param = new HashMap<>();
		try {
			if (perfs!=null && perfs.size()>0) {
				numall = perfs.size();
				for (PersonRefPo personRefPo : perfs) {
					ids += " or persf.resId = '"+personRefPo.getResId()+"'";
				}
				if (sortType==1) {
					 ids = "";
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
					}
			    } else {
			        param.clear();
			        param.put("mediaType", mediaType.equals("SEQU")?" ch.assetType = 'wt_SeqMediaAsset'":" ch.assetType = 'wt_MediaAsset'");
			        param.put("ids", ids.replace("persf.resId", "ch.assetId").substring(3));
			        param.put("OrderByClause", " ch.sort DESC, ch.pubTime DESC");
			        param.put("LimitByClause", (page-1)*pageSize+","+(page*pageSize));
			        List<Map<String, Object>> chls = channelContentService.getPersonContentList(param);
			        if (chls!=null) {
			        	ids = "";
			            for (Map<String, Object> map : chls) {
							Map<String, Object> oneDate = new HashMap<String, Object>();
							oneDate.put("id", map.get("id"));
							oneDate.put("ChannelName", map.get("channelName"));
							oneDate.put("ChannelId", map.get("channelId"));
							oneDate.put("ContentName", map.get("pubName"));
							oneDate.put("ContentId", map.get("assetId"));
							oneDate.put("ContentPublisher", map.get("publisher"));
							oneDate.put("MediaType", map.get("assetType"));
							oneDate.put("ContentDesc", map.get("descn"));
							oneDate.put("ContentImg", map.get("pubImg"));
							oneDate.put("ContentFlowFlag", map.get("flowFlag"));
							oneDate.put("ContentSort", map.get("sort"));
							oneDate.put("ContentTime", map.get("time"));
							oneDate.put("PersonId", map.get("personId"));
							oneDate.put("PersonName", map.get("pName"));
							oneDate.put("ContentPubTime", map.get("pubTime"));
							oneDate.put("MediaSize", 1);
							ls.add(oneDate);
							ids += " or persf.resId = '"+map.get("assetId")+"'";
						}
					}
			    }
	            
	            param.clear();
	            param.put("ids", ids.replace("persf.resId", "cha.assetId").substring(3));
	            param.put("assetType", (mediaType.equals("SEQU")?"wt_SeqMediaAsset":"wt_MediaAsset"));
	            param.put("sortByClause", " cha.pubTime asc");
	            List<Map<String, Object>> cham = channelContentService.getChannelAssetsByAssetIdsAndAssetType(param);
	            if (cham!=null) {
					for (Map<String, Object> map : ls) {
						for (Map<String, Object> chm : cham) {
							if (map.get("ContentId").equals(chm.get("assetId"))) {
								Map<String, Object> chamap = new HashMap<>();
								chamap.put("FlowFlag", chm.get("flowFlag"));
								chamap.put("ChannelName", chm.get("channelName"));
								chamap.put("ChannelId", chm.get("id"));
								chamap.put("PubTime", chm.get("pubTime"));
								map.put("ContentPubTime", chm.get("pubTime"));
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
				}
	            
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
				
				List<Map<String, Object>> perls = personService.getPersonsByResIdsAndResTableName(ids.replace("persf", "perf").substring(3), mediaType.equals("SEQU")?"wt_SeqMediaAsset":"wt_MediaAsset");
				if (perls!=null) {
					for (Map<String, Object> m1 : ls) {
						for (Map<String, Object> m2 : perls) {
							if (m1.get("ContentId").equals(m2.get("resId"))) {
								Map<String, Object> df = new HashMap<>();
								df.put("RefName", "主播");
								df.put("PerId", m2.get("id"));
								df.put("PerName", m2.get("pName"));
								if (m1.containsKey("ContentPersons")) {
									List<Map<String, Object>> dfls = (List<Map<String, Object>>) m1.get("ContentPersons");
									dfls.add(df);
								} else {
									List<Map<String, Object>> dfls = new ArrayList<>();
									dfls.add(df);
									m1.put("ContentPersons", dfls);
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
