package com.woting.content.manage.seqmedia.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.spiritdata.framework.util.ChineseCharactersUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.woting.cm.core.channel.model.ChannelAsset;
import com.woting.cm.core.channel.persis.po.ChannelAssetPo;
import com.woting.cm.core.complexref.persis.po.ComplexRefPo;
import com.woting.cm.core.complexref.service.ComplexRefService;
import com.woting.cm.core.keyword.persis.po.KeyWordPo;
import com.woting.cm.core.keyword.persis.po.KeyWordResPo;
import com.woting.cm.core.keyword.service.KeyWordBaseService;
import com.woting.cm.core.media.model.SeqMediaAsset;
import com.woting.cm.core.media.persis.po.MediaAssetPo;
import com.woting.cm.core.media.persis.po.SeqMediaAssetPo;
import com.woting.cm.core.media.service.MediaService;
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

	/**
	 * 查询主播的资源列表
	 * 
	 * @param userid
	 * @param mediatype
	 * @return
	 */
	public List<Map<String, Object>> getHostSeqMediaContents(String userid, String flagflow, String channelid,
			String shortsearch) {
		if (shortsearch.equals("true")) {
			List<Map<String, Object>> l = mediaService.getShortSmaListByPubId(userid);
			return l;
		} else {
			if (flagflow.equals("0") && channelid.equals("0")) {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				list = mediaService.getSmaListByPubId(userid);
				if (list != null && list.size() > 0)
					return list;
				else
					return null;
			} else {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				list = mediaService.getSmaListByPubId(userid, flagflow, channelid);
				if (list != null && list.size() > 0)
					return list;
				else
					return null;
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
	public Map<String, Object> addSeqMediaInfo(String userid, String contentname, String channelId, String contentimg,
			List<Map<String, Object>> tags, List<Map<String, Object>> memberType, String contentdesc, String pubTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 保存专辑信息到资源库
		SeqMediaAsset sma = new SeqMediaAsset();
		sma.setId(SequenceUUID.getPureUUID());
		sma.setSmaTitle(contentname);
		sma.setSmaImg(contentimg);
		sma.setDescn(contentdesc.toLowerCase().equals("null") ? "这家伙真懒，什么都没留下" : contentdesc);
		sma.setSmaStatus(1);
		sma.setCTime(new Timestamp(System.currentTimeMillis()));
		sma.setSmaPubType(3);
		sma.setSmaPubId(userid);
		UserPo user = userService.getUserById(userid);
		if (user == null) {
			return null;
		}
		sma.setSmaPublisher(user.getLoginName());
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
							kwr.setResTableName("palt_User");
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
						kwr.setResTableName("palt_User");
						kwr.setResId(userid);
						kwr.setcTime(new Timestamp(System.currentTimeMillis()));
						ls.add(kwr);
					}
				}
			}
			if (lk.size()>0) {
				keyWordBaseService.insertKeyWords(lk);
			}
			if (ls.size()>0) {
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
		if (!channelId.equals("null")) {
			map = modifySeqStatus(userid, sma.getId(), channelId, 0);
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
		Map<String, Object> map = new HashMap<String, Object>();
		SeqMediaAsset sma = mediaService.getSmaInfoById(contentid);
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
								kwr.setResTableName("palt_User");
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
							kwr.setResTableName("palt_User");
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
			if (channelId != null & !channelId.toLowerCase().equals("null")) {
				ChannelAsset cha = mediaService.getCHAInfoByAssetId(sma.getId());
				if (cha != null) {
					int flowflag = cha.getFlowFlag();
					if (!cha.getCh().getId().equals(channelId)) {
						mediaService.removeCha(sma.getId(), "wt_SeqMediaAsset");
						modifySeqStatus(userid, sma.getId(), channelId, flowflag);
					}

				}
			}
			map.put("ReturnType", "1001");
			map.put("Message", "修改成功");
		} else {
			map.put("ReturnType", "1011");
			map.put("Message", "修改失败");
		}
		return map;
	}

	/**
	 * 
	 * @param userid
	 * @param contentId
	 * @param channelId
	 *            创建专辑使用
	 * @param flowflag
	 *            0为创建专辑，2为发布专辑
	 * @return
	 */
	public Map<String, Object> modifySeqStatus(String userid, String contentId, String channelId, int flowflag) {
		Map<String, Object> map = new HashMap<String, Object>();
		SeqMediaAsset sma = mediaService.getSmaInfoById(contentId);
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
			cha.setPublisherId(userid);
			cha.setCheckerId("1");
			cha.setSort(0);
			cha.setInRuleIds("etl");
			cha.setCheckRuleIds("etl");
			mediaService.saveCha(cha);
			map.put("ReturnType", "1001");
			map.put("Message", "添加成功");
		} else {
			if (flowflag == 2) {
				List<MediaAssetPo> malist = mediaService.getMaListBySmaId(contentId);
				if (flowflag == 2 && (malist == null || malist.size() == 0)) {
					map.put("ReturnType", "1014");
					map.put("Message", "专辑无下级单体");
					return map;
				}
				List<ChannelAssetPo> chas = mediaService.getCHAListByAssetId("'" + contentId + "'", "wt_SeqMediaAsset");
				if (chas != null && chas.size() > 0) {
					for (ChannelAssetPo cha : chas) {
						cha.setFlowFlag(flowflag);
						if (flowflag == 2) {
							cha.setPubTime(new Timestamp(System.currentTimeMillis()));
						}
						// 发布专辑
						mediaService.updateCha(cha);
					}
					// 发布专辑下级节目
					for (MediaAssetPo mediaAssetPo : malist) {
						mediaContentService.modifyMediaStatus(userid, mediaAssetPo.getId(), contentId, flowflag);
					}
					map.put("ReturnType", "1001");
					map.put("Message", "专辑发布成功");
				} else {
					map.put("ReturnType", "1011");
					map.put("Message", "专辑发布失败");
				}
			} else {
				map.put("ReturnType", "1011");
				map.put("Message", "修改失败");
			}
		}
		return map;
	}

	public Map<String, Object> removeSeqMediaAsset(String contentid) {
		Map<String, Object> map = new HashMap<String, Object>();
		mediaService.removeSeqMedia(contentid);
		if (mediaService.getSmaInfoById(contentid) != null) {
			map.put("ReturnType", "1011");
			map.put("Message", "专辑删除失败");
		} else {
			map.put("ReturnType", "1001");
			map.put("Message", "专辑删除成功");
		}
		return map;
	}
	
	public void removeMaToSmaRefInfo(String userId, String seqMediaId, String mediaAssetId) {
		mediaService.removeMa2SmaByMid(mediaAssetId);
	}

	public Map<String, Object> getSeqMediaAssetInfo(String userId, String contentId) {
		List<ChannelAssetPo> chas = mediaService.getChaByAssetIdAndPubId(userId, contentId);
		if (chas != null && chas.size() > 0) {
			SeqMediaAsset sma = mediaService.getSmaInfoById(contentId);
			if (sma != null) {
				List<SeqMediaAssetPo> smas = new ArrayList<>();
				smas.add(sma.convert2Po());
				List<Map<String, Object>> ls = mediaService.makeSmaListToReturn(smas);
				if (ls != null && ls.size() > 0) {
					List<MediaAssetPo> mas = mediaService.getMaListBySmaId(contentId);
					if (mas!=null && mas.size()>0) {
						List<Map<String, Object>> mam = mediaService.makeMaListToReturn(mas);
						ls.get(0).put("MediaAssetList", mam);
						return ls.get(0);
					}
				}
				return ls.get(0);
			}
		}
		return null;
	}
}
