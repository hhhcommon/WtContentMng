package com.woting.content.manage.seqmedia.service;

import java.nio.channels.ScatteringByteChannel;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import com.greenpineyu.fel.parser.FelParser.conditionalOrExpression_return;
import com.spiritdata.framework.util.ChineseCharactersUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.woting.cm.core.channel.persis.po.ChannelAssetPo;
import com.woting.cm.core.channel.persis.po.ChannelAssetProgressPo;
import com.woting.cm.core.channel.service.ChannelAssetProgressService;
import com.woting.cm.core.complexref.persis.po.ComplexRefPo;
import com.woting.cm.core.complexref.service.ComplexRefService;
import com.woting.cm.core.keyword.persis.po.KeyWordPo;
import com.woting.cm.core.keyword.persis.po.KeyWordResPo;
import com.woting.cm.core.keyword.service.KeyWordBaseService;
import com.woting.cm.core.media.model.SeqMediaAsset;
import com.woting.cm.core.media.persis.po.MediaAssetPo;
import com.woting.cm.core.media.persis.po.SeqMediaAssetPo;
import com.woting.cm.core.media.service.MediaService;
import com.woting.cm.core.person.persis.po.PersonPo;
import com.woting.cm.core.person.persis.po.PersonRefPo;
import com.woting.cm.core.person.service.PersonService;
import com.woting.content.manage.dict.service.DictContentService;
import com.woting.content.manage.media.service.MediaContentService;
import com.woting.passport.UGA.persis.pojo.UserPo;
import com.woting.passport.UGA.service.UserService;

@Service
public class SeqContentService {
	@Resource
	private MediaService mediaService;
	@Resource
	private DictContentService dictContentService;
	@Resource
	private MediaContentService mediaContentService;
	@Resource
	private UserService userService;
	@Resource
	private KeyWordBaseService keyWordBaseService;
	@Resource
	private ComplexRefService complexRefService;
	@Resource
	private PersonService personService;
	@Resource
	private ChannelAssetProgressService channelAssetProgressService;

	/**
	 * 查询主播的资源列表
	 * 
	 * @param userid
	 * @param mediatype
	 * @return
	 */
	public Map<String, Object> getHostSeqMediaContents(String userid, String flowflag, String channelid, String shortsearch, int page, int pageSize) {
		Map<String, Object> retM = new HashMap<>();
		if (shortsearch.equals("true")) {
			List<Map<String, Object>> l = mediaService.getShortSmaListByPubId(userid);
			retM.put("List", l);
			return retM;
		} else {
			if (flowflag.equals("0") && channelid.equals("0")) {
				retM = mediaService.getSmaListByPubId(userid,page,pageSize);
				if (retM != null && retM.size() > 0)
					return retM;
				else
					return null;
			} else {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				list = mediaService.getSmaListByPubId(userid, flowflag, channelid, page, pageSize);
				if (list != null && list.size() > 0) {
					retM.put("List", list);
					return retM;
				} else return null;
			}
		}
	}

