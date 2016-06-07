package com.woting.content.manage.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.ui.tree.EasyUiTree;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.WtContentMngConstants;
import com.woting.cm.core.channel.model.Channel;
import com.woting.cm.core.channel.model.ChannelAsset;
import com.woting.cm.core.dict.mem._CacheDictionary;
import com.woting.cm.core.dict.model.DictDetail;
import com.woting.cm.core.dict.model.DictModel;
import com.woting.cm.core.dict.model.DictRefRes;
import com.woting.cm.core.media.model.MaSource;
import com.woting.cm.core.media.model.MediaAsset;
import com.woting.cm.core.media.model.SeqMediaAsset;
import com.woting.cm.core.media.persis.po.SeqMaRefPo;
import com.woting.cm.core.media.service.MediaService;

@Service
public class ContentService {
	@Resource
	private MediaService mediaService;

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
			List<Map<String, Object>> catalist = mediaService.getResDictRefByResId(seqid, "wt_SeqMediaAsset");
			if(catalist!=null&&catalist.size()>0)
				for (Map<String, Object> map2 : catalist) {
					addCataLogs("3", map2.get("dictDid")+"", "wt_MediaAsset", maid);
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
		    if (sma != null) {
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

	/**
	 * 创建专辑
	 * 
	 * @param upfile
	 * @param m
	 * @return
	 */
	public Map<String, Object> addSequInfo(String userid, String username, String smaname, String smaimg,
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
			addCataLogs("3", did, "wt_SeqMediaAsset", smaid);
		
		if (mediaService.getSmaInfoById(smaid) != null) {
			map.put("ReturnType", "1001");
			map.put("Message", "添加专辑成功");
		} else {
			map.put("ReturnType", "1011");
			map.put("Message", "添加专辑失败");
		}
		return map;
	}

	public void addCataLogs(String mid, String did, String mediatype, String assetid) {
		// 保存专辑分类信息到wt_ResDict_Ref
		try {
			_CacheDictionary _cd = ((CacheEle<_CacheDictionary>) SystemCache.getCache(WtContentMngConstants.CACHE_DICT))
					.getContent();
			DictModel dm = _cd.getDictModelById(mid);
			EasyUiTree<DictDetail> eu1 = new EasyUiTree<DictDetail>(dm.dictTree);
			Map<String, Object> m = eu1.toTreeMap();
			String refname = "";
			String restablename = "";
			if (mediatype.equals("wt_MediaAsset")) {
				refname = "单体-内容分类";
				restablename = "wt_MediaAsset";
			} else {
				if (mediatype.equals("wt_SeqMediaAsset")) {
					refname = "专辑-内容分类";
					restablename = "wt_SeqMediaAsset";
				} else {
					if(mediatype.equals("wt_Broadcast")) {
						refname = "电台-内容分类";
						restablename = "wt_Broadcast";
					}
				}
			}
			List<Map<String, Object>> chillist = (List<Map<String, Object>>) m.get("children");
			for (Map<String, Object> map2 : chillist) {
				if (map2.get("id").equals(did)) {
					DictRefRes dicres = new DictRefRes();
					dicres.setId(SequenceUUID.getPureUUID());
					dicres.setRefName(refname);
					dicres.setResTableName(restablename);
					dicres.setResId(assetid);
					dicres.setDm(dm);
					DictDetail dicd = new DictDetail();
					dicd.setId(map2.get("id") + "");
					dicd.setMId(mid);
					Map<String, Object> l = (Map<String, Object>) map2.get("attributes");
					dicd.setBCode(l.get("bCode") + "");
					dicd.setDdName(l.get("nodeName") + "");
					dicres.setDd(dicd);
					dicres.setCTime(new Timestamp(System.currentTimeMillis()));
					mediaService.saveDictRef(dicres);
				}
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询主播的资源列表
	 * 
	 * @param userid
	 * @param mediatype
	 * @return
	 */
	public Map<String, Object> getContents(String userid, String mediatype) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (mediatype.equals("wt_MediaAsset"))
			list = mediaService.getMaInfoByMaPubId(userid);
		if (mediatype.equals("wt_SeqMediaAsset"))
			list = mediaService.getSmaInfoBySmaPubId(userid);
		if (list != null && list.size() > 0) {
			map.put("List", list);
			map.put("AllCount", list.size());
			map.put("ReturnType", "1001");
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

	public void removeMediaAsset(String contentid) {
		mediaService.removeMa(contentid);
		mediaService.removeMas(contentid);
		mediaService.removeMa2Sma(contentid);
		mediaService.removeResDictRef(contentid);
		mediaService.removeCha(contentid);
	}

	public void removeSeqMedia(String contentid) {
		mediaService.removeSma(contentid);
		mediaService.removeMa2Sma(contentid);
		mediaService.removeResDictRef(contentid);
		mediaService.removeCha(contentid);
	}

	/** 计算分享地址的功能 */
	public static final String preAddr = "http://www.wotingfm.com:908/CM/mweb";// 分享地址前缀

	public static final String getShareUrl_JM(String preUrl, String contentId) {// 的到节目的分享地址
		return preUrl + "/jm/" + contentId + "/content.html";
	}

	public static final String getShareUrl_ZJ(String preUrl, String contentId) {// 的到专辑的分享地址
		return preUrl + "/zj/" + contentId + "/content.html";
	}

	public static final String getShareUrl_DT(String preUrl, String contentId) {// 的到电台的分享地址
		return preUrl + "/dt/" + contentId + "/content.html";
	}
}