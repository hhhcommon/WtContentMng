package com.woting.content.manage.media.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

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
import com.woting.cm.core.media.model.MaSource;
import com.woting.cm.core.media.model.MediaAsset;
import com.woting.cm.core.media.persis.po.MaSourcePo;
import com.woting.cm.core.media.persis.po.MediaAssetPo;
import com.woting.cm.core.media.persis.po.SeqMaRefPo;
import com.woting.cm.core.media.persis.po.SeqMediaAssetPo;
import com.woting.cm.core.media.service.MediaService;
import com.woting.cm.core.person.persis.po.PersonPo;
import com.woting.cm.core.person.persis.po.PersonRefPo;
import com.woting.cm.core.person.service.PersonService;
import com.woting.cm.core.subscribe.SubscribeThread;
import com.woting.content.manage.dict.service.DictContentService;
import com.woting.content.manage.seqmedia.service.SeqContentService;
import com.woting.passport.UGA.persis.pojo.UserPo;
import com.woting.passport.UGA.service.UserService;

@Service
public class MediaContentService {
	@Resource
	private MediaService mediaService;
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
	public Map<String, Object> getMediaContents(String userid, String flowflag, String channelid, String seqmediaid, int page, int pageSize) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> retMap = mediaService.getMaListByPubId(userid, flowflag, channelid, seqmediaid, page, pageSize);
		if (retMap != null) {
			map.put("ResultList", retMap.get("List"));
			map.put("AllCount", retMap.get("allCount"));
			map.put("ReturnType", "1001");
			return map;
		} else return null;
	}

	/**
	 * 上传单体节目
	 * 
	 * @param upfiles
	 * @param uploadmap
	 * @return
	 */
	public Map<String, Object> addMediaAssetInfo(String userid, String contentname, String contentimg, String seqid,
			String timelong, String contenturi, List<Map<String, Object>> tags, List<Map<String, Object>> memberType,
			String contentdesc, String pubTime, String flowFlag) {
		UserPo user = userService.getUserById(userid);
		if (user == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		MediaAsset ma = new MediaAsset();
		ma.setId(SequenceUUID.getPureUUID());
		ma.setMaTitle(contentname);
		ma.setMaImg(contentimg);
		ma.setMaURL(contenturi);
		ma.setTimeLong(Long.valueOf(timelong));
		// ma.setKeyWords("上传文件测试用待删除");
		ma.setMaPubType(1);// 主播
		ma.setMaPubId("0");
		ma.setMaPublisher("我听科技");
		ma.setDescn(contentdesc.toLowerCase().equals("null") ? "这家伙真懒，什么都没留下" : contentdesc);
		ma.setPubCount(0);
		ma.setMaStatus(1);
		ma.setCTime(new Timestamp(System.currentTimeMillis()));
		PersonPo po = personService.getPersonPoById(userid);
		if (po != null) {
			PersonRefPo poref = new PersonRefPo();
			poref.setId(SequenceUUID.getPureUUID());
			poref.setRefName("主播-节目");
			poref.setPersonId(userid);
			poref.setResTableName("wt_MediaAsset");
			poref.setResId(ma.getId());
			poref.setcTime(ma.getCTime());
			personService.insertPersonRef(poref);
		} else {
			po = new PersonPo();
			po.setId(userid);
			if (user.getPortraitBig()!=null && user.getPortraitBig().length()>5)
				po.setPortrait(user.getPortraitBig());
			if (user.getUserName() != null) {
				po.setpName(user.getUserName());
			} else if (user.getNickName() != null) {
				po.setpName(user.getNickName());
			} else if (user.getLoginName() != null) {
				po.setpName(user.getLoginName());
			} else return null;
			po.setIsVerified(1);
			if (user.getDescn() != null) {
				po.setDescn(user.getDescn());
			}
			po.setpSource("我听科技");
			po.setpSrcId("0");
			po.setcTime(ma.getCTime());
			po.setLmTime(ma.getCTime());
			personService.insertPerson(po);
			PersonRefPo poref = new PersonRefPo();
			poref.setId(SequenceUUID.getPureUUID());
			poref.setRefName("主播-节目");
			poref.setPersonId(po.getId());
			poref.setResTableName("wt_MediaAsset");
			poref.setResId(ma.getId());
			poref.setcTime(ma.getCTime());
			personService.insertPersonRef(poref);
		}

		// 保存单体资源
		mediaService.saveMa(ma);
		SeqMediaAssetPo smaPo;
		// 保存专辑与单体媒体对应表
		if (seqid != null) {
			smaPo = mediaService.getSmaInfoById(seqid);
			mediaService.bindMa2Sma(ma.convert2Po(), smaPo);
		} else {
			smaPo = mediaService.getSmaInfoById("user=" + userid);
			if (smaPo == null) {
				String smaName = "";
				if (user.getUserName() != null) {
					smaName = user.getUserName();
				} else if (user.getNickName() != null) {
					smaName = user.getNickName();
				} else if (user.getLoginName() != null) {
					smaName = user.getLoginName();
				}
				seqid = "user=" + userid;
				seqContentService.addSeqMediaInfo(seqid, userid, smaName + "的默认专辑", "cn36", null, null, null, null,
						null);
				smaPo = mediaService.getSmaInfoById(seqid);
				mediaService.bindMa2Sma(ma.convert2Po(), smaPo);
			} else {
				mediaService.bindMa2Sma(ma.convert2Po(), smaPo);
			}
		}

		// 保存资源来源表里
		MaSource maSource = new MaSource();
		maSource.setMa(ma);
		maSource.setId(SequenceUUID.getPureUUID());
		maSource.setMaSrcType(1);
		maSource.setMaSrcId("0");
		maSource.setMaSource("我听科技");
		maSource.setSmType(1);
		maSource.setPlayURI(contenturi);
		maSource.setIsMain(1);
		maSource.setCTime(ma.getCTime());
		mediaService.saveMas(maSource);

		// 保存标签信息
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
							kwr.setResTableName("wt_Person");
							kwr.setResId(po.getId());
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
				cp.setcTime(new Timestamp(System.currentTimeMillis()));
				cps.add(cp);
			}
			complexRefService.insertComplexRef(cps);
		}

		// 新增栏目
		modifyMediaStatus(userid, ma.getId(), smaPo.getId(), 0, null);

		if (flowFlag.equals("2")) {
			modifyMediaStatus(userid, ma.getId(), seqid, 2, null);
		}

		if (mediaService.getMaInfoById(ma.getId()) != null) {
			map.put("ReturnType", "1001");
			map.put("Message", "添加节目成功");
		} else {
			map.put("ReturnType", "1011");
			map.put("Message", "添加节目失败");
		}
		return map;
	}

	/**
	 * 修改单体信息
	 * 
	 * @param userid
	 * @param contentId
	 * @param contentname
	 * @param contentimg
	 * @param seqmediaId
	 * @param timelong
	 * @param contenturi
	 * @param tags
	 * @param memberType
	 * @param contentdesc
	 * @param pubTime
	 * @return
	 */
	public Map<String, Object> updateMediaInfo(String userid, String contentId, String contentname, String contentimg,
			String seqmediaId, String timelong, String contenturi, List<Map<String, Object>> tags,
			List<Map<String, Object>> memberType, String contentdesc, String pubTime) {
		Map<String, Object> map = new HashMap<>();
		List<ChannelAssetPo> channelAssetPos = mediaService.getChaByAssetIdAndPubId(userid, contentId, "wt_MediaAsset");
		if (channelAssetPos != null && channelAssetPos.size() > 0) {
			if (channelAssetPos.get(0).getFlowFlag() != 0) {
				map.put("ReturnType", "1015");
				map.put("Message", "资源已发布");
				return map;
			}
		}

		MediaAssetPo ma = mediaService.getMaInfoById(contentId);
		if (contentname != null && !contentname.toLowerCase().equals("null")) { // 修改节目名称
			ma.setMaTitle(contentname);
		}
		if (contentimg != null && !contentimg.toLowerCase().equals("null")) { // 修改题图地址
			ma.setMaImg(contentimg);
		}
		if (contentdesc != null && !contentdesc.toLowerCase().equals("null")) { // 修改题图地址
			ma.setDescn(contentdesc);
		}
		if (timelong != null && !timelong.toLowerCase().equals("null")) { // 修改题图地址
			ma.setTimeLong(Long.valueOf(timelong));
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
		modifyMediaStatus(userid, ma.getId(), seqmediaId, 0, null);

		// 删除标签
		keyWordBaseService.deleteKeyWordRes(contentId, "wt_MediaAsset");
		// 保存标签信息
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
						kwres.setRefName("标签-节目");
						kwres.setResTableName("wt_MediaAsset");
						kwres.setResId(ma.getId());
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
				cp.setcTime(new Timestamp(System.currentTimeMillis()));
				cps.add(cp);
			}
			if (cps != null && cps.size() > 0) {
				complexRefService.insertComplexRef(cps);
			}
		}
		map.put("ReturnType", "1001");
		map.put("Message", "修改成功");
		return map;
	}
	
	public boolean modifyMediaStatus(String userid, String mediaId, String seqMediaId, int flowflag, String descn) {
		SeqMediaAssetPo sma = mediaService.getSmaInfoById(seqMediaId);
		MediaAssetPo ma = mediaService.getMaInfoById(mediaId);
		if (sma != null) {
			SeqMaRefPo seqmapo = mediaService.getSeqMaRefByMId(mediaId);
			if (seqmapo != null) {
				if (flowflag == 0) { // 创建节目和修改节目使用
					if (!seqmapo.getSId().equals(seqMediaId)) { // 修改节目对应专辑
						mediaService.removeMa2SmaByMid(mediaId);
						mediaService.bindMa2Sma(ma, sma);
						List<ChannelAssetPo> chas = mediaService.getCHAListByAssetId("'" + sma.getId() + "'",
								"wt_SeqMediaAsset");
						if (chas != null && chas.size() > 0) {
							mediaService.removeCha(ma.getId(), "wt_MediaAsset");
							for (ChannelAssetPo cha : chas) {
								ChannelAssetPo macha = new ChannelAssetPo();
								macha.setId(SequenceUUID.getPureUUID());
								macha.setChannelId(cha.getChannelId());
								macha.setPublisherId("0");
								macha.setCheckerId("1");
								macha.setAssetId(ma.getId());
								macha.setAssetType("wt_MediaAsset");
								macha.setFlowFlag(flowflag);
								macha.setSort(0);
								macha.setPubName(ma.getMaTitle());
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
					} else { // 创建节目
						List<ChannelAssetPo> chas = mediaService.getCHAListByAssetId("'" + sma.getId() + "'",
								"wt_SeqMediaAsset");
						if (chas != null && chas.size() > 0) {
							mediaService.removeCha(ma.getId(), "wt_MediaAsset");
							for (ChannelAssetPo cha : chas) {
								ChannelAssetPo macha = new ChannelAssetPo();
								macha.setId(SequenceUUID.getPureUUID());
								macha.setChannelId(cha.getChannelId());
								macha.setPublisherId("0");
								macha.setCheckerId("1");
								macha.setPubName(ma.getMaTitle());
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
					}
				} else {
					if (flowflag == 2) { // 发布节目
						List<ChannelAssetPo> smachas = mediaService.getCHAListByAssetId("'" + sma.getId() + "'",
								"wt_SeqMediaAsset");
						if (smachas != null && smachas.size() > 0) { // 判断节目绑定专辑是否发布
							for (ChannelAssetPo cha : smachas) {
								if (cha.getFlowFlag() != flowflag) {
									cha.setFlowFlag(1);
									cha.setPubTime(new Timestamp(System.currentTimeMillis()));
									mediaService.updateCha(cha);
									insertChannelAssetProgress(cha.getId(), 1, descn);
								} else {
									cha.setPubTime(new Timestamp(System.currentTimeMillis()));
									mediaService.updateCha(cha);
								}
							}
						} else return false;
						List<ChannelAssetPo> machas = mediaService.getCHAListByAssetId("'" + mediaId + "'", "wt_MediaAsset");
						if (machas != null && machas.size() > 0) { // 修改栏目发布表里节目发布信息
							for (ChannelAssetPo cha : machas) {
								if (cha.getFlowFlag() != flowflag) {
									cha.setFlowFlag(1);
									cha.setPubTime(new Timestamp(System.currentTimeMillis()));
									mediaService.updateCha(cha);
									insertChannelAssetProgress(cha.getId(), 1, descn);
								} else {
									cha.setPubTime(new Timestamp(System.currentTimeMillis()));
									mediaService.updateCha(cha);
								}
							}
						} else return false;
						new SubscribeThread(mediaId).start();
						return true;
					} else if (flowflag == 4) {
						List<ChannelAssetPo> machas = mediaService.getCHAListByAssetId("'" + mediaId + "'", "wt_MediaAsset");
						if (machas != null && machas.size() > 0) { // 修改栏目发布表里节目发布信息
							for (ChannelAssetPo cha : machas) {
								insertChannelAssetProgress(cha.getId(), 2, descn);
							}
							return true;
						} else return false;
					}
				}
			}
		}
		return false;
	}

	public List<Map<String, Object>> modifyMediaStatus(String userid, List<Map<String, Object>> updateList, int flowflag) {
		if (updateList!=null && updateList.size()>0) {
			List<Map<String, Object>> retList = new ArrayList<>();
			for (Map<String, Object> map : updateList) {
				try {
					String mediaId = map.get("ContentId").toString();
					String seqMediaId = map.get("SeqMediaId").toString();
					String descn = null;
					try {descn = map.get("ApplyDescn").toString();} catch (Exception e) {}
					SeqMediaAssetPo sma = mediaService.getSmaInfoById(seqMediaId);
					MediaAssetPo ma = mediaService.getMaInfoById(mediaId);
					if (sma != null) {
						SeqMaRefPo seqmapo = mediaService.getSeqMaRefByMId(mediaId);
						if (seqmapo != null) {
							if (flowflag == 0) { // 创建节目和修改节目使用
								if (!seqmapo.getSId().equals(seqMediaId)) { // 修改节目对应专辑
									mediaService.removeMa2SmaByMid(mediaId);
									mediaService.bindMa2Sma(ma, sma);
									List<ChannelAssetPo> chas = mediaService.getCHAListByAssetId("'" + sma.getId() + "'", "wt_SeqMediaAsset");
									if (chas != null && chas.size() > 0) {
										mediaService.removeCha(ma.getId(), "wt_MediaAsset");
										for (ChannelAssetPo cha : chas) {
											ChannelAssetPo macha = new ChannelAssetPo();
											macha.setId(SequenceUUID.getPureUUID());
											macha.setChannelId(cha.getChannelId());
											macha.setPublisherId("0");
											macha.setCheckerId("1");
											macha.setAssetId(ma.getId());
											macha.setAssetType("wt_MediaAsset");
											macha.setFlowFlag(flowflag);
											macha.setSort(0);
											macha.setPubName(ma.getMaTitle());
											macha.setPubImg(ma.getMaImg());
											macha.setCheckRuleIds("0");
											macha.setCTime(new Timestamp(System.currentTimeMillis()));
											macha.setIsValidate(1);
											macha.setInRuleIds("elt");
											macha.setCheckRuleIds("elt");
											mediaService.saveCha(macha);
										}
										Map<String, Object> retM = new HashMap<>();
										retM.put("ContentId", mediaId);
										retM.put("ReturnType", "1001");
										retList.add(retM);
									}
								} else { // 创建节目
									List<ChannelAssetPo> chas = mediaService.getCHAListByAssetId("'" + sma.getId() + "'",
											"wt_SeqMediaAsset");
									if (chas != null && chas.size() > 0) {
										mediaService.removeCha(ma.getId(), "wt_MediaAsset");
										for (ChannelAssetPo cha : chas) {
											ChannelAssetPo macha = new ChannelAssetPo();
											macha.setId(SequenceUUID.getPureUUID());
											macha.setChannelId(cha.getChannelId());
											macha.setPublisherId("0");
											macha.setCheckerId("1");
											macha.setPubName(ma.getMaTitle());
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
										Map<String, Object> retM = new HashMap<>();
										retM.put("ContentId", mediaId);
										retM.put("ReturnType", "1001");
										retList.add(retM);
									}
								}
							} else {
								if (flowflag == 2) { // 发布节目
									List<ChannelAssetPo> smachas = mediaService.getCHAListByAssetId("'" + sma.getId() + "'",
											"wt_SeqMediaAsset");
									if (smachas != null && smachas.size() > 0) { // 判断节目绑定专辑是否发布
										for (ChannelAssetPo cha : smachas) {
											if (cha.getFlowFlag() != flowflag) {
												cha.setFlowFlag(1);
												cha.setPubTime(new Timestamp(System.currentTimeMillis()));
												mediaService.updateCha(cha);
												insertChannelAssetProgress(cha.getId(), 1, descn);
											} else {
												cha.setPubTime(new Timestamp(System.currentTimeMillis()));
												mediaService.updateCha(cha);
											}
										}
									} else {
										Map<String, Object> retM = new HashMap<>();
										retM.put("ContentId", mediaId);
										retM.put("ReturnType", "1011");
										retM.put("Message", "发布失败");
										retList.add(retM);
									}
									List<ChannelAssetPo> machas = mediaService.getCHAListByAssetId("'" + mediaId + "'", "wt_MediaAsset");
									if (machas != null && machas.size() > 0) { // 修改栏目发布表里节目发布信息
										for (ChannelAssetPo cha : machas) {
											if (cha.getFlowFlag() != flowflag) {
												cha.setFlowFlag(1);
												cha.setPubTime(new Timestamp(System.currentTimeMillis()));
												mediaService.updateCha(cha);
												insertChannelAssetProgress(cha.getId(), 1, descn);
											} else {
												cha.setPubTime(new Timestamp(System.currentTimeMillis()));
												mediaService.updateCha(cha);
											}
										}
									} else {
										Map<String, Object> retM = new HashMap<>();
										retM.put("ContentId", mediaId);
										retM.put("ReturnType", "1011");
										retM.put("Message", "发布失败");
										retList.add(retM);
									}
									new SubscribeThread(mediaId).start();
									Map<String, Object> retM = new HashMap<>();
									retM.put("ContentId", mediaId);
									retM.put("ReturnType", "1001");
									retList.add(retM);
								} else if (flowflag == 4) {
									List<ChannelAssetPo> machas = mediaService.getCHAListByAssetId("'" + mediaId + "'", "wt_MediaAsset");
									if (machas != null && machas.size() > 0) { // 修改栏目发布表里节目发布信息
										for (ChannelAssetPo cha : machas) {
											insertChannelAssetProgress(cha.getId(), 2, descn);
										}
										Map<String, Object> retM = new HashMap<>();
										retM.put("ContentId", mediaId);
										retM.put("ReturnType", "1001");
										retList.add(retM);
									} else {
										Map<String, Object> retM = new HashMap<>();
										retM.put("ContentId", mediaId);
										retM.put("ReturnType", "1011");
										retM.put("Message", "发布失败");
										retList.add(retM);
									}
								}
							}
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

	public Map<String, Object> removeMediaAsset(String userId, String contentids) {
		Map<String, Object> map = new HashMap<String, Object>();
		String[] ids = contentids.split(",");
		for (String id : ids) {
			mediaService.removeMedia(userId, id);
		}
		map.put("ReturnType", "1001");
		map.put("Message", "单体成功");
		return map;
	}

	public Map<String, Object> getMediaAssetInfo(String userId, String contentId) {
		Map<String, Object> map = new HashMap<>();
		map.put("id", userId);
		if (personService.getPersonPoById(userId) != null) {
			PersonRefPo poref = personService.getPersonRefBy("wt_MediaAsset", contentId);
			if (poref.getPersonId().equals(userId)) {
				List<ChannelAssetPo> chas = mediaService.getChaByAssetIdAndPubId("0", contentId, "wt_MediaAsset");
				if (chas != null && chas.size() > 0) {
					MediaAssetPo ma = mediaService.getMaInfoById(contentId);
					if (ma != null) {
						List<MediaAssetPo> mas = new ArrayList<>();
						mas.add(ma);
						List<Map<String, Object>> rem = mediaService.makeMaListToReturn(mas);
						if (rem != null && rem.size() > 0) {
							return rem.get(0);
						}
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