	/**
	 * 创建专辑
	 * 
	 * @param upfile
	 * @param m
	 * @return
	 */
	public Map<String, Object> addSeqMediaInfo(String id, String userid, String contentname, String channelId,
			String contentimg, List<Map<String, Object>> tags, List<Map<String, Object>> memberType, String contentdesc,
			String pubTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 保存专辑信息到资源库
		SeqMediaAsset sma = new SeqMediaAsset();
		if (id != null) {
			sma.setId(id);
		} else {
			sma.setId(SequenceUUID.getPureUUID());
		}
		sma.setSmaTitle(contentname);
		sma.setSmaImg(contentimg);
		sma.setDescn(contentdesc == null || contentdesc.toLowerCase().equals("null") ? "这家伙真懒，什么都没留下" : contentdesc);
		sma.setSmaStatus(1);
		sma.setCTime(new Timestamp(System.currentTimeMillis()));
		sma.setSmaPubType(1);
		sma.setSmaPubId("0");
		sma.setSmaPublisher("我听科技");
		UserPo user = userService.getUserById(userid);
		if (user == null) {
			return null;
		}
		PersonPo po = personService.getPersonPoById(userid);
		if (po != null) {
			PersonRefPo poref = new PersonRefPo();
			poref.setId(SequenceUUID.getPureUUID());
			poref.setRefName("主播-专辑");
			poref.setPersonId(userid);
			poref.setResTableName("wt_SeqMediaAsset");
			poref.setResId(sma.getId());
			poref.setcTime(sma.getCTime());
			personService.insertPersonRef(poref);
		} else {
			po = new PersonPo();
			po.setId(userid);
			po.setPortrait(contentimg);
			if (user.getUserName() != null) {
				po.setpName(user.getUserName());
			} else if (user.getNickName() != null) {
				po.setpName(user.getNickName());
			} else if (user.getLoginName() != null) {
				po.setpName(user.getLoginName());
			} else
				return null;
			po.setIsVerified(1);
			if (user.getDescn() != null) {
				po.setDescn(user.getDescn());
			}
			po.setpSource("我听科技");
			po.setpSrcId("0");
			po.setcTime(sma.getCTime());
			po.setLmTime(sma.getCTime());
			personService.insertPerson(po);
			PersonRefPo poref = new PersonRefPo();
			poref.setId(SequenceUUID.getPureUUID());
			poref.setRefName("主播-专辑");
			poref.setPersonId(userid);
			poref.setResTableName("wt_SeqMediaAsset");
			poref.setResId(sma.getId());
			poref.setcTime(sma.getCTime());
			personService.insertPersonRef(poref);
		}
		mediaService.saveSma(sma);

		if (tags != null && tags.size() > 0) {
			List<KeyWordPo> lk = new ArrayList<>();
			List<KeyWordResPo> ls = new ArrayList<>();
			for (Map<String, Object> m : tags) {
				if (m.containsKey("TagName") && m.containsKey("TagOrg")) {
					KeyWordPo kw = keyWordBaseService.getKeyWordInfoByName(m.get("TagName") + "");
					if (kw != null) {
						KeyWordResPo kwres = new KeyWordResPo();
						kwres.setId(SequenceUUID.getPureUUID());
						kwres.setKwId(kw.getId());
						kwres.setRefName("标签-专辑");
						kwres.setResTableName("wt_SeqMediaAsset");
						kwres.setResId(sma.getId());
						kwres.setcTime(new Timestamp(System.currentTimeMillis()));
						ls.add(kwres);
						if (m.get("TagOrg").equals("我的标签")) {
							KeyWordResPo kwr = new KeyWordResPo();
							kwr.setId(SequenceUUID.getPureUUID());
							kwr.setKwId(kw.getId());
							kwr.setRefName("标签-主播");
							kwr.setResTableName("wt_Person");
							kwr.setResId(po.getId());
							kwr.setcTime(new Timestamp(System.currentTimeMillis()));
							ls.add(kwr);
						}
					} else {
						kw = new KeyWordPo();
						kw.setId(SequenceUUID.getPureUUID());
						kw.setOwnerId(po.getId());
						kw.setOwnerType(1);
						kw.setSort(0);
						kw.setIsValidate(1);
						kw.setKwName(m.get("TagName") + "");
						kw.setnPy(ChineseCharactersUtils.getFullSpellFirstUp(kw.getKwName()));
						kw.setDescn(po.getId() + "主播创建");
						kw.setcTime(new Timestamp(System.currentTimeMillis()));
						lk.add(kw);
						KeyWordResPo kwres = new KeyWordResPo();
						kwres.setId(SequenceUUID.getPureUUID());
						kwres.setKwId(kw.getId());
						kwres.setRefName("标签-专辑");
						kwres.setResTableName("wt_SeqMediaAsset");
						kwres.setResId(sma.getId());
						kwres.setcTime(new Timestamp(System.currentTimeMillis()));
						ls.add(kwres);
						KeyWordResPo kwr = new KeyWordResPo();
						kwr.setId(SequenceUUID.getPureUUID());
						kwr.setKwId(kw.getId());
						kwr.setRefName("标签-主播");
						kwr.setResTableName("wt_Person");
						kwr.setResId(po.getId());
						kwr.setcTime(new Timestamp(System.currentTimeMillis()));
						ls.add(kwr);
					}
				}
			}
			if (lk.size() > 0) {
				keyWordBaseService.insertKeyWords(lk);
			}
			if (ls.size() > 0) {
				keyWordBaseService.insertKwRefs(ls);
			}
		}
		if (memberType != null && memberType.size() > 0) {
			List<ComplexRefPo> cps = new ArrayList<>();
			for (Map<String, Object> m : memberType) {
				ComplexRefPo cp = new ComplexRefPo();
				cp.setId(SequenceUUID.getPureUUID());
				cp.setAssetTableName("wt_SeqMediaAsset");
				cp.setAssetId(sma.getId());
				cp.setResId(m.get("TypeInfo") + "");
				cp.setDictMId("4");
				cp.setDictDId(m.get("TypeId") + "");
				cps.add(cp);
			}
			if (cps != null && cps.size() > 0) {
				complexRefService.insertComplexRef(cps);
			}
		}
		String[] chaids = channelId.split(",");
		for (String chaid : chaids) {
			map = modifySeqStatus(userid, sma.getId(), chaid, 0, null);
		}
		if (mediaService.getSmaInfoById(sma.getId()) != null) {
			if (channelId.equals("null") || map.get("ReturnType").equals("1001"))
				map.clear();
			map.put("ReturnType", "1001");
			map.put("Message", "添加专辑成功");
		} else {
			map.clear();
			map.put("ReturnType", "1011");
			map.put("Message", "添加专辑失败");
		}
		return map;
	}

	/**
	 * 修改专辑信息
	 * 
	 * @param ma
	 * @param sma
	 * @return
	 */
	public Map<String, Object> updateSeqInfo(String userid, String contentid, String contentname, String channelId,
			String contentimg, List<Map<String, Object>> tags, List<Map<String, Object>> memberType, String contentdesc,
			String pubTime) {
		if (personService.getPersonPoById(userid) != null) {
			Map<String, Object> map = new HashMap<String, Object>();
			SeqMediaAssetPo sma = mediaService.getSmaInfoById(contentid);
			if (sma != null) {
				sma.setSmaTitle(contentname);
				if (contentimg != null & !contentimg.toLowerCase().equals("null")) {
					sma.setSmaImg(contentimg);
				}
				if (contentdesc != null & !contentdesc.toLowerCase().equals("null")) {
					sma.setDescn(contentdesc);
				}
				mediaService.updateSma(sma);
				if (tags != null && tags.size() > 0) {
					keyWordBaseService.deleteKeyWordRes(sma.getId(), "wt_SeqMediaAsset");
					List<KeyWordPo> lk = new ArrayList<>();
					List<KeyWordResPo> ls = new ArrayList<>();
					for (Map<String, Object> m : tags) {
						if (m.containsKey("TagName") && m.containsKey("TagOrg")) {
							KeyWordPo kw = keyWordBaseService.getKeyWordInfoByName(m.get("TagName") + "");
							if (kw != null) {
								KeyWordResPo kwres = new KeyWordResPo();
								kwres.setId(SequenceUUID.getPureUUID());
								kwres.setKwId(kw.getId());
								kwres.setRefName("标签-专辑");
								kwres.setResTableName("wt_SeqMediaAsset");
								kwres.setResId(sma.getId());
								kwres.setcTime(new Timestamp(System.currentTimeMillis()));
								ls.add(kwres);
								if (m.get("TagOrg").equals("我的标签")) {
									KeyWordResPo kwr = new KeyWordResPo();
									kwr.setId(SequenceUUID.getPureUUID());
									kwr.setKwId(kw.getId());
									kwr.setRefName("标签-主播");
									kwr.setResTableName("wt_Person");
									kwr.setResId(userid);
									kwr.setcTime(new Timestamp(System.currentTimeMillis()));
									ls.add(kwr);
								}
							} else {
								kw = new KeyWordPo();
								kw.setId(SequenceUUID.getPureUUID());
								kw.setOwnerId(userid);
								kw.setOwnerType(1);
								kw.setSort(0);
								kw.setIsValidate(1);
								kw.setKwName(m.get("TagName") + "");
								kw.setnPy(ChineseCharactersUtils.getFullSpellFirstUp(kw.getKwName()));
								kw.setDescn(userid + "主播创建");
								kw.setcTime(new Timestamp(System.currentTimeMillis()));
								lk.add(kw);
								KeyWordResPo kwres = new KeyWordResPo();
								kwres.setId(SequenceUUID.getPureUUID());
								kwres.setKwId(kw.getId());
								kwres.setRefName("标签-专辑");
								kwres.setResTableName("wt_SeqMediaAsset");
								kwres.setResId(sma.getId());
								kwres.setcTime(new Timestamp(System.currentTimeMillis()));
								ls.add(kwres);
								KeyWordResPo kwr = new KeyWordResPo();
								kwr.setId(SequenceUUID.getPureUUID());
								kwr.setKwId(kw.getId());
								kwr.setRefName("标签-主播");
								kwr.setResTableName("wt_Person");
								kwr.setResId(userid);
								kwr.setcTime(new Timestamp(System.currentTimeMillis()));
								ls.add(kwr);
							}
						}
					}
					if (lk.size() > 0) {
						keyWordBaseService.insertKeyWords(lk);
					}
					if (ls.size() > 0) {
						keyWordBaseService.insertKwRefs(ls);
					}
				}
				if (memberType != null && memberType.size() > 0) {
					complexRefService.deleteComplexRef("wt_SeqMediaAsset", contentid, "4");
					List<ComplexRefPo> cps = new ArrayList<>();
					for (Map<String, Object> m : memberType) {
						ComplexRefPo cp = new ComplexRefPo();
						cp.setId(SequenceUUID.getPureUUID());
						cp.setAssetTableName("wt_SeqMediaAsset");
						cp.setAssetId(sma.getId());
						cp.setResId(m.get("TypeInfo") + "");
						cp.setDictMId("4");
						cp.setDictDId(m.get("TypeId") + "");
						cps.add(cp);
					}
					if (cps != null && cps.size() > 0) {
						complexRefService.insertComplexRef(cps);
					}
				}
				String[] chaids = channelId.split(",");
				List<ChannelAssetPo> chas = mediaService.getCHAInfoByAssetId(sma.getId());
				int flowflag = chas.get(0).getFlowFlag();
				mediaService.removeCha(sma.getId(), "wt_SeqMediaAsset");
				for (String chaid : chaids) {
					modifySeqStatus(userid, sma.getId(), chaid, flowflag, null);
				}
				map.put("ReturnType", "1001");
				map.put("Message", "修改成功");
			} else {
				map.put("ReturnType", "1011");
				map.put("Message", "修改失败");
			}
			return map;
		}
		return null;
	}

