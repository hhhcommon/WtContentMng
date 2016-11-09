package com.woting.content.manage.media.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import com.spiritdata.framework.util.ChineseCharactersUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.woting.cm.core.channel.model.Channel;
import com.woting.cm.core.channel.model.ChannelAsset;
import com.woting.cm.core.channel.persis.po.ChannelAssetPo;
import com.woting.cm.core.complexref.persis.po.ComplexRefPo;
import com.woting.cm.core.complexref.service.ComplexRefService;
import com.woting.cm.core.keyword.persis.po.KeyWordPo;
import com.woting.cm.core.keyword.persis.po.KeyWordResPo;
import com.woting.cm.core.keyword.service.KeyWordBaseService;
import com.woting.cm.core.media.model.MaSource;
import com.woting.cm.core.media.model.MediaAsset;
import com.woting.cm.core.media.model.SeqMediaAsset;
import com.woting.cm.core.media.persis.po.MaSourcePo;
import com.woting.cm.core.media.persis.po.MediaAssetPo;
import com.woting.cm.core.media.persis.po.SeqMaRefPo;
import com.woting.content.manage.dict.service.DictContentService;
import com.woting.content.manage.seqmedia.service.SeqContentService;
import com.woting.passport.UGA.persis.pojo.UserPo;
import com.woting.passport.UGA.service.UserService;

