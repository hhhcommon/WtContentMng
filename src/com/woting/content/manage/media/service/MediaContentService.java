package com.woting.content.manage.media.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.spiritdata.framework.util.SequenceUUID;
import com.woting.cm.core.channel.model.Channel;
import com.woting.cm.core.channel.model.ChannelAsset;
import com.woting.cm.core.media.model.MaSource;
import com.woting.cm.core.media.model.MediaAsset;
import com.woting.cm.core.media.model.SeqMediaAsset;
import com.woting.cm.core.media.persis.po.SeqMaRefPo;
import com.woting.cm.core.media.service.MediaService;
import com.woting.content.manage.dict.service.DictContentService;

@Service
public class MediaContentService {
	@Resource
	private MediaService mediaService;
	@Resource
	private DictContentService dictContentService;
	
	/**
	 * 查询主播的资源列表
	 * 
	 * @param userid
	 * @param mediatype
	 * @return
	 */
	public Map<String, Object> getHostMediaContents(String userid) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = mediaService.getMaInfoByMaPubId(userid);
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
	public Map<String, Object> addMediaInfo(String userid, String username, String maname, String maimg, String maurl,
			String keywords, String madesc, String seqid, String seqname) {
		Map<String, Object> map = new HashMap<String, Object>();
		String maid = SequenceUUID.getPureUUID();
		String sequtitle = seqname + "KeyWords";
		Timestamp ctime = new Timestamp(System.currentTimeMillis()); // 节目创建时间

		MediaAsset ma = new MediaAsset();
		ma.setId(maid);
		ma.setMaTitle(maname);
		ma.setMaImg(maimg.toLowerCase().equals("null") ? "www.wotingfm.com:908/CM/mweb/templet/zj_templet/imgs/default.png" : maimg.replace("D:\\workIDE\\work\\WtContentMng\\WebContent\\", "localhost:908/CM/"));
		ma.setMaURL(maurl);
		ma.setKeyWords("上传文件测试用待删除");
		ma.setMaPubType(3);
		ma.setMaPubId(userid);
		ma.setMaPublisher(username);
		ma.setDescn(madesc.toLowerCase().equals("null") ? "这家伙真懒，什么都没留下" : madesc);
		ma.setPubCount(0);
		ma.setCTime(ctime);

		// 保存单体资源
		mediaService.saveMa(ma);

		// 保存专辑与单体媒体对应表
		if (!seqid.toLowerCase().equals("null")) {
			SeqMediaAsset sma = new SeqMediaAsset();
			sma.setId(seqid);
			sma.setSmaTitle(sequtitle);
			mediaService.bindMa2Sma(ma, sma);
			List<Map<String, Object>> catalist = mediaService.getResDictRefByResId("'"+seqid+"'", "wt_SeqMediaAsset");
			if(catalist!=null&&catalist.size()>0)
				for (Map<String, Object> map2 : catalist) {
					dictContentService.addCataLogs("3", map2.get("dictDid")+"", "wt_MediaAsset", maid);
				}
		}

		// 保存资源来源表里
		MaSource maSource = new MaSource();
		maSource.setMa(ma);
		maSource.setId(SequenceUUID.getPureUUID());
		maSource.setMaSrcType(3);
		maSource.setMaSrcId(userid);
		maSource.setMaSource(username);
		maSource.setSmType(1);
		maSource.setPlayURI(maurl);
		maSource.setIsMain(1);
		maSource.setDescn("上传文件测试用待删除");
		maSource.setCTime(ctime);
		mediaService.saveMas(maSource);

		if (mediaService.getMaInfoById(maid) != null) {
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
			    SeqMaRefPo seqmapo = new SeqMaRefPo();
			    seqmapo.setMId(mediaService.getSeqMaRefByMId(ma.getId()).getMId());
			    seqmapo.setSId(sma.getId());
			    seqmapo.setCTime(new Timestamp(System.currentTimeMillis()));
			    mediaService.updateSeqMaRef(seqmapo); // 待修改wt_Masource,wt_ResDict_Ref,wt_ChannelAsset
		    }
		    map.put("ReturnType", "1001");
		    map.put("Message", "修改成功");
		}else {
			map.put("ReturnType", "1011");
		    map.put("Message", "修改失败");
		}
		return map;
	}
	
	public Map<String, Object> modifyMediaStatus(String userid, String maid, String maname, String chid, String madesc,
			String maimg) {
		Map<String, Object> map = new HashMap<String, Object>();
		MediaAsset ma = mediaService.getMaInfoById(maid);
		if (ma == null) {
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
		cha.setPubObj(ma);
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
			MediaAsset ma2 = new MediaAsset();
			ma2.setId(maid);
			ma2.setPubCount(ma.getPubCount() + 1);
			mediaService.updateMa(ma2);
		} else {
			map.put("ReturnType", "1011");
			map.put("Message", "专辑发布失败");
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