	/**
	 * 
	 * @param userid
	 * @param contentId
	 * @param channelId
	 *            创建专辑使用
	 * @param flowflag
	 *            0为创建专辑,2为发布专辑,4为撤回
	 * @param descn 
	 * @return
	 */
	public Map<String, Object> modifySeqStatus(String userid, String contentId, String channelId, int flowflag, String descn) {
		Map<String, Object> map = new HashMap<String, Object>();
		SeqMediaAssetPo sma = mediaService.getSmaInfoById(contentId);
		if (sma == null) {
			map.put("ReturnType", "1013");
			map.put("Message", "专辑不存在");
			return map;
		}
		if (flowflag == 0 && channelId != null) { // flowflag=0 时，为创建专辑
			ChannelAssetPo cha = new ChannelAssetPo();
			cha.setId(SequenceUUID.getPureUUID());
			cha.setChannelId(channelId);
			cha.setAssetId(contentId);
			cha.setAssetType("wt_SeqMediaAsset");
			cha.setFlowFlag(0);
			cha.setIsValidate(1);
			cha.setPubImg(sma.getSmaImg());
			cha.setPubName(sma.getSmaTitle());
			cha.setPublisherId("0");
			cha.setCheckerId("1");
			cha.setSort(0);
			cha.setInRuleIds("etl");
			cha.setCheckRuleIds("etl");
			mediaService.saveCha(cha);
			map.put("ReturnType", "1001");
			map.put("Message", "添加成功");
		} else {
			if (flowflag == 2) {
				List<MediaAssetPo> malist = mediaService.getMaListBySmaId(contentId, 0, 0);
				if (flowflag == 2 && (malist == null || malist.size() == 0)) {
					map.put("ReturnType", "1014");
					map.put("Message", "专辑无下级单体");
					return map;
				}
				List<ChannelAssetPo> chas = mediaService.getCHAListByAssetId("'" + contentId + "'", "wt_SeqMediaAsset");
				if (chas != null && chas.size() > 0) {
					for (ChannelAssetPo cha : chas) {
						if (flowflag!=2) {
							cha.setFlowFlag(1);
							cha.setPubTime(new Timestamp(System.currentTimeMillis()));
						} else {
							cha.setPubTime(new Timestamp(System.currentTimeMillis()));
						}
						// 发布专辑
						mediaService.updateCha(cha);
						insertChannelAssetProgress(cha.getId(), 1, descn);
					}
					// 发布专辑下级节目
					for (MediaAssetPo mediaAssetPo : malist) {
						mediaContentService.modifyMediaStatus(userid, mediaAssetPo.getId(), contentId, flowflag, descn);
					}
					map.put("ReturnType", "1001");
					map.put("Message", "专辑发布成功");
				} else {
					map.put("ReturnType", "1011");
					map.put("Message", "专辑发布失败");
				}
			} else {
				if (flowflag==4) {
					List<ChannelAssetPo> chas = mediaService.getCHAListByAssetId("'" + contentId + "'", "wt_SeqMediaAsset");
					if (chas != null && chas.size() > 0) {
						for (ChannelAssetPo channelAssetPo : chas) {
							insertChannelAssetProgress(channelAssetPo.getId(), 2, descn);
							map.put("ReturnType", "1001");
							map.put("Message", "申请撤回成功");
						}
					}
				} else {
					map.put("ReturnType", "1011");
					map.put("Message", "修改失败");
				}
			}
		}
		return map;
	}
	
