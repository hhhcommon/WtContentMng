package com.woting.content.manage.seq.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.cm.core.channel.model.Channel;
import com.woting.cm.core.channel.model.ChannelAsset;
import com.woting.cm.core.dict.model.DictDetail;
import com.woting.cm.core.media.model.MediaAsset;
import com.woting.cm.core.media.model.SeqMediaAsset;
import com.woting.cm.core.media.service.MediaService;
import com.woting.content.manage.service.ContentService;

public class SeqContentService {
	@Resource
	private MediaService mediaService;
	@Resource
	private ContentService contentService;

	/**
	 * 查询主播的资源列表
	 * 
	 * @param userid
	 * @param mediatype
	 * @return
	 */
	public Map<String, Object> getHostSeqMediaContents(String userid) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = mediaService.getSmaInfoBySmaPubId(userid);
		if (list != null && list.size() > 0) {
			map.put("List", list);
			map.put("AllCount", list.size());
			map.put("ReturnType", "1001");
		}
		return map;
	}
	
	/**
	 * 创建专辑
	 * 
	 * @param upfile
	 * @param m
	 * @return
	 */
	public Map<String, Object> addSeqInfo(String userid, String username, String smaname, String smaimg,
			String did, String smadesc, List<Map<String, Object>> malist) {
		Map<String, Object> map = new HashMap<String, Object>();

		// 保存专辑信息到资源库
		String smaid = SequenceUUID.getPureUUID();
		SeqMediaAsset sma = new SeqMediaAsset();
		sma.setId(smaid);
		String smatitle = smaname;
		if (StringUtils.isNullOrEmptyOrSpace(smatitle) || smatitle.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无专辑名称");
			return map;
		}
		sma.setSmaTitle(smatitle);
		if (smaimg.toLowerCase().equals("null"))
			smaimg = "www.wotingfm.com:908/CM/mweb/templet/zj_templet/imgs/default.png";
		sma.setSmaImg(smaimg);
		sma.setDescn(smadesc.toLowerCase().equals("null") ? "这家伙真懒，什么都没留下" : smadesc);

		// 保存专辑与单体媒体对应关系
		if (malist != null && malist.size() > 0) {
			for (Map<String, Object> m2 : malist) {
				MediaAsset ma = new MediaAsset();
				ma.setId(m2.get("ContentId") + "");
				ma.setMaTitle(m2.get("ContentName") + "");
				mediaService.bindMa2Sma(ma, sma);
			}
			sma.setSmaAllCount(malist.size());
		}
		sma.setCTime(new Timestamp(System.currentTimeMillis()));
		sma.setSmaPubType(3);
		sma.setSmaPubId(userid);
		sma.setSmaPublisher(username);
		DictDetail detail = new DictDetail();
		detail.setId("zho");
		detail.setNodeName("中文");
		sma.setLang(detail);
		sma.setPubCount(0);
		mediaService.saveSma(sma);
		
		if(!did.toLowerCase().equals("null")) 
			contentService.addCataLogs("3", did, "wt_SeqMediaAsset", smaid);
		
		if (mediaService.getSmaInfoById(smaid) != null) {
			map.put("ReturnType", "1001");
			map.put("Message", "添加专辑成功");
		} else {
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
	public Map<String, Object> updateSeqInfo(SeqMediaAsset sma) {
		Map<String, Object> map = new HashMap<String,Object>();
		if(mediaService.getSmaInfoById(sma.getId())!=null) {
			mediaService.updateSma(sma); // 待修改wt_SeqMa_Ref,wt_ResDict_Ref,wt_ChannelAsset
			map.put("ReturnType", "1001");
		    map.put("Message", "修改成功");
		}else{
			map.put("ReturnType", "1011");
		    map.put("Message", "修改失败");
		}
		return map;
	}
	
	public Map<String, Object> modifySeqStatus(String userid, String smaid, String smaname, String chid, String smadesc,
			String smaimg) {
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
		ChannelAsset cha = new ChannelAsset();
		String chaid = SequenceUUID.getPureUUID();
		cha.setId(chaid);
		cha.setCh(ch);
		cha.setPubObj(sma);
		cha.setPublisherId(userid);
		cha.setCheckerId("1");
		cha.setFlowFlag(2);
		cha.setSort(0);
		cha.setCheckRuleIds("0");
		cha.setCTime(new Timestamp(System.currentTimeMillis()));
		cha.setIsValidate(1);
		cha.setInRuleIds("elt");
		cha.setCheckRuleIds("elt");
		mediaService.saveCHA(cha);
		if (mediaService.getCHAInfoById(chaid) != null) {
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
	
	public void removeSeqMedia(String contentid) {
		mediaService.removeSma(contentid);
		mediaService.removeMa2Sma(contentid);
		mediaService.removeResDictRef(contentid);
		mediaService.removeCha(contentid);
	}
}
