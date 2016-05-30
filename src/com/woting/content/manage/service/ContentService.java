package com.woting.content.manage.service;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.spiritdata.framework.util.DateUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.WtContentMngConstants;
import com.woting.cm.core.channel.model.Channel;
import com.woting.cm.core.channel.model.ChannelAsset;
import com.woting.cm.core.dict.model.DictDetail;
import com.woting.cm.core.media.model.MaSource;
import com.woting.cm.core.media.model.MediaAsset;
import com.woting.cm.core.media.model.SeqMediaAsset;
import com.woting.cm.core.media.service.MediaService;
import com.woting.content.publish.utils.CacheUtils;

@Service
public class ContentService {
	@Resource
	private MediaService mediaService;

	private String filepath = WtContentMngConstants.ROOT_PATH + "media/";

	public ContentService() {
		filepath = filepath + DateUtils.convert2DateStr(new Date(System.currentTimeMillis())) + "/";
	}

	/**
	 * 上传单体节目
	 * @param upfiles
	 * @param uploadmap
	 * @return
	 */
	public Map<String, Object> addMediaInfo(MultipartFile[] upfiles, Map<String, Object> uploadmap) {
		Map<String, Object> map = new HashMap<String, Object>();
		String[] filepaths = new String[2];
		String filename = "";
		String imgname = "";
		// 把文件存入服务器里
		for (MultipartFile upfile : upfiles) {
			try {
				if (!upfile.isEmpty()) {
					if (upfile.getContentType().contains("audio")) { // 上传的音频资源
						filename = upfile.getOriginalFilename();
						filepaths[1] = (filepath + filename).trim();
						File file = CacheUtils.createFile(filepaths[1]);
						upfile.transferTo(file);
					}
					if (upfile.getContentType().contains("image")) { // 上传的图片资源
						imgname = upfile.getOriginalFilename();
						filepaths[0] = (filepath + imgname).trim();
						File file = CacheUtils.createFile(filepaths[0]);
						upfile.transferTo(file);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String maid = SequenceUUID.getPureUUID();
		String sequid = uploadmap.get("ContentSequId") + "";
		String sequtitle = uploadmap.get("ContentSequTitle") + "";
		Timestamp ctime = new Timestamp(System.currentTimeMillis()); // 节目创建时间

		MediaAsset ma = new MediaAsset();
		ma.setId(maid);
		ma.setMaTitle(uploadmap.get("ContentName") + "");
		ma.setMaImg(StringUtils.isNullOrEmptyOrSpace(filepaths[0]) ? "默认图片" : filepaths[0]);
		ma.setMaURL(filepaths[1]);
		ma.setKeyWords("上传文件测试用待删除");
		ma.setMaPubType(3);
		ma.setMaPubId(uploadmap.get("UserId") + "");
		ma.setDescn(StringUtils.isNullOrEmptyOrSpace(uploadmap.get("ContentDesc") + "") ? "这家伙真懒，什么都没留下"
				: (uploadmap.get("ContentDesc") + ""));
		ma.setPubCount(0);
		ma.setCTime(ctime);

		// 保存单体资源
		mediaService.saveMa(ma);
		System.out.println(sequid);
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
		maSource.setMaSrcId(uploadmap.get("UserId") + "");
		maSource.setMaSource(uploadmap.get("UserName") + "");
		maSource.setSmType(1);
		maSource.setPlayURI(filepaths[1]);
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
	 * 创建专辑
	 * @param upfile
	 * @param m
	 * @return
	 */
	public Map<String, Object> addSequInfo(MultipartFile upfile, Map<String, Object> m) {
		Map<String, Object> map = new HashMap<String, Object>();
		String imgpath = "";
		try {
			if (!upfile.isEmpty()) {
				if (upfile.getContentType().contains("image")) { // 上传的图片资源
					String imgname = upfile.getOriginalFilename();
					imgpath = (filepath + imgname).trim();
					File file = CacheUtils.createFile(imgpath);
					upfile.transferTo(file);
				}
			}
		} catch (Exception e) {
		}

		String smaid = SequenceUUID.getPureUUID();
		SeqMediaAsset sma = new SeqMediaAsset();
		sma.setId(smaid);
		String smatitle = m.get("ContentName") + "";
		if (StringUtils.isNullOrEmptyOrSpace(smatitle) || smatitle.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无专辑名称");
			return map;
		}
		sma.setSmaTitle(smatitle);
		sma.setSmaImg(StringUtils.isNullOrEmptyOrSpace(imgpath) ? "默认图片" : imgpath);
		sma.setDescn(StringUtils.isNullOrEmptyOrSpace(m.get("ContentDesc") + "") ? "这家伙真懒，什么都没留下"
				: (m.get("ContentDesc") + ""));
		Map<String, Object> addmediamap = (Map<String, Object>) m.get("AddMediaInfo");
		if (addmediamap != null) {
			List<Map<String, Object>> list = (List<Map<String, Object>>) addmediamap.get("List");
			for (Map<String, Object> m2 : list) {
				MediaAsset ma = new MediaAsset();
				ma.setId(m2.get("ContentId") + "");
				ma.setMaTitle(m2.get("ContentName") + "");
				mediaService.bindMa2Sma(ma, sma);
			}
		}
		sma.setCTime(new Timestamp(System.currentTimeMillis()));
		sma.setSmaPubType(3);
		sma.setSmaPubId(m.get("UserId") + "");
		sma.setSmaPublisher(m.get("UserName") + "");
		DictDetail detail = new DictDetail();
		detail.setId("zho");
		detail.setNodeName("中文");
		sma.setLang(detail);
		if (addmediamap != null)
			sma.setSmaAllCount(addmediamap.size());
		else
			sma.setSmaAllCount(1);
		sma.setPubCount(0);
		mediaService.saveSma(sma);

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
		} else {
			map.put("ReturnType", "1011");
			map.put("Message", "处理专辑失败");
			return map;
		}
		return map;
	}

	/**
	 * 修改专辑，单体的发布状态
	 * @param userid
	 * @param list
	 * @return
	 */
	public Map<String, Object> modifyStatus(String userid, List<Map<String, Object>> list) {
		Map<String, Object> map = new HashMap<String,Object>();
		for (Map<String, Object> m : list) {
			String mediatype = m.get("MediaType") + "";
			switch (mediatype) {
			case "MediaType":
				break;
			case "wt_SeqMediaAsset":
				map = modifySeqStatus(userid, m);
				break;
			default:
				break;
			}
		}
		return map;
	}

	private Map<String, Object> modifySeqStatus(String userid, Map<String, Object> m) {
		Map<String, Object> map = new HashMap<String, Object>();
		String caid = m.get("ChannelAssetId")+"";
		if(StringUtils.isNullOrEmptyOrSpace(caid)||caid.toLowerCase().equals("null")) {
			caid = SequenceUUID.getPureUUID();
		}
		String contentid = m.get("ContentId") + "";
		String contenttitle = m.get("ContentTitle") + "";
		String contentimg = m.get("ContentImg") + "";
		String channelid = m.get("ChannelId") + "";
		String flowflag = m.get("ContentFlowFlag") + "";
		if (StringUtils.isNullOrEmptyOrSpace(contentid) || contentid.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无专辑id");
			return map;
		}
		if (StringUtils.isNullOrEmptyOrSpace(flowflag) || flowflag.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无专辑修改状态");
			return map;
		}
		if (flowflag.equals("1")) {
			SeqMediaAsset sma = mediaService.getSmaInfoById(contentid);
			ChannelAsset cha = new ChannelAsset();
			cha.setId(caid);
			Channel ch = mediaService.getChInfoById(channelid);
			cha.setCh(ch);
			cha.setPubObj(sma);
			cha.setPublisherId(userid);
			if (!StringUtils.isNullOrEmptyOrSpace(contenttitle) && !contenttitle.toLowerCase().equals("null"))
				cha.setPubName(contenttitle);
			if (!StringUtils.isNullOrEmptyOrSpace(contentimg) && !contentimg.toLowerCase().equals("null"))
				cha.setPubImg(contentimg);
			cha.setPubObj(sma);
			cha.setCheckerId("1");
			cha.setFlowFlag(1);
			cha.setSort(0);
			cha.setCheckRuleIds("0");
			cha.setCTime(new Timestamp(System.currentTimeMillis()));
			cha.setIsValidate(1);
			cha.setInRuleIds("elt");
			cha.setCheckRuleIds("elt");
			mediaService.saveCHA(cha);
			if(mediaService.getCHAInfoById(caid)!=null){
				map.put("ReturnType", "1001");
				map.put("Message", "专辑处于待审核状态");
				return map;
			} else {
				map.put("ReturnType", "1011");
				map.put("Message", "专辑提交审核失败");
				return map;
			}
		}else {
			if(flowflag.equals("4")) {
				SeqMediaAsset sma = mediaService.getSmaInfoById(contentid);
				ChannelAsset cha = mediaService.getCHAInfoById(caid);
				cha.setFlowFlag(4);
				mediaService.updateCHA(cha);
				if(mediaService.getCHAInfoById(caid).getFlowFlag()==4){
					map.put("ReturnType", "1001");
					map.put("Message", "专辑处于撤回状态");
					return map;
				}else{
					map.put("ReturnType", "1011");
					map.put("Message", "专辑撤回失败");
					return map;
				}
			}
		}
		return map;
	}
	
	private Map<String, Object> modifyMaStatus(String userid, Map<String, Object> m){
		Map<String, Object> map = new HashMap<String, Object>();
		String caid = m.get("ChannelAssetId")+"";
		if(StringUtils.isNullOrEmptyOrSpace(caid)||caid.toLowerCase().equals("null")) {
			caid = SequenceUUID.getPureUUID();
		}
		String contentid = m.get("ContentId") + "";
		String contenttitle = m.get("ContentTitle") + "";
		String contentimg = m.get("ContentImg") + "";
		String channelid = m.get("ChannelId") + "";
		String flowflag = m.get("ContentFlowFlag") + "";
		if (StringUtils.isNullOrEmptyOrSpace(contentid) || contentid.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无节目id");
			return map;
		}
		if (StringUtils.isNullOrEmptyOrSpace(flowflag) || flowflag.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无节目修改状态");
			return map;
		}
		if(flowflag.equals("1")) {
			MediaAsset ma = mediaService.getMaInfoById(contentid);
			ChannelAsset cha = new ChannelAsset();
			cha.setId(caid);
			Channel ch = mediaService.getChInfoById(channelid);
			cha.setCh(ch);
			cha.setPubObj(ma);
			cha.setPublisherId(userid);
			if (!StringUtils.isNullOrEmptyOrSpace(contenttitle) && !contenttitle.toLowerCase().equals("null"))
				cha.setPubName(contenttitle);
			if (!StringUtils.isNullOrEmptyOrSpace(contentimg) && !contentimg.toLowerCase().equals("null"))
				cha.setPubImg(contentimg);
			cha.setPubObj(ma);
			cha.setCheckerId("1");
			cha.setFlowFlag(1);
			cha.setSort(0);
			cha.setCheckRuleIds("0");
			cha.setCTime(new Timestamp(System.currentTimeMillis()));
			cha.setIsValidate(1);
			cha.setInRuleIds("elt");
			cha.setCheckRuleIds("elt");
			mediaService.saveCHA(cha);
			if(mediaService.getCHAInfoById(caid)!=null){
				map.put("ReturnType", "1001");
				map.put("Message", "节目处于待审核状态");
				return map;
			} else {
				map.put("ReturnType", "1011");
				map.put("Message", "节目提交审核失败");
				return map;
			}
		}else {
			if(flowflag.equals("4")) {
				MediaAsset ma = mediaService.getMaInfoById(contentid);
				ChannelAsset cha = mediaService.getCHAInfoById(caid);
				cha.setFlowFlag(4);
				mediaService.updateCHA(cha);
				if(mediaService.getCHAInfoById(caid).getFlowFlag()==4){
					map.put("ReturnType", "1001");
					map.put("Message", "节目处于撤回状态");
					return map;
				}else{
					map.put("ReturnType", "1011");
					map.put("Message", "节目撤回失败");
					return map;
				}
			}
		}
		return m;
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