package com.woting.cm.core.media.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.ui.tree.ZTree;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.WtContentMngConstants;
import com.woting.cm.core.broadcast.persis.po.BCLiveFlowPo;
import com.woting.cm.core.broadcast.persis.po.BroadcastPo;
import com.woting.cm.core.broadcast.service.BcLiveFlowService;
import com.woting.cm.core.channel.model.Channel;
import com.woting.cm.core.channel.model.ChannelAsset;
import com.woting.cm.core.channel.persis.po.ChannelAssetPo;
import com.woting.cm.core.channel.persis.po.ChannelPo;
import com.woting.cm.core.complexref.persis.po.ComplexRefPo;
import com.woting.cm.core.complexref.service.ComplexRefService;
import com.woting.cm.core.dict.mem._CacheDictionary;
import com.woting.cm.core.dict.model.DictDetail;
import com.woting.cm.core.dict.model.DictModel;
import com.woting.cm.core.dict.model.DictRefRes;
import com.woting.cm.core.dict.persis.po.DictRefResPo;
import com.woting.cm.core.media.model.MaSource;
import com.woting.cm.core.media.model.MediaAsset;
import com.woting.cm.core.media.model.SeqMediaAsset;
import com.woting.cm.core.media.persis.po.MaSourcePo;
import com.woting.cm.core.media.persis.po.MediaAssetPo;
import com.woting.cm.core.media.persis.po.SeqMaRefPo;
import com.woting.cm.core.media.persis.po.SeqMediaAssetPo;
import com.woting.cm.core.person.persis.po.PersonPo;
import com.woting.cm.core.person.persis.po.PersonRefPo;
import com.woting.cm.core.person.service.PersonService;
import com.woting.cm.core.utils.ContentUtils;
import com.woting.content.manage.channel.service.ChannelContentService;
import com.woting.content.manage.keyword.service.KeyWordProService;
import com.woting.exceptionC.Wtcm0101CException;

public class MediaService {
	@Resource(name = "defaultDAO")
	private MybatisDAO<MediaAssetPo> mediaAssetDao;
	@Resource(name = "defaultDAO")
	private MybatisDAO<MaSourcePo> maSourceDao;
	@Resource(name = "defaultDAO")
	private MybatisDAO<SeqMediaAssetPo> seqMediaAssetDao;
	@Resource(name = "defaultDAO")
	private MybatisDAO<SeqMaRefPo> seqMaRefDao;
	@Resource(name = "defaultDAO")
	private MybatisDAO<ChannelAssetPo> channelAssetDao;
	@Resource(name = "defaultDAO")
	private MybatisDAO<ChannelPo> channelDao;
	@Resource(name = "defaultDAO")
	private MybatisDAO<DictRefResPo> dictRefDao;
	@Resource
	private ChannelContentService channelContentService;
	@Resource
	private KeyWordProService keyWordProService;
	@Resource
	private BcLiveFlowService bcLiveFlowService;
	@Resource
	private ComplexRefService complexRefService;
	@Resource
	private PersonService personService;

	private Map<String, Object> FlowFlagState = new HashMap<String, Object>() {
		{
			put("0", "已提交");
			put("1", "审核中");
			put("2", "已发布");
			put("3", "已撤回");
			put("4", "未通过");
		}
	};

	@PostConstruct
	public void initParam() {
		mediaAssetDao.setNamespace("A_MEDIA");
		maSourceDao.setNamespace("A_MEDIA");
		seqMaRefDao.setNamespace("A_MEDIA");
		seqMediaAssetDao.setNamespace("A_MEDIA");
		channelAssetDao.setNamespace("A_CHANNELASSET");
		channelDao.setNamespace("A_CHANNEL");
		dictRefDao.setNamespace("A_DREFRES");
	}

	public MaSource getMasInfoByMasId(Map<String, Object> m) {
		MaSource mas = new MaSource();
		MaSourcePo maspo = maSourceDao.getInfoObject("getMasInfoByMaId", m);
		if (maspo == null)
			return null;
		mas.buildFromPo(maspo);
		return mas;
	}

	public SeqMaRefPo getSeqMaRefByMId(String mid) {
		SeqMaRefPo smarefpo = seqMaRefDao.getInfoObject("getS2MRefInfoByMId", mid);
		return smarefpo;
	}

	public List<SeqMaRefPo> getSeqMaRefBySid(String sid) {
		List<SeqMaRefPo> list = seqMaRefDao.queryForList("getS2MRefInfoBySId", sid);
		return list;
	}

	public int getCountInCha(Map<String, Object> m) {
		return channelAssetDao.getCount("countnum", m);
	}

	public List<ChannelAssetPo> getContentsByFlowFlag(Map<String, Object> m) {
		return channelAssetDao.queryForList("getListByFlowFlag", m);
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getMaListByPubId(String userid, String flowflag, String channelid,
			String seqmediaid) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> m1 = new HashMap<>();
		if (!flowflag.equals("0")) {
			if (flowflag.equals("5")) {
				m1.put("flowFlag", "0");
			} else {
				m1.put("flowFlag", flowflag);
			}
		}
		if (!channelid.equals("0")) {
			Map<String, Object> chm = new HashMap<>();
			chm.put("pcId", channelid);
			List<ChannelPo> chs = channelDao.queryForList("getInfo", chm);
			if (chs != null && chs.size() > 0) {
				String chids = "";
				for (ChannelPo ch : chs) {
					chids += ",'" + ch.getId() + "'";
				}
				chids = chids.substring(1);
				m1.put("channelIds", chids);
			} else {
				m1.put("channelIds", "'" + channelid + "'");
			}
		}
		if (!seqmediaid.equals("0")) {
			m1.put("isValidate", "1");
			m1.put("publisherId", userid);
			String wheresql = " and assetId in (select resId from wt_Person_Ref where personId = '" + userid
					+ "' and resTableName = 'wt_SeqMediaAsset' and resId = '"+seqmediaid+"')";
			if (m1.containsKey("channelIds")) {
				wheresql += " and channelId in ("+m1.get("channelIds")+")";
			}
			m1.put("wheresql", wheresql);
			m1.put("sortByClause", " pubTime,cTime");
			m1.remove("channelIds");
			List<ChannelAssetPo> chas = channelAssetDao.queryForList("getListBy", m1);
			if (chas != null && chas.size() > 0) {
				String assetIds = "";
				for (ChannelAssetPo cha : chas) {
					assetIds += ",'" + cha.getAssetId() + "'";
				}
				assetIds = assetIds.substring(1);
				Map<String, Object> m2 = new HashMap<>();
				m2.put("ids", assetIds);
				m2.put("smaPubId", "0");
				List<SeqMediaAssetPo> smas = seqMediaAssetDao.queryForList("getSmaList", m2);
				if (smas != null && smas.size() > 0) {
					for (SeqMediaAssetPo sma : smas) {
						List<MediaAssetPo> listpo = getMaListBySmaId(sma.getId());
						if (listpo != null && listpo.size() > 0) {
							String resids = "";
							for (MediaAssetPo mediaAssetPo : listpo) {
								resids += ",'" + mediaAssetPo.getId() + "'";
							}
							resids = resids.substring(1);
							List<ChannelAssetPo> chapolist = getCHAListByAssetId(resids, "wt_MediaAsset");
							List<Map<String, Object>> pubChannelList = channelContentService
									.getChannelAssetList(chapolist);
							for (MediaAssetPo mediaAssetPo : listpo) {
								MediaAsset ma = new MediaAsset();
								ma.buildFromPo(mediaAssetPo);
								Map<String, Object> m = ContentUtils.convert2Ma(ma.toHashMap(), null, null,
										pubChannelList, null);
								m.put("ContentSeqId", sma.getId());
								m.put("ContentSeqName", sma.getSmaTitle());
								if (m.containsKey("ContentPubChannels")) {
									List<Map<String, Object>> chass = (List<Map<String, Object>>) m
											.get("ContentPubChannels");
									if (chass != null && chass.size() > 0) {
										for (Map<String, Object> map : chass) {
											map.put("FlowFlagState", FlowFlagState.get(map.get("FlowFlag")));
										}
									}
								}
								list.add(m);
							}
						}
					}
				}
				return list;
			}
		}
		m1.put("assetType", "wt_MediaAsset");
		m1.put("isValidate", "1");
		m1.put("publisherId", userid);
		String wheresql = " and assetId in (select resId from wt_Person_Ref where personId = '" + userid
				+ "' and resTableName = 'wt_MediaAsset')";
		if (m1.containsKey("channelIds")) {
			wheresql += " and channelId in ("+m1.get("channelIds")+")";
		}
		m1.put("wheresql", wheresql);
		m1.put("sortByClause", " pubTime,cTime");
		m1.remove("channelIds");
		List<ChannelAssetPo> chas = channelAssetDao.queryForList("getList", m1);
		if (chas != null && chas.size() > 0) {
			String assetIds = "";
			for (ChannelAssetPo cha : chas) {
				assetIds += ",'" + cha.getAssetId() + "'";
			}
			assetIds = assetIds.substring(1);
			Map<String, Object> m2 = new HashMap<>();
			m2.put("ids", assetIds);
			m2.put("maPubId", "0");
			List<MediaAssetPo> mas = mediaAssetDao.queryForList("getMaList", m2);
			if (mas != null && mas.size() > 0) {
				String resids = "";
				for (MediaAssetPo mediaAssetPo : mas) {
					resids += ",'" + mediaAssetPo.getId() + "'";
				}
				resids = resids.substring(1);
				List<ChannelAssetPo> chapolist = getCHAListByAssetId(resids, "wt_MediaAsset");
				List<Map<String, Object>> pubChannelList = channelContentService.getChannelAssetList(chapolist);
				for (MediaAssetPo mediaAssetPo : mas) {
					MediaAsset ma = new MediaAsset();
					ma.buildFromPo(mediaAssetPo);
					Map<String, Object> m = ContentUtils.convert2Ma(ma.toHashMap(), null, null, pubChannelList, null);
					if (m.containsKey("ContentPubChannels")) {
						List<Map<String, Object>> chass = (List<Map<String, Object>>) m.get("ContentPubChannels");
						if (chass != null && chass.size() > 0) {
							for (Map<String, Object> map : chass) {
								map.put("FlowFlagState", FlowFlagState.get(map.get("FlowFlag")));
							}
						}
					}
					SeqMaRefPo seqMaRefPo = seqMaRefDao.getInfoObject("getS2MRefInfoByMId", mediaAssetPo.getId());
					if (seqMaRefPo != null) {
						m.put("ContentSeqId", seqMaRefPo.getSId());
						SeqMediaAsset sma = getSmaInfoById(seqMaRefPo.getSId());
						if (sma != null) {
							m.put("ContentSeqName", sma.getSmaTitle());
						}
					}
					list.add(m);
				}
			}
			return list;
		}
		return null;
	}