	public List<Map<String, Object>> modifySeqStatus(String userid, List<Map<String, Object>> updateList, int flowflag) {
		if (updateList!=null && updateList.size()>0) {
			List<Map<String, Object>> retList = new ArrayList<>();
			for (Map<String, Object> map : updateList) {
				try {
					Map<String, Object> retMap = new HashMap<>();
					String contentId = map.get("ContentId").toString();
					String descn = null;
					try {descn = map.get("ApplyDescn").toString();} catch (Exception e) {}
					SeqMediaAssetPo sma = mediaService.getSmaInfoById(contentId);
					if (sma == null) {
						retMap.put("ReturnType", "1013");
						retMap.put("ContentId", contentId);
						retMap.put("Message", "专辑不存在");
						retList.add(retMap);
					}
					if (flowflag == 2) {
						List<MediaAssetPo> malist = mediaService.getMaListBySmaId(contentId, 0, 0);
						if (flowflag == 2 && (malist == null || malist.size() == 0)) {
							retMap.put("ReturnType", "1014");
							retMap.put("ContentId", contentId);
							retMap.put("Message", "专辑无下级单体");
							retList.add(retMap);
						}
						List<ChannelAssetPo> chas = mediaService.getCHAListByAssetId("'" + contentId + "'", "wt_SeqMediaAsset");
						if (chas != null && chas.size() > 0) {
							for (ChannelAssetPo cha : chas) {
								if (flowflag!=2) {
									cha.setFlowFlag(1);
									cha.setPubTime(new Timestamp(System.currentTimeMillis()));
								} else {
									cha.setPubTime(new Timestamp(System.currentTimeMillis()));
								}
								// 发布专辑
								mediaService.updateCha(cha);
								insertChannelAssetProgress(cha.getId(), 1, descn);
							}
							// 发布专辑下级节目
							for (MediaAssetPo mediaAssetPo : malist) {
								mediaContentService.modifyMediaStatus(userid, mediaAssetPo.getId(), contentId, flowflag, descn);
							}
							retMap.put("ReturnType", "1001");
							retMap.put("Message", "专辑发布成功");
							retMap.put("ContentId", contentId);
							retList.add(retMap);
						} else {
							retMap.put("ReturnType", "1011");
							retMap.put("Message", "专辑发布失败");
							retMap.put("ContentId", contentId);
							retList.add(retMap);
						}
					} else {
						if (flowflag==4) {
							List<ChannelAssetPo> chas = mediaService.getCHAListByAssetId("'" + contentId + "'", "wt_SeqMediaAsset");
							if (chas != null && chas.size() > 0) {
								for (ChannelAssetPo channelAssetPo : chas) {
									insertChannelAssetProgress(channelAssetPo.getId(), 2, descn);
									retMap.put("ReturnType", "1001");
									retMap.put("Message", "申请撤回成功");
									retMap.put("ContentId", contentId);
									retList.add(retMap);
								}
							}
						} else {
							retMap.put("ReturnType", "1011");
							retMap.put("Message", "修改失败");
							retMap.put("ContentId", contentId);
							retList.add(retMap);
						}
					}
			    } catch (Exception e) {
				    continue;
			    }
		    }
			return retList;
		}
		return null;
	}

