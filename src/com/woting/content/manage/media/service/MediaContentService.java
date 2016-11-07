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
import com.woting.cm.core.channel.model.Channel;
import com.woting.cm.core.channel.model.ChannelAsset;
import com.woting.cm.core.keyword.persis.po.KeyWordPo;
import com.woting.cm.core.keyword.persis.po.KeyWordResPo;
import com.woting.cm.core.keyword.service.KeyWordBaseService;
import com.woting.cm.core.media.model.MaSource;
import com.woting.cm.core.media.model.MediaAsset;
import com.woting.cm.core.media.model.SeqMediaAsset;
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
	public Map<String, Object> addMediaAssetInfo(String userid,  String contentname, String contentimg, String seqid, String contenturi,
			List<Map<String, Object>> tags, List<Map<String, Object>> memberType,String contentdesc, String pubTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		MediaAsset ma = new MediaAsset();
		ma.setId(SequenceUUID.getPureUUID());
		ma.setMaTitle(contentname);
		ma.setMaImg(contentimg);
		ma.setMaURL(contenturi);
//		ma.setKeyWords("上传文件测试用待删除");
		ma.setMaPubType(3);//主播
		ma.setMaPubId(userid);
		UserPo user = userService.getUserById(userid);
		if (user==null) {
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
		if (!seqid.toLowerCase().equals("null")) {
			SeqMediaAsset sma = mediaService.getSmaInfoById(seqid);
			mediaService.bindMa2Sma(ma, sma);
			if (tags!=null && tags.size()>0) {
				List<KeyWordPo> lk = new ArrayList<>();
				List<KeyWordResPo> ls = new ArrayList<>();
				for (Map<String, Object> m : tags) {
					KeyWordPo kw = keyWordBaseService.getKeyWordInfoByName(m.get("TagName")+"");
					if (kw!=null) {
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
						kw.setKwName(m.get("TagName")+"");
						kw.setnPy(ChineseCharactersUtils.getFullSpellFirstUp(kw.getKwName()));
						kw.setDescn(userid+"主播创建");
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
				keyWordBaseService.insertKeyWords(lk);
				keyWordBaseService.insertKwRefs(ls);
			}
		}

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
		
		// 保存创作方式信息
		if (memberType!=null && memberType.size()>0) {
			for (Map<String, Object> m : memberType) {
				dictContentService.insertResDictRef("创作方式-"+m.get("TypeName"), "wt_MediaAsset", ma.getId(), "4", m.get("TypeId")+"");
			}
		}
		
		// 获取专辑分类
		ChannelAsset chasma = mediaService.getCHAInfoByAssetId(seqid);
		if(chasma!=null) 
			modifyMediaStatus(userid, ma.getId(), seqid, chasma.getFlowFlag());
		
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
	public Map<String, Object> updateMediaInfo(MediaAsset ma, SeqMediaAsset sma) {
		Map<String, Object> map = new HashMap<String,Object>();
		if(mediaService.getMaInfoById(ma.getId())!=null){
			mediaService.updateMa(ma);
		    if (sma!=null) {
		    	if (mediaService.getSeqMaRefByMId(ma.getId())!=null) {
					SeqMaRefPo seqmapo = new SeqMaRefPo();
			        seqmapo.setMId(ma.getId());
			        seqmapo.setSId(sma.getId());
			        seqmapo.setCTime(new Timestamp(System.currentTimeMillis()));
			        mediaService.updateSeqMaRef(seqmapo); // 待修改wt_Masource,wt_ResDict_Ref,wt_ChannelAsset
				}else{
					sma = mediaService.getSmaInfoById(sma.getId());
			        mediaService.bindMa2Sma(ma, sma);
				}
		    }
		    map.put("ReturnType", "1001");
		    map.put("Message", "修改成功");
		}else {
			map.put("ReturnType", "1011");
		    map.put("Message", "修改失败");
		}
		return map;
	}
	
	public Map<String, Object> modifyMediaStatus(String userid, String maid, String smaid, int flowflag) {
		Map<String, Object> map = new HashMap<String, Object>();
		MediaAsset ma = mediaService.getMaInfoById(maid);
		if (ma == null) {
			map.put("ReturnType", "1011");
			map.put("Message", "节目不存在");
			return map;
		}
		ChannelAsset chasma = mediaService.getCHAInfoByAssetId(smaid);
		if(chasma==null) {
			map.put("ReturnType", "1011");
			map.put("Message", "未查询到专辑发布信息");
			return map;
		}
		Channel ch = chasma.getCh();
		if (ch == null) {
			map.put("ReturnType", "1011");
			map.put("Message", "栏目不存在");
			return map;
		}
		ChannelAsset cha = mediaService.getCHAInfoByAssetId(maid);
		if(cha!=null){
			cha.setFlowFlag(flowflag);
			cha.setCh(ch);
			if(flowflag==2)
				cha.setPubTime(new Timestamp(System.currentTimeMillis()));
			mediaService.updateCha(cha);
		}else{
			cha = new ChannelAsset();
		    String chaid = SequenceUUID.getPureUUID();
		    cha.setId(chaid);
		    cha.setCh(ch);
		    cha.setPubObj(ma);
		    cha.setPublisherId(userid);
		    cha.setCheckerId("1");
		    cha.setFlowFlag(flowflag);
		    cha.setSort(0);
		    cha.setPubImg(ma.getMaImg());
		    cha.setCheckRuleIds("0");
		    cha.setCTime(new Timestamp(System.currentTimeMillis()));
		    if(flowflag==2) {
		    	cha.setPubTime(new Timestamp(System.currentTimeMillis()));
		    }
		    cha.setIsValidate(1);
		    cha.setInRuleIds("elt");
		    cha.setCheckRuleIds("elt");
		    mediaService.saveCha(cha);
		}
		if (flowflag==2&&chasma.getFlowFlag()==0) {
			chasma.setFlowFlag(flowflag);
			mediaService.updateCha(chasma);
		}
		if (mediaService.getCHAInfoById(cha.getId()) != null) {
			map.put("ReturnType", "1001");
			map.put("Message", "节目发布成功");
			MediaAsset ma2 = new MediaAsset();
			ma2.setId(maid);
			ma2.setPubCount(ma.getPubCount() + 1);
			mediaService.updateMa(ma2);
		} else {
			map.put("ReturnType", "1011");
			map.put("Message", "节目发布失败");
		}
		return map;
	}
	
	public Map<String, Object> removeMediaAsset(String contentid) {
		Map<String, Object> map = new HashMap<String,Object>();
		mediaService.removeMedia(contentid);
		if(mediaService.getMaInfoById(contentid)!=null){
			map.put("ReturnType", "1011");
			map.put("Message", "单体删除失败");
		}else{
			map.put("ReturnType", "1001");
			map.put("Message", "单体删除成功");
		}
		return map;
	}
}