@Service
public class MediaContentService {
	@Resource
	private com.woting.cm.core.media.service.MediaService mediaService;
	@Resource
	private DictContentService dictContentService;
	@Resource
	private SeqContentService seqContentService;
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
	public Map<String, Object> getMediaContents(String userid, String flowflag, String channelid, String seqmediaid) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = mediaService.getMaListByPubId(userid, flowflag, channelid, seqmediaid);
		if (list != null && list.size() > 0) {
			map.put("List", list);
			map.put("AllCount", list.size());
			map.put("ReturnType", "1001");
		}
		return map;
	}

	/**
	 * 上传单体节目
	 * 
	 * @param upfiles
	 * @param uploadmap
	 * @return
	 */
	public Map<String, Object> addMediaAssetInfo(String userid, String contentname, String contentimg, String seqid,
			String contenturi, List<Map<String, Object>> tags, List<Map<String, Object>> memberType, String contentdesc,
			String pubTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		MediaAsset ma = new MediaAsset();
		ma.setId(SequenceUUID.getPureUUID());
		ma.setMaTitle(contentname);
		ma.setMaImg(contentimg);
		ma.setMaURL(contenturi);
		// ma.setKeyWords("上传文件测试用待删除");
		ma.setMaPubType(3);// 主播
		ma.setMaPubId(userid);
		UserPo user = userService.getUserById(userid);
		if (user == null) {
			return null;
		}
		ma.setMaPublisher(user.getLoginName());
		ma.setDescn(contentdesc.toLowerCase().equals("null") ? "这家伙真懒，什么都没留下" : contentdesc);
		ma.setPubCount(0);
		ma.setMaStatus(1);
		ma.setCTime(new Timestamp(System.currentTimeMillis()));

		// 保存单体资源
		mediaService.saveMa(ma);
		// 保存专辑与单体媒体对应表
		SeqMediaAsset sma = mediaService.getSmaInfoById(seqid);
		mediaService.bindMa2Sma(ma, sma);

		// 保存资源来源表里
		MaSource maSource = new MaSource();
		maSource.setMa(ma);
		maSource.setId(SequenceUUID.getPureUUID());
		maSource.setMaSrcType(3);
		maSource.setMaSrcId(userid);
		maSource.setMaSource(user.getLoginName());
		maSource.setSmType(1);
		maSource.setPlayURI(contenturi);
		maSource.setIsMain(1);
		maSource.setDescn("上传文件测试用待删除");
		maSource.setCTime(ma.getCTime());
		mediaService.saveMas(maSource);

		// 保存标签信息
		if (tags != null && tags.size() > 0) {
			List<KeyWordPo> lk = new ArrayList<>();
			List<KeyWordResPo> ls = new ArrayList<>();
			for (Map<String, Object> m : tags) {
				KeyWordPo kw = keyWordBaseService.getKeyWordInfoByName(m.get("TagName") + "");
				if (kw != null) {
					KeyWordResPo kwres = new KeyWordResPo();
					kwres.setId(SequenceUUID.getPureUUID());
					kwres.setKwId(kw.getId());
					kwres.setRefName("标签-节目");
					kwres.setResTableName("wt_MediaAsset");
					kwres.setResId(ma.getId());
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
					kwres.setRefName("标签-节目");
					kwres.setResTableName("wt_MediaAsset");
					kwres.setResId(ma.getId());
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
			keyWordBaseService.insertKeyWords(lk);
			keyWordBaseService.insertKwRefs(ls);
		}

		// 保存创作方式信息
		if (memberType != null && memberType.size() > 0) {
			List<ComplexRefPo> cps = new ArrayList<>();
			for (Map<String, Object> m : memberType) {
				ComplexRefPo cp = new ComplexRefPo();
				cp.setId(SequenceUUID.getPureUUID());
				cp.setAssetTableName("wt_MediaAsset");
				cp.setAssetId(ma.getId());
				cp.setResId(m.get("TypeInfo") + "");
				cp.setDictMId("4");
				cp.setDictDId(m.get("TypeId") + "");
				cps.add(cp);
			}
			complexRefService.insertComplexRef(cps);
		}

		// 获取专辑分类
		modifyMediaStatus(userid, ma.getId(), sma.getId(), 0);

		if (mediaService.getMaInfoById(ma.getId()) != null) {
			map.put("ReturnType", "1001");
			map.put("Message", "上传文件成功");
		} else {
			map.put("ReturnType", "1011");
			map.put("Message", "上传失败");
		}
		return map;
	}

	/**
	 * 修改单体信息
	 * 
	 * @param ma
	 * @param sma
	 * @return
	 */
	public Map<String, Object> updateMediaInfo(String userid, String contentId, String contentname, String contentimg,
			String seqmediaId, String contenturi, List<Map<String, Object>> tags, List<Map<String, Object>> memberType,
			String contentdesc, String pubTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		MediaAsset ma = mediaService.getMaInfoById(contentId);
		if (ma != null) {
			if (contentname != null && !contentname.toLowerCase().equals("null")) { // 修改节目名称
				ma.setMaTitle(contentname);
			}
			if (contentimg != null && !contentimg.toLowerCase().equals("null")) { // 修改题图地址
				ma.setMaImg(contentimg);
			}
			if (contentdesc != null && !contentdesc.toLowerCase().equals("null")) { // 修改题图地址
				ma.setDescn(contentdesc);
			}
			if (contenturi != null && !contenturi.toLowerCase().equals("null")) { // 修改播放地址
				ma.setMaURL(contenturi);
				MaSourcePo mas = mediaService.getMasInfoByMaId(contentId);
				mas.setPlayURI(contenturi);
				mas.setCTime(new Timestamp(System.currentTimeMillis()));
				mediaService.updateMas(mas);
			}
			mediaService.updateMa(ma);

			// 修改节目绑定栏目信息
			modifyMediaStatus(userid, ma.getId(), seqmediaId, 0);

			// 删除标签
			keyWordBaseService.deleteKeyWordRes(contentId, "wt_MediaAsset");
			// 保存标签信息
			if (tags != null && tags.size() > 0) {
				List<KeyWordPo> lk = new ArrayList<>();
				List<KeyWordResPo> ls = new ArrayList<>();
				for (Map<String, Object> m : tags) {
					KeyWordPo kw = keyWordBaseService.getKeyWordInfoByName(m.get("TagName") + "");
					if (kw != null) {
						KeyWordResPo kwres = new KeyWordResPo();
						kwres.setId(SequenceUUID.getPureUUID());
						kwres.setKwId(kw.getId());
						kwres.setRefName("标签-节目");
						kwres.setResTableName("wt_MediaAsset");
						kwres.setResId(ma.getId());
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
						kwres.setRefName("标签-节目");
						kwres.setResTableName("wt_MediaAsset");
						kwres.setResId(ma.getId());
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
				keyWordBaseService.insertKeyWords(lk);
				keyWordBaseService.insertKwRefs(ls);
			}

			// 删除创作方式
			complexRefService.deleteComplexRef("wt_MediaAsset", contentId, "4");
			// 修改创作方式信息
			if (memberType != null && memberType.size() > 0) {
				List<ComplexRefPo> cps = new ArrayList<>();
				for (Map<String, Object> m : memberType) {
					ComplexRefPo cp = new ComplexRefPo();
					cp.setId(SequenceUUID.getPureUUID());
					cp.setAssetTableName("wt_MediaAsset");
					cp.setAssetId(ma.getId());
					cp.setResId(m.get("TypeInfo") + "");
					cp.setDictMId("4");
					cp.setDictDId(m.get("TypeId") + "");
					cps.add(cp);
				}
				if (cps != null && cps.size() > 0) {
					complexRefService.insertComplexRef(cps);
				}

			}
		}
		return map;
	}

	public boolean modifyMediaStatus(String userid, String mediaId, String seqMediaId, int flowflag) {
		SeqMediaAsset sma = mediaService.getSmaInfoById(seqMediaId);
		MediaAsset ma = mediaService.getMaInfoById(mediaId);
		if (sma != null) {
			SeqMaRefPo seqmapo = mediaService.getSeqMaRefByMId(mediaId);
			if (seqmapo != null) {
				if (flowflag == 0) {
					List<ChannelAssetPo> chas = mediaService.getCHAListByAssetId("'"+sma.getId()+"'", "wt_SeqMediaAsset");
					if (chas != null && chas.size() > 0) {
						mediaService.removeCha(ma.getId(), "wt_MediaAsset");
						for (ChannelAssetPo cha : chas) {
							ChannelAssetPo macha = new ChannelAssetPo();
							macha.setId(SequenceUUID.getPureUUID());
							macha.setChannelId(cha.getChannelId());
							macha.setPublisherId(userid);
							macha.setCheckerId("1");
							macha.setFlowFlag(flowflag);
							macha.setAssetId(ma.getId());
							macha.setAssetType("wt_MediaAsset");
							macha.setSort(0);
							macha.setPubImg(ma.getMaImg());
							macha.setCheckRuleIds("0");
							macha.setCTime(new Timestamp(System.currentTimeMillis()));
							macha.setIsValidate(1);
							macha.setInRuleIds("elt");
							macha.setCheckRuleIds("elt");
							mediaService.saveCha(macha);
						}
						return true;
					}
				} else {
					if (!seqmapo.getSId().equals(seqMediaId)) {
						mediaService.removeMa2SmaByMid(mediaId);
						mediaService.bindMa2Sma(ma, sma);
						List<ChannelAssetPo> chas = mediaService.getCHAListByAssetId("'"+sma.getId()+"'", "wt_SeqMediaAsset");
						if (chas != null && chas.size() > 0) {
							mediaService.removeCha(ma.getId(), "wt_MediaAsset");
							for (ChannelAssetPo cha : chas) {
								ChannelAssetPo macha = new ChannelAssetPo();
								macha.setId(SequenceUUID.getPureUUID());
								macha.setChannelId(cha.getChannelId());
								macha.setPublisherId(userid);
								macha.setCheckerId("1");
								macha.setAssetId(ma.getId());
								macha.setAssetType("wt_MediaAsset");
								macha.setFlowFlag(flowflag);
								macha.setSort(0);
								macha.setPubImg(ma.getMaImg());
								macha.setCheckRuleIds("0");
								macha.setCTime(new Timestamp(System.currentTimeMillis()));
								macha.setIsValidate(1);
								macha.setInRuleIds("elt");
								macha.setCheckRuleIds("elt");
								mediaService.saveCha(macha);
							}
							return true;
						}
					} else {
						List<ChannelAssetPo> smachas = mediaService.getCHAListByAssetId("'" + sma.getId() + "'",
								"wt_SeqMediaAsset");
						if (smachas != null && smachas.size() > 0) {
							for (ChannelAssetPo cha : smachas) {
								if (cha.getFlowFlag() != flowflag) {
									cha.setFlowFlag(flowflag);
									if (flowflag == 2) {
										cha.setPubTime(new Timestamp(System.currentTimeMillis()));
									}
									mediaService.updateCha(cha);
								}
							}
						} else return false;
						List<ChannelAssetPo> machas = mediaService.getCHAListByAssetId("'" + mediaId + "'",
								"wt_MediaAsset");
						if (machas != null && machas.size() > 0) {
							for (ChannelAssetPo cha : machas) {
								if (cha.getFlowFlag() != flowflag) {
									cha.setFlowFlag(flowflag);
									if (flowflag == 2) {
										cha.setPubTime(new Timestamp(System.currentTimeMillis()));
									}
									mediaService.updateCha(cha);
								}
							}
						} else return false;
						return true;
					}
				}
			}
		}
		return false;
	}

	public Map<String, Object> removeMediaAsset(String contentid) {
		Map<String, Object> map = new HashMap<String, Object>();
		mediaService.removeMedia(contentid);
		if (mediaService.getMaInfoById(contentid) != null) {
			map.put("ReturnType", "1011");
			map.put("Message", "单体删除失败");
		} else {
			map.put("ReturnType", "1001");
			map.put("Message", "单体删除成功");
		}
		return map;
	}

	public Map<String, Object> getMediaAssetInfo(String userId, String contentId) {
		List<ChannelAssetPo> chas = mediaService.getChaByAssetIdAndPubId(userId, contentId);
		if (chas != null && chas.size() > 0) {
			MediaAsset ma = mediaService.getMaInfoById(contentId);
			if (ma != null) {
				List<MediaAssetPo> mas = new ArrayList<>();
				mas.add(ma.convert2Po());
				List<Map<String, Object>> rem = mediaService.makeMaListToReturn(mas);
				if (rem != null && rem.size() > 0) {
					return rem.get(0);
				}
			}
		}
		return null;
	}
}