	public List<Map<String, Object>> removeSeqMediaAsset(String userId, String contentids) {
		String[] ids = contentids.split(",");
		List<Map<String, Object>> retLs = new ArrayList<>();
		for (String id : ids) {
			try {
				mediaService.removeSeqMedia(userId, id);
			} catch (Exception e) {}
			finally {
				Map<String, Object> map = new HashMap<String, Object>();
				if (mediaService.getSmaInfoById(id) != null) {
					map.put("ContentId", id);
					map.put("Message", "专辑删除失败");
				} else {
					map.put("ContentId", id);
					map.put("Message", "专辑删除成功");
				}
				retLs.add(map);
			}
		}
		return retLs;
	}

	public void removeMaToSmaRefInfo(String userId, String seqMediaId, String mediaAssetId) {
		mediaService.removeMa2SmaByMid(mediaAssetId);
	}

	public Map<String, Object> getSeqMediaAssetInfo(String userId, String contentId) {
		Map<String, Object> map = new HashMap<>();
		map.put("id", userId);
		if (personService.getPersonPoById(userId) != null) {
			PersonRefPo poref = personService.getPersonRefBy("wt_SeqMediaAsset", contentId);
			if (poref.getPersonId().equals(userId)) {
				List<ChannelAssetPo> chas = mediaService.getChaByAssetIdAndPubId("0", contentId, "wt_SeqMediaAsset");
				if (chas != null && chas.size() > 0) {
					SeqMediaAssetPo sma = mediaService.getSmaInfoById(contentId);
					if (sma != null) {
						List<SeqMediaAssetPo> smas = new ArrayList<>();
						smas.add(sma);
						List<Map<String, Object>> ls = mediaService.makeSmaListToReturn(smas);
						if (ls != null && ls.size() > 0) {
							List<MediaAssetPo> mas = mediaService.getMaListBySmaId(contentId, 0, 0);
							if (mas != null && mas.size() > 0) {
								List<Map<String, Object>> mam = mediaService.makeMaListToReturn(mas);
								ls.get(0).put("MediaAssetList", mam);
								return ls.get(0);
							}
						}
						return ls.get(0);
					}
				}
			}
		}
		return null;
	}
	
	private void insertChannelAssetProgress(String channelAssetId,int applyFlowFlag, String applyDescn) {
		ChannelAssetProgressPo cPo = new ChannelAssetProgressPo();
		cPo.setId(SequenceUUID.getPureUUID());
		cPo.setChaId(channelAssetId);
		cPo.setApplyFlowFlag(applyFlowFlag);
		cPo.setApplyDescn(applyDescn);
		channelAssetProgressService.insertChannelAssetProgress(cPo);
	}
}