	// 根据主播id查询其所有单体资源
	public List<Map<String, Object>> getMaInfoByMaPubId(String id) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<MediaAssetPo> listpo = new ArrayList<MediaAssetPo>();
		listpo = mediaAssetDao.queryForList("getMaListByMaPubId", id);
		if (listpo != null && listpo.size() > 0) {
			String resids = "";
			for (MediaAssetPo mediaAssetPo : listpo) {
				resids += ",'" + mediaAssetPo.getId() + "'";
			}
			resids = resids.substring(1);
			List<ChannelAssetPo> chapolist = getCHAListByAssetId(resids, "wt_MediaAsset");
			List<Map<String, Object>> pubChannelList = channelContentService.getChannelAssetList(chapolist);
			for (MediaAssetPo mediaAssetPo : listpo) {
				MediaAsset ma = new MediaAsset();
				ma.buildFromPo(mediaAssetPo);
				Map<String, Object> m = ContentUtils.convert2Ma(ma.toHashMap(), null, null, pubChannelList, null);
				if (m.containsKey("ContentPubChannels")) {
					List<Map<String, Object>> chass = (List<Map<String, Object>>) m.get("ContentPubChannels");
					if (chass != null && chass.size() > 0) {
						for (Map<String, Object> map : chass) {
							map.put("FlowFlagState", FlowFlagState.get(map.get("FlowFlag")));
						}
					}
				}
				SeqMaRefPo seqMaRefPo = seqMaRefDao.getInfoObject("getS2MRefInfoByMId", mediaAssetPo.getId());
				m.put("ContentSeqId", seqMaRefPo == null ? null : seqMaRefPo.getSId());
				SeqMediaAsset sma = getSmaInfoById(seqMaRefPo.getSId());
				if (sma != null) {
					m.put("ContentSeqName", sma.getSmaTitle());
				}
				list.add(m);
			}
		}
		return list;
	}

	/**
	 * 获取简介专辑信息列表
	 * 
	 * @param userid
	 * @return
	 */
	public List<Map<String, Object>> getShortSmaListByPubId(String userid) {
		List<Map<String, Object>> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		map.put("id", userid);
		if (personService.getPersonPoById(userid) != null) {
			map.clear();
			map.put("isValidate", "1");
			map.put("publisherId", userid);
			map.put("assetType", "wt_SeqMediaAsset");
			map.put("wheresql", " and assetId in (select resId from wt_Person_Ref where personId = '" + userid
					+ "' and resTableName = 'wt_SeqMediaAsset')");
			map.put("sortByClause", " pubTime,cTime");
			List<ChannelAssetPo> chas = channelAssetDao.queryForList("getListBy", map);
			if (chas != null && chas.size() > 0) {
				for (ChannelAssetPo cha : chas) {
					SeqMediaAsset sma = getSmaInfoById(cha.getAssetId());
					if (sma != null) {
						Map<String, Object> m = new HashMap<>();
						m.put("SeqMediaName", sma.getSmaTitle());
						m.put("SeqMediaId", sma.getId());
						list.add(m);
					}
				}
				if (list != null && list.size() > 0) {
					return list;
				}
			}
		}
		return null;
	}

	public List<Map<String, Object>> getSmaListByPubId(String userid) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<SeqMediaAssetPo> listpo = new ArrayList<SeqMediaAssetPo>();
		Map<String, Object> m = new HashMap<>();
		m.put("id", userid);
		if (personService.getPersonPoById(userid) != null) {
			m.clear();
			m.put("isValidate", "1");
			m.put("publisherId", userid);
			m.put("assetType", "wt_SeqMediaAsset");
			m.put("wheresql", " and assetId in (select resId from wt_Person_Ref where personId = '" + userid
					+ "' and resTableName = 'wt_SeqMediaAsset')");
			m.put("sortByClause", " pubTime,cTime");
			List<ChannelAssetPo> chas = channelAssetDao.queryForList("getListBy", m);
			if (chas != null && chas.size() > 0) {
				String ids = "";
				for (ChannelAssetPo cha : chas) {
					ids += ",'" + cha.getAssetId() + "'";
				}
				ids = ids.substring(1);
				m = new HashMap<>();
				m.put("ids", ids);
				m.put("smaPubId", "0");
				listpo = seqMediaAssetDao.queryForList("getSmaList", m);
				list = makeSmaListToReturn(listpo);
				return list;
			}
		}

		return null;
	}

	public List<Map<String, Object>> getSmaListByPubId(String userid, String flowflag, String channelid) {
		Map<String, Object> m1 = new HashMap<>();
		m1.put("id", userid);
		if (personService.getPersonPoById(userid) != null) {
			m1.clear();
			m1.put("assetType", "wt_SeqMediaAsset");
			if (!flowflag.equals("0")) {
				if (flowflag.equals("5")) {
					m1.put("flowFlag", "0"); // 5用来查询待入库，其余编号与flowflag对应
				} else {
					m1.put("flowFlag", flowflag);
				}
			}
			if (!channelid.equals("0")) {
				Map<String, Object> chm = new HashMap<>();
				chm.put("pcId", channelid);
				List<ChannelPo> chs = channelDao.queryForList("getInfo", chm);
				if (chs != null && chs.size() > 0) {
					String chids = "";
					for (ChannelPo ch : chs) {
						chids += ",'" + ch.getId() + "'";
					}
					chids = chids.substring(1);
					m1.put("channelIds", chids);
				} else {
					m1.put("channelIds", "'" + channelid + "'");
				}
			}
			
			m1.put("isValidate", "1");
			m1.put("publisherId", userid);
			String wheresql = " and assetId in (select resId from wt_Person_Ref where personId = '" + userid
					+ "' and resTableName = 'wt_SeqMediaAsset')";
			
			if (m1.containsKey("channelIds")) {
				wheresql += " and channelId in ("+m1.get("channelIds")+")";
			}
			m1.put("wheresql", wheresql);
			m1.put("sortByClause", " pubTime,cTime");
			m1.remove("channelIds");
			List<ChannelAssetPo> chas = channelAssetDao.queryForList("getListBy", m1);
			if (chas != null && chas.size() > 0) {
				String assetIds = "";
				for (ChannelAssetPo cha : chas) {
					assetIds += ",'" + cha.getAssetId() + "'";
				}
				assetIds = assetIds.substring(1);
				Map<String, Object> m2 = new HashMap<>();
				m2.put("ids", assetIds);
				m2.put("smaPubId", "0");
				List<SeqMediaAssetPo> smas = seqMediaAssetDao.queryForList("getSmaList", m2);
				List<Map<String, Object>> list = makeSmaListToReturn(smas);
				return list;
			}
		}

		return null;
	}

	// 整理专辑返回结果
	public List<Map<String, Object>> makeSmaListToReturn(List<SeqMediaAssetPo> listpo) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (listpo != null && listpo.size() > 0) {
			String resids = "";
			for (SeqMediaAssetPo seqMediaAssetPo : listpo) {
				resids += ",'" + seqMediaAssetPo.getId() + "'";
			}
			resids = resids.substring(1);
			List<Map<String, Object>> catalist = getResDictRefByResId(resids, "wt_SeqMediaAsset");
			List<ChannelAssetPo> chapolist = getCHAListByAssetId(resids, "wt_SeqMediaAsset");
			List<Map<String, Object>> pubChannelList = channelContentService.getChannelAssetList(chapolist);
			for (SeqMediaAssetPo seqMediaAssetPo : listpo) {
				SeqMediaAsset sma = new SeqMediaAsset();
				sma.buildFromPo(seqMediaAssetPo);
				List<Map<String, Object>> personlist = makePersonList("wt_SeqMediaAsset", sma.getId());
				Map<String, Object> smap = ContentUtils.convert2Sma(sma.toHashMap(), personlist, catalist, pubChannelList, null);
				// 标签处理
				List<Map<String, Object>> kws = keyWordProService.getKeyWordListByAssetId("'" + sma.getId() + "'",
						"wt_SeqMediaAsset");
				if (kws != null && kws.size() > 0) {
					smap.put("ContentKeyWords", kws);
				}
				// 创作方式处理
				List<Map<String, Object>> cps = makeComplexRefList("wt_SeqMediaAsset", sma.getId(), "4", null);
				if (cps != null && cps.size() > 0) {
					smap.put("ContentMemberTypes", cps);
				}
				List<SeqMaRefPo> l = seqMaRefDao.queryForList("getS2MRefInfoBySId", sma.getId());
				if (smap.containsKey("ContentPubChannels")) {
					List<Map<String, Object>> chas = (List<Map<String, Object>>) smap.get("ContentPubChannels");
					if (chas != null && chas.size() > 0) {
						for (Map<String, Object> map : chas) {
							map.put("FlowFlagState", FlowFlagState.get(map.get("FlowFlag")));
						}
					}
				}
				smap.put("SubCount", l.size());
				list.add(smap);
			}
		}
		return list;
	}

	// 整理专辑返回结果
	public List<Map<String, Object>> makeMaListToReturn(List<MediaAssetPo> listpo) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (listpo != null && listpo.size() > 0) {
			String resids = "";
			for (MediaAssetPo mediaAssetPo : listpo) {
				resids += ",'" + mediaAssetPo.getId() + "'";
			}
			resids = resids.substring(1);
			List<Map<String, Object>> catalist = getResDictRefByResId(resids, "wt_MediaAsset");
			List<ChannelAssetPo> chapolist = getCHAListByAssetId(resids, "wt_MediaAsset");
			List<Map<String, Object>> pubChannelList = channelContentService.getChannelAssetList(chapolist);
			for (MediaAssetPo ma : listpo) {
				List<Map<String, Object>> personlist = makePersonList("wt_MediaAsset", ma.getId());
				Map<String, Object> mam = ContentUtils.convert2Ma(ma.toHashMap(), personlist, catalist, pubChannelList, null);
				SeqMaRefPo smaref = getSeqMaRefByMId(ma.getId());
				SeqMediaAsset sma = getSmaInfoById(smaref.getSId());
				mam.put("ContentSeqId", sma.getId());
				mam.put("ContentSeqName", sma.getSmaTitle());
				List<Map<String, Object>> kws = keyWordProService.getKeyWordListByAssetId("'" + ma.getId() + "'",
						"wt_MediaAsset");
				if (kws != null && kws.size() > 0) {
					mam.put("ContentKeyWords", kws);
				}
				List<Map<String, Object>> cps = makeComplexRefList("wt_MediaAsset", ma.getId(), "4", null);
				if (cps != null && cps.size() > 0) {
					mam.put("ContentMemberTypes", cps);
				}
				if (mam.containsKey("ContentPubChannels")) {
					List<Map<String, Object>> chas = (List<Map<String, Object>>) mam.get("ContentPubChannels");
					if (chas != null && chas.size() > 0) {
						for (Map<String, Object> map : chas) {
							map.put("FlowFlagState", FlowFlagState.get(map.get("FlowFlag")));
						}
					}
				}
				list.add(mam);
			}
		}
		return list;
	}

	// 整理专辑返回结果
	public List<Map<String, Object>> makeBcListToReturn(List<BroadcastPo> listpo) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (listpo != null && listpo.size() > 0) {
			String resids = "";
			for (BroadcastPo bcPo : listpo) {
				resids += ",'" + bcPo.getId() + "'";
			}
			resids = resids.substring(1);
			List<Map<String, Object>> catalist = getResDictRefByResId(resids, "wt_Broadcast");
			List<ChannelAssetPo> chapolist = getCHAListByAssetId(resids, "wt_Broadcast");
			List<Map<String, Object>> pubChannelList = channelContentService.getChannelAssetList(chapolist);
			for (BroadcastPo bc : listpo) {
				List<BCLiveFlowPo> ls = bcLiveFlowService.getBcLiveFlowsByBcId(bc.getId());
				Map<String, Object> bcm = bc.toHashMap();
				List<Map<String, Object>> ms = new ArrayList<>();
				if (ls != null) {
					for (BCLiveFlowPo bcLiveFlowPo : ls) {
						if (bcLiveFlowPo.getIsMain() == 1) {
							bcm.put("flowURI", bcLiveFlowPo.getFlowURI());
							bcm.put("bcSource", bcLiveFlowPo.getBcSource());
						}
						Map<String, Object> bclfm = new HashMap<>();
						bclfm.put("BcPlayPath", bcLiveFlowPo.getFlowURI());
						bclfm.put("IsMain", bcLiveFlowPo.getIsMain());
						bclfm.put("BcSource", bcLiveFlowPo.getBcSource());
						ms.add(bclfm);
					}
				}
				Map<String, Object> mam = ContentUtils.convert2Bc(bcm, null, catalist, pubChannelList, null);
				mam.put("BcPlayPathList", ms);
				list.add(mam);
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> makeComplexRefList(String assetTableName, String assetId, String dictMID,
			String dictDId) {
		List<ComplexRefPo> cps = complexRefService.getComplexRefList(assetTableName, assetId, dictMID, dictDId);
		if (cps != null && cps.size() > 0) {
			_CacheDictionary _cd = ((CacheEle<_CacheDictionary>) SystemCache.getCache(WtContentMngConstants.CACHE_DICT))
					.getContent();
			DictModel dm = _cd.getDictModelById("4");
			ZTree<DictDetail> eu1 = new ZTree<DictDetail>(dm.dictTree);
			List<Map<String, Object>> rel = new ArrayList<>();
			for (ComplexRefPo complexRefPo : cps) {
				Map<String, Object> m = new HashMap<>();
				m.put("TypeName", eu1.findNode(complexRefPo.getDictDId()).getNodeName());
				m.put("TypeId", complexRefPo.getDictDId());
				m.put("TypeInfo", complexRefPo.getResId());
				rel.add(m);
			}
			if (rel != null && rel.size() > 0) {
				return rel;
			}
		}
		return null;
	}

	// 根据专辑id得到专辑
	public SeqMediaAsset getSmaInfoById(String id) {
		SeqMediaAsset sma = new SeqMediaAsset();
		SeqMediaAssetPo smapo = seqMediaAssetDao.getInfoObject("getSmaInfoById", id);
		if (smapo == null)
			return null;
		sma.buildFromPo(smapo);
		return sma;
	}

	public List<MediaAssetPo> getMaListBySmaId(String smaid) {
		List<MediaAssetPo> malist = mediaAssetDao.queryForList("getMaListBySmaId", smaid);
		return malist;
	}

	public List<SeqMaRefPo> getSmaListBySid(String sid) {
		List<SeqMaRefPo> seqMaRefPos = seqMaRefDao.queryForList("getS2MRefInfoBySId", sid);
		if (seqMaRefPos == null)
			return null;
		return seqMaRefPos;
	}

	// 根据栏目id得到栏目
	public Channel getChInfoById(String id) {
		Channel ch = new Channel();
		ChannelPo chpo = channelDao.getInfoObject("getInfoById", id);
		if (chpo == null)
			return null;
		ch.buildFromPo(chpo);
		return ch;
	}

	// 根据栏目发布表id得到栏目发布信息
	public ChannelAsset getCHAInfoById(String id) {
		ChannelAsset cha = new ChannelAsset();
		ChannelAssetPo chapo = channelAssetDao.getInfoObject("getInfoById", id);
		if (chapo == null)
			return null;
		cha.buildFromPo(chapo);
		return cha;
	}

	// 根据栏目发布表资源id得到栏目发布信息
	public List<ChannelAssetPo> getCHAInfoByAssetId(String id) {
		List<ChannelAssetPo> chapo = channelAssetDao.queryForList("getInfoByAssetId", id);
		if (chapo != null && chapo.size()>0)
			return chapo;
		return null;
	}
	

	public List<ChannelAssetPo> getCHAListByAssetId(String assetIds, String assetType) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("assetType", assetType);
		param.put("assetIds", assetIds);
		List<ChannelAssetPo> chapolist = channelAssetDao.queryForList("getListByAssetIds", param);
		return chapolist;
	}

	public List<Map<String, Object>> getCHAByAssetId(String assetIds, String assetType) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("assetType", assetType);
		param.put("assetIds", assetIds);
		List<ChannelAssetPo> chpolist = channelAssetDao.queryForList("getListByAssetIds", param);
		List<Map<String, Object>> chlist = new ArrayList<Map<String, Object>>();
		for (ChannelAssetPo chpo : chpolist) {
			chlist.add(chpo.toHashMap());
		}
		return chlist;
	}

	public List<ChannelAssetPo> getChaByAssetIdAndPubId(String pubId, String assetId, String assetType) {
		Map<String, Object> m = new HashMap<>();
		if (pubId != null) {
			m.put("publisherId", pubId);
		}
		m.put("assetId", assetId);
		m.put("assetType", assetType);
		List<ChannelAssetPo> chas = channelAssetDao.queryForList("getList", m);
		if (chas != null && chas.size() > 0) {
			return chas;
		}
		return null;
	}

	// 根据资源id得到资源字典项对应关系
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getResDictRefByResId(String resids, String resTableName) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("resTableName", resTableName);
		param.put("resIds", resids);
		List<DictRefResPo> rcrpL = dictRefDao.queryForList("getListByResIds", param);
		List<Map<String, Object>> catalist = new ArrayList<Map<String, Object>>();
		if (rcrpL != null && rcrpL.size() > 0) {
			_CacheDictionary _cd = ((CacheEle<_CacheDictionary>) SystemCache.getCache(WtContentMngConstants.CACHE_DICT)).getContent();
			for (DictRefResPo dictRefResPo : rcrpL) {
				Map<String, Object> dictrefm = dictRefResPo.toHashMap();
				DictModel dm = _cd.getDictModelById(dictrefm.get("dictMid") + "");
				dictrefm.put("dictMName", dm.getDmName());
				TreeNode<DictDetail> root = (TreeNode<DictDetail>) dm.dictTree.findNode(dictrefm.get("dictDid") + "");
				dictrefm.put("pathNames", root.getNodeName());
				catalist.add(dictrefm);
			}
		}
		return catalist;
	}

	public List<DictRefResPo> getResDictRefByResId(String resid) {
		List<DictRefResPo> rcrpL = dictRefDao.queryForList("getListByResId", resid);
		return rcrpL;
	}

	public MediaAsset getMaInfoById(String id) {
		MediaAsset ma = new MediaAsset();
		MediaAssetPo mapo = mediaAssetDao.getInfoObject("getMaInfoById", id);
		if (mapo == null)
			return null;
		else
			ma.buildFromPo(mapo);
		return ma;
	}

	public MaSourcePo getMasInfoByMaId(String maId) {
		Map<String, Object> m = new HashMap<>();
		m.put("maId", maId);
		MaSourcePo mas = maSourceDao.getInfoObject("getMas", m);
		if (mas != null) {
			return mas;
		}
		return null;
	}

	public MaSource getSameMas(MaSource mas) {
		MaSourcePo masPo = maSourceDao.getInfoObject("getSameSam", mas);
		if (masPo == null)
			return null;
		MaSource _mas = new MaSource();
		_mas.buildFromPo(masPo);
		return _mas;
	}

	public void bindMa2Sma(MediaAsset ma, SeqMediaAsset sma) {
		SeqMaRefPo smrPo = new SeqMaRefPo();
		if (StringUtils.isNullOrEmptyOrSpace(ma.getId()) || StringUtils.isNullOrEmptyOrSpace(sma.getId())) {
			throw new Wtcm0101CException("专辑和单曲的Id都不能为空");
		}
		smrPo.setId(SequenceUUID.getPureUUID());
		smrPo.setSId(sma.getId());
		smrPo.setMId(ma.getId());
		smrPo.setColumnNum(0);
		smrPo.setDescn(sma.getSmaTitle() + "--" + ma.getMaTitle());
		seqMaRefDao.insert("bindMa2Sma", smrPo);
	}

	public void saveMa(MediaAsset ma) {
		mediaAssetDao.insert("insertMa", ma.convert2Po());
	}

	public void saveMas(MaSource mas) {
		maSourceDao.insert("insertMas", mas.convert2Po());
	}

	public void saveCha(ChannelAsset cha) {
		channelAssetDao.insert("insert", cha.convert2Po());
	}

	public void saveCha(ChannelAssetPo cha) {
		channelAssetDao.insert("insert", cha);
	}

	public void saveSma(SeqMediaAsset sma) {
		seqMediaAssetDao.insert("insertSma", sma.convert2Po());
	}

	public void saveDictRef(DictRefRes dictref) {
		dictRefDao.insert("insert", dictref.convert2Po());
	}

	public void updateSeqMaRef(SeqMaRefPo seqmapo) {
		mediaAssetDao.update("updateSeqMaRef", seqmapo);
	}

	public void updateMas(MaSource mas) {
		mediaAssetDao.update("updateMas", mas.convert2Po());
	}

	public void updateMas(MaSourcePo mas) {
		mediaAssetDao.update("updateMas", mas);
	}

	public void updateMa(MediaAsset ma) {
		mediaAssetDao.update("updateMa", ma.convert2Po());
	}

	public void updateSma(SeqMediaAsset sma) {
		seqMediaAssetDao.update("updateSma", sma.convert2Po());
	}

	public int updateCha(ChannelAsset cha) {
		return channelAssetDao.update("update", cha.convert2Po());
	}

	public int updateCha(ChannelAssetPo cha) {
		return channelAssetDao.update("update", cha);
	}

	public void removeMa(String id) {
		mediaAssetDao.delete("multiMaById", id);
	}

	public void removeSma(String id) {
		seqMediaAssetDao.delete("multiSmaById", id);
	}

	public void removeMas(String maid) {
		maSourceDao.delete("multiMasByMaId", maid);
	}

	public void removeMa2SmaByMid(String mid) {
		seqMaRefDao.delete("multiM2SRefByMId", mid);
	}

	public void removeMa2SmaBySid(String sid) {
		seqMaRefDao.delete("multiM2SRefBySId", sid);
	}

	public void removeResDictRef(String id) {
		dictRefDao.delete("multiDelByResId", id);
	}

	public void removeCha(String assetId, String resTableName) {
		Map<String, Object> m = new HashMap<>();
		m.put("assetId", assetId);
		m.put("resTableName", resTableName);
		channelAssetDao.delete("deleteByEntity", m);
	}

	public void removeKeyWordRes(String assetId, String resTableName) {
		keyWordProService.removeKeyWordByAssetId(assetId, resTableName);
	}

	public void removeMedia(String userId, String id) {
		removeMa(id);
		removeMas(id);
		removeMa2SmaByMid(id);
		removeResDictRef(id);
		removeKeyWordRes(id, "wt_MediaAsset");
		removeCha(id, "wt_MediaAsset");
		personService.remove(userId, "wt_MediaAsset", id);
	}

	public void removeSeqMedia(String userId, String id) {
		List<SeqMaRefPo> l = getSeqMaRefBySid(id);
		if (getResDictRefByResId(id) != null) { // 删除与专辑绑定的下级节目内容分类信息
			for (SeqMaRefPo seqMaRefPo : l) {
				removeMedia(userId, seqMaRefPo.getMId());
				removeResDictRef(seqMaRefPo.getMId());
			}
		}
		personService.remove(userId, "wt_SeqMediaAsset", id);
		removeResDictRef(id);
		if (getCHAInfoByAssetId(id) != null) { // 删除与专辑绑定的下级节目栏目信息
			for (SeqMaRefPo seqMaRefPo : l) {
				removeCha(seqMaRefPo.getMId(), "wt_SeqMediaAsset");
			}
		}
		removeKeyWordRes(id, "wt_SeqMediaAsset");
		removeCha(id, "wt_SeqMediaAsset");
		removeMa2SmaBySid(id);
		removeSma(id);
	}
	
	private List<Map<String, Object>> makePersonList(String resTableName, String resId) {
		PersonRefPo poref = personService.getPersonRefBy(resTableName, resId);
		if (poref!=null) {
			PersonPo po = personService.getPersonPoById(poref.getPersonId());
		    Map<String, Object> pom = new HashMap<>();
		    pom.put("resTableName", resTableName);
		    pom.put("resId", resId);
		    pom.put("pName", po.getpName());
		    pom.put("cName", poref.getRefName());
		    pom.put("personId", po.getId());
		    pom.put("perImg", po.getPortrait());
		    List<Map<String, Object>> personlist = new ArrayList<>();
		    personlist.add(pom);
		    return personlist;
		}
		return null;
	}
}