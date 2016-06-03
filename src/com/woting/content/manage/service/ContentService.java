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
import com.woting.cm.core.dict.mem._CacheDictionary;
import com.woting.cm.core.dict.model.DictDetail;
import com.woting.cm.core.dict.model.DictModel;
import com.woting.cm.core.dict.model.DictRefRes;
import com.woting.cm.core.dict.persis.po.DictRefResPo;
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
	 * @param upfiles
	 * @param uploadmap
	 * @return
	 */
	public Map<String, Object> addMediaInfo(String userid, String username, String maname, String maimg, String maurl, String keywords, String madesc, String seqid, String seqname) {
		Map<String, Object> map = new HashMap<String, Object>();
		String maid = SequenceUUID.getPureUUID();
		String sequid = seqid+"";
		String sequtitle = seqname+ "KeyWords";
		Timestamp ctime = new Timestamp(System.currentTimeMillis()); // 节目创建时间

		MediaAsset ma = new MediaAsset();
		ma.setId(maid);
		ma.setMaTitle(maname);
		ma.setMaImg(maimg.toLowerCase().equals("null")?"www.wotingfm.com:908/CM/mweb/templet/zj_templet/imgs/default.png":maimg.replace("D:\\workIDE\\work\\WtContentMng\\WebContent\\", "localhost:908/CM/"));
		ma.setMaURL(maurl);
		ma.setKeyWords("上传文件测试用待删除");
		ma.setMaPubType(3);
		ma.setMaPubId(userid);
		ma.setMaPublisher(username);
		ma.setDescn(madesc.toLowerCase().equals("null")?"这家伙真懒，什么都没留下":madesc);
		ma.setPubCount(0);
		ma.setCTime(ctime);

		// 保存单体资源
		mediaService.saveMa(ma);
		
		// 保存专辑与单体媒体对应表
		if (!StringUtils.isNullOrEmptyOrSpace(sequid) || !sequid.toLowerCase().equals("null")) {
			SeqMediaAsset sma = new SeqMediaAsset();
			sma.setId(sequid);
			sma.setSmaTitle(sequtitle);
			mediaService.bindMa2Sma(ma, sma);
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
	 * @param ma
	 * @param sma
	 * @return
	 */
	public Map<String, Object> updateMediaInfo(MediaAsset ma, SeqMediaAsset sma){
		mediaService.updateMa(ma);
		if(sma!=null){
			SeqMaRefPo seqmapo = new SeqMaRefPo();
		    seqmapo.setMId(mediaService.getSeqMaRefByMId(ma.getId()).getMId());
		    seqmapo.setSId(sma.getId());
		    seqmapo.setCTime(new Timestamp(System.currentTimeMillis()));
		    mediaService.updateSeqMaRef(seqmapo);
		}
		return null;
	}
	
	/**
	 * 修改专辑信息
	 * @param ma
	 * @param sma
	 * @return
	 */
	public Map<String, Object> updateSeqInfo(SeqMediaAsset sma){
		mediaService.updateSma(sma);
		return null;
	}

	/**
	 * 创建专辑
	 * @param upfile
	 * @param m
	 * @return
	 */
	public Map<String, Object> addSequInfo(String userid,String username, String smaname, String smaimg, String catalogsid, String smadesc, List<Map<String, Object>> malist) {
		Map<String, Object> map = new HashMap<String, Object>();

		//保存专辑信息到资源库
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
		if(smaimg.toLowerCase().equals("null"))
			smaimg = "www.wotingfm.com:908/CM/mweb/templet/zj_templet/imgs/default.png";
		sma.setSmaImg(smaimg);
		sma.setDescn(smadesc.toLowerCase().equals("null")?"这家伙真懒，什么都没留下":smadesc);
		
		//保存专辑与单体媒体对应关系
		if(malist!=null&&malist.size()>0){
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
		
		//保存专辑分类信息到wt_ResDict_Ref
		try {
			_CacheDictionary _cd = ((CacheEle<_CacheDictionary>)SystemCache.getCache(WtContentMngConstants.CACHE_DICT)).getContent();
		    DictModel dm=_cd.getDictModelById("3");
			EasyUiTree<DictDetail> eu1 = new EasyUiTree<DictDetail>(dm.dictTree);
			Map<String, Object> m = eu1.toTreeMap();
			List<Map<String, Object>> chillist = (List<Map<String, Object>>) m.get("children");
			for (Map<String, Object> map2 : chillist) {
				if(map2.get("id").equals(catalogsid)){
					DictRefRes dicres = new DictRefRes();
					dicres.setId(SequenceUUID.getPureUUID());
					dicres.setRefName("专辑-内容分类");
					dicres.setResTableName("wt_SeqMediaAsset");
					dicres.setResId(smaid);
					dicres.setDm(dm);
					DictDetail dicd = new DictDetail();
					dicd.setId(map2.get("id")+"");
					dicd.setMId("3");
					Map<String, Object> l = (Map<String, Object>) map2.get("attributes");
					dicd.setBCode(l.get("bCode")+"");
					dicd.setDdName(l.get("nodeName")+"");
					dicres.setDd(dicd);
					dicres.setCTime(new Timestamp(System.currentTimeMillis()));
					mediaService.saveDictRef(dicres);
				}
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
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
	 * 查询主播的资源列表
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

//	/**
//	 * 修改专辑，单体的发布状态
//	 * @param userid
//	 * @param list
//	 * @return
//	 */
//	public Map<String, Object> modifyStatus(String userid, List<Map<String, Object>> list) {
//		Map<String, Object> map = new HashMap<String,Object>();
//		for (Map<String, Object> m : list) {
//			String mediatype = m.get("MediaType") + "";
//			switch (mediatype) {
//			case "MediaType":
//				break;
//			case "wt_SeqMediaAsset":
//				map = modifySeqStatus(userid, m);
//				break;
//			default:
//				break;
//			}
//		}
//		return map;
//	}
//
//	private Map<String, Object> modifySeqStatus(String userid, Map<String, Object> m) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		String caid = m.get("ChannelAssetId")+"";
//		if(StringUtils.isNullOrEmptyOrSpace(caid)||caid.toLowerCase().equals("null")) {
//			caid = SequenceUUID.getPureUUID();
//		}
//		String contentid = m.get("ContentId") + "";
//		String contenttitle = m.get("ContentTitle") + "";
//		String contentimg = m.get("ContentImg") + "";
//		String channelid = m.get("ChannelId") + "";
//		String flowflag = m.get("ContentFlowFlag") + "";
//		if (StringUtils.isNullOrEmptyOrSpace(contentid) || contentid.toLowerCase().equals("null")) {
//			map.put("ReturnType", "1011");
//			map.put("Message", "无专辑id");
//			return map;
//		}
//		if (StringUtils.isNullOrEmptyOrSpace(flowflag) || flowflag.toLowerCase().equals("null")) {
//			map.put("ReturnType", "1011");
//			map.put("Message", "无专辑修改状态");
//			return map;
//		}
//		
//		SeqMediaAsset sma = mediaService.getSmaInfoById(contentid);
//		ChannelAsset cha = new ChannelAsset();
//		cha.setId(caid);
//		Channel ch = mediaService.getChInfoById(channelid);
//		cha.setCh(ch);
//		cha.setPubObj(sma);
//		cha.setPublisherId(userid);
//		if (!StringUtils.isNullOrEmptyOrSpace(contenttitle) && !contenttitle.toLowerCase().equals("null"))
//			cha.setPubName(contenttitle);
//		if (!StringUtils.isNullOrEmptyOrSpace(contentimg) && !contentimg.toLowerCase().equals("null"))
//			cha.setPubImg(contentimg);
//		cha.setPubObj(sma);
//		cha.setCheckerId("1");
//		cha.setFlowFlag(1);
//		cha.setSort(0);
//		cha.setCheckRuleIds("0");
//		cha.setCTime(new Timestamp(System.currentTimeMillis()));
//		cha.setIsValidate(1);
//		cha.setInRuleIds("elt");
//		cha.setCheckRuleIds("elt");
//		mediaService.saveCHA(cha);
//		if(mediaService.getCHAInfoById(caid)!=null){
//			map.put("ReturnType", "1001");
//			map.put("Message", "专辑处于待审核状态");
//			return map;
//		} else {
//			map.put("ReturnType", "1011");
//			map.put("Message", "专辑提交审核失败");
//			return map;
//		}
//	}
//	
//	private Map<String, Object> modifyMaStatus(String userid, Map<String, Object> m){
//		Map<String, Object> map = new HashMap<String, Object>();
//		String caid = m.get("ChannelAssetId")+"";
//		if(StringUtils.isNullOrEmptyOrSpace(caid)||caid.toLowerCase().equals("null")) {
//			caid = SequenceUUID.getPureUUID();
//		}
//		String contentid = m.get("ContentId") + "";
//		String contenttitle = m.get("ContentTitle") + "";
//		String contentimg = m.get("ContentImg") + "";
//		String channelid = m.get("ChannelId") + "";
//		String flowflag = m.get("ContentFlowFlag") + "";
//		if (StringUtils.isNullOrEmptyOrSpace(contentid) || contentid.toLowerCase().equals("null")) {
//			map.put("ReturnType", "1011");
//			map.put("Message", "无节目id");
//			return map;
//		}
//		if (StringUtils.isNullOrEmptyOrSpace(flowflag) || flowflag.toLowerCase().equals("null")) {
//			map.put("ReturnType", "1011");
//			map.put("Message", "无节目修改状态");
//			return map;
//		}
//		if(flowflag.equals("1")) {
//			MediaAsset ma = mediaService.getMaInfoById(contentid);
//			ChannelAsset cha = new ChannelAsset();
//			cha.setId(caid);
//			Channel ch = mediaService.getChInfoById(channelid);
//			cha.setCh(ch);
//			cha.setPubObj(ma);
//			cha.setPublisherId(userid);
//			if (!StringUtils.isNullOrEmptyOrSpace(contenttitle) && !contenttitle.toLowerCase().equals("null"))
//				cha.setPubName(contenttitle);
//			if (!StringUtils.isNullOrEmptyOrSpace(contentimg) && !contentimg.toLowerCase().equals("null"))
//				cha.setPubImg(contentimg);
//			cha.setPubObj(ma);
//			cha.setCheckerId("1");
//			cha.setFlowFlag(1);
//			cha.setSort(0);
//			cha.setCheckRuleIds("0");
//			cha.setCTime(new Timestamp(System.currentTimeMillis()));
//			cha.setIsValidate(1);
//			cha.setInRuleIds("elt");
//			cha.setCheckRuleIds("elt");
//			mediaService.saveCHA(cha);
//			if(mediaService.getCHAInfoById(caid)!=null){
//				map.put("ReturnType", "1001");
//				map.put("Message", "节目处于待审核状态");
//				return map;
//			} else {
//				map.put("ReturnType", "1011");
//				map.put("Message", "节目提交审核失败");
//				return map;
//			}
//		}else {
//			if(flowflag.equals("4")) {
//				MediaAsset ma = mediaService.getMaInfoById(contentid);
//				ChannelAsset cha = mediaService.getCHAInfoById(caid);
//				cha.setFlowFlag(4);
//				mediaService.updateCHA(cha);
//				if(mediaService.getCHAInfoById(caid).getFlowFlag()==4){
//					map.put("ReturnType", "1001");
//					map.put("Message", "节目处于撤回状态");
//					return map;
//				}else{
//					map.put("ReturnType", "1011");
//					map.put("Message", "节目撤回失败");
//					return map;
//				}
//			}
//		}
//		return m;
//	}

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