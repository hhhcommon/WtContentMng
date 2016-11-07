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
import com.woting.cm.core.channel.model.Channel;
import com.woting.cm.core.channel.model.ChannelAsset;
import com.woting.cm.core.keyword.persis.po.KeyWordPo;
import com.woting.cm.core.keyword.persis.po.KeyWordResPo;
import com.woting.cm.core.keyword.service.KeyWordBaseService;
import com.woting.cm.core.media.model.MediaAsset;
import com.woting.cm.core.media.model.SeqMediaAsset;
import com.woting.cm.core.media.persis.po.MediaAssetPo;
import com.woting.cm.core.media.persis.po.SeqMaRefPo;
import com.woting.cm.core.media.service.MediaService;
import com.woting.content.manage.dict.service.DictContentService;
import com.woting.content.manage.keyword.service.KeyWordProService;
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

	/**
	 * 查询主播的资源列表
	 * 
	 * @param userid
	 * @param mediatype
	 * @return
	 */
	public List<Map<String, Object>> getHostSeqMediaContents(String userid, String flagflow, String channelid, String shortsearch) {
		if (shortsearch.equals("true")) {
			List<Map<String, Object>> l = mediaService.getShortSmaListByPubId(userid);
			return l;
		} else {
			if (flagflow.equals("0") && channelid.equals("0")) {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				list = mediaService.getSmaListByPubId(userid);
				if(list!=null && list.size()>0) return list;
				else return null;
			} else {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				list = mediaService.getSmaListByPubId(userid,flagflow,channelid);
				if(list!=null && list.size()>0) return list;
				else return null;
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
	public Map<String, Object> addSeqMediaInfo(String userid, String contentname, String channelId, String contentimg, List<Map<String, Object>> tags,
			String contentdesc, String pubTime) {
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
		if (user==null) {
			return null;
		}
		sma.setSmaPublisher(user.getLoginName());
//		DictDetail detail = new DictDetail();
//		detail.setId("zho");
//		detail.setNodeName("中文");
//		sma.setLang(detail);
//		sma.setPubCount(0);
		mediaService.saveSma(sma);
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
		if(!channelId.equals("null"))
			map = modifySeqStatus(userid, sma.getId(), channelId, 0);
		if (mediaService.getSmaInfoById(sma.getId()) != null) {
			if(channelId.equals("null")||map.get("ReturnType").equals("1001"))
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
	public Map<String, Object> updateSeqInfo(String userid, SeqMediaAsset sma, String did, String chid) {
		Map<String, Object> map = new HashMap<String,Object>();
		List<MediaAssetPo> malist = mediaService.getMaListBySmaId(sma.getId());
		if(mediaService.getSmaInfoById(sma.getId())!=null) {
			mediaService.updateSma(sma);
			if(malist!=null&&malist.size()>0){
				for (MediaAssetPo mapo : malist) {
					MediaAsset ma = new MediaAsset();
					ma.buildFromPo(mapo);
					mediaService.bindMa2Sma(ma, sma);
				}
			}
			List<SeqMaRefPo> l = mediaService.getSmaListBySid(sma.getId());
			if(!did.toLowerCase().equals("null")){
				mediaService.removeResDictRef(sma.getId());
				dictContentService.addCataLogs("3", did, "wt_SeqMediaAsset", sma.getId());
				if(l!=null&&l.size()>0)
				    for (SeqMaRefPo seqMaRefPo : l) { // 查询专辑下级信息，删除下级单体的内容分类数据信息，重新写入wt_ResDict_Ref表内
					    mediaService.removeResDictRef(seqMaRefPo.getMId());
					    dictContentService.addCataLogs("3", did, "wt_MediaAsset", seqMaRefPo.getMId());
				    }
			}
			if(!chid.toLowerCase().equals("null")){
				ChannelAsset cha = mediaService.getCHAInfoByAssetId(sma.getId());
				if(cha!=null){
					int flowflag = cha.getFlowFlag();
					mediaService.removeCha(sma.getId());
					modifySeqStatus(userid, sma.getId(), chid, flowflag);
				}
			}
			map.put("ReturnType", "1001");
		    map.put("Message", "修改成功");
		}else{
			map.put("ReturnType", "1011");
		    map.put("Message", "修改失败");
		}
		return map;
	}
	
	public Map<String, Object> modifySeqStatus(String userid, String smaid, String chid, int flowflag) {
		Map<String, Object> map = new HashMap<String, Object>();
		SeqMediaAsset sma = mediaService.getSmaInfoById(smaid);
		if (sma == null) {
			map.put("ReturnType", "1011");
			map.put("Message", "专辑不存在");
			return map;
		}
		Channel ch = mediaService.getChInfoById(chid);
		if (ch == null) {
			map.put("ReturnType", "1011");
			map.put("Message", "栏目不存在");
			return map;
		}
		List<MediaAssetPo> malist = mediaService.getMaListBySmaId(smaid);
		if(flowflag!=0 && (malist==null||malist.size()==0)) {
			map.put("ReturnType", "1011");
			map.put("Message", "专辑无下级单体");
			return map;
		}
		ChannelAsset cha = mediaService.getCHAInfoByAssetId(smaid);
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
		    cha.setPubObj(sma);
		    cha.setPublisherId(userid);
		    cha.setCheckerId("1");
		    cha.setFlowFlag(flowflag);
		    cha.setSort(0);
		    cha.setCheckRuleIds("0");
		    cha.setCTime(new Timestamp(System.currentTimeMillis()));
		    if(flowflag==2) {
		    	cha.setPubTime(new Timestamp(System.currentTimeMillis()));
		    }
		    cha.setIsValidate(1);
		    cha.setInRuleIds("elt");
		    cha.setCheckRuleIds("elt");
		    //发布专辑
		    mediaService.saveCha(cha);
		}
		
		//发布专辑下级节目
		for (MediaAssetPo mediaAssetPo : malist) {
			mediaContentService.modifyMediaStatus(userid, mediaAssetPo.getId(), smaid, flowflag);
		}
		
		if (mediaService.getCHAInfoById(cha.getId()) != null) {
			map.put("ReturnType", "1001");
			map.put("Message", "专辑发布成功");
			SeqMediaAsset sma2 = new SeqMediaAsset();
			sma2.setId(smaid);
			sma2.setPubCount(sma.getPubCount() + 1);
			mediaService.updateSma(sma2);
		} else {
			map.put("ReturnType", "1011");
			map.put("Message", "专辑发布失败");
		}
		return map;
	}
	
	
	public Map<String, Object> removeSeqMediaAsset(String contentid) {
		Map<String, Object> map = new HashMap<String,Object>();
		mediaService.removeSeqMedia(contentid);
		if(mediaService.getSmaInfoById(contentid)!=null){
			map.put("ReturnType", "1011");
			map.put("Message", "专辑删除失败");
		}else{
			map.put("ReturnType", "1001");
			map.put("Message", "专辑删除成功");
		}
		return map;
	}
}
