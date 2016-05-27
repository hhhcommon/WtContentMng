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
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.WtContentMngConstants;
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
	
	private String filepath =WtContentMngConstants.ROOT_PATH+"media/";

	public ContentService() {
		filepath =filepath+DateUtils.convert2DateStr(new Date(System.currentTimeMillis()))+"/";
	}

	public Map<String, Object> addMediaInfo(MultipartFile[] upfiles, Map<String, Object> uploadmap) {
		Map<String, Object> map = new HashMap<String,Object>();
		String[] filepaths = new String[2];
		String filename = "";
		String imgname = "";
		//把文件存入服务器里
		for (MultipartFile upfile : upfiles) {
			try {
				if (!upfile.isEmpty()) {
					if (upfile.getContentType().contains("audio")) { // 上传的音频资源
						filename = upfile.getOriginalFilename();
						filepaths[1] = (filepath+filename).trim();
						File file = CacheUtils.createFile(filepaths[1]);
						upfile.transferTo(file);
					}
					if (upfile.getContentType().contains("image")) { // 上传的图片资源
						imgname = upfile.getOriginalFilename();
						filepaths[0] = (filepath+imgname).trim();
						File file = CacheUtils.createFile(filepaths[0]);
						upfile.transferTo(file);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		String maid = SequenceUUID.getPureUUID();
		String sequid = uploadmap.get("ContentSequId")+"";
		String sequtitle = uploadmap.get("ContentSequTitle")+"";
		Timestamp ctime = new Timestamp(System.currentTimeMillis()); // 节目创建时间
		
		MediaAsset ma = new MediaAsset();
		ma.setId(maid);
		ma.setMaTitle(uploadmap.get("ContentName")+"");
		ma.setMaImg(StringUtils.isNullOrEmptyOrSpace(filepaths[0])?"默认图片":filepaths[0]);
		ma.setMaURL(filepaths[1]);
		ma.setKeyWords("上传文件测试用待删除");
		ma.setMaPubType(3);
		ma.setMaPubId(uploadmap.get("UserId")+"");
		ma.setDescn(StringUtils.isNullOrEmptyOrSpace(uploadmap.get("ContentDesc")+"")?"这家伙真懒，什么都没留下":(uploadmap.get("ContentDesc")+""));
		ma.setPubCount(0);
		ma.setCTime(ctime);
		
		//保存单体资源
		mediaService.saveMa(ma);
		
		//保存专辑与单体媒体对应表
		if (!StringUtils.isNullOrEmptyOrSpace(sequid)||!sequid.equals("null")) {
			SeqMediaAsset sma = new SeqMediaAsset();
			sma.setId(sequid);
			sma.setSmaTitle(sequtitle);
			mediaService.bindMa2Sma(ma, sma);
		}
		
		//保存资源来源表里
		MaSource maSource = new MaSource();
		maSource.setMa(ma);
		maSource.setId(SequenceUUID.getPureUUID());
		maSource.setMaSrcType(3);
		maSource.setMaSrcId(uploadmap.get("UserId")+"");
		maSource.setMaSource(uploadmap.get("UserName")+"");
		maSource.setSmType(1);
		maSource.setPlayURI(filepaths[1]);
		maSource.setIsMain(1);
		maSource.setDescn("上传文件测试用待删除");
		maSource.setCTime(ctime);
		mediaService.saveMas(maSource);
		
		if(mediaService.getMaInfoById(maid) != null) {
			map.put("ResultType", "1001");
			map.put("Message", "上传文件成功");
		}
		else {
			map.put("ResultType", "1011");
			map.put("Message", "上传失败");
		}
		return map;
	}
	

	public Map<String, Object> addSequInfo(MultipartFile upfile,Map<String, Object> m){
		Map<String, Object> map = new HashMap<String,Object>();
		String imgpath = "";
		try {
			if(!upfile.isEmpty()) {
			    if (upfile.getContentType().contains("image")) { // 上传的图片资源
			    	String imgname = upfile.getOriginalFilename();
				    imgpath = (filepath+imgname).trim();
				    File file = CacheUtils.createFile(imgpath);
				    upfile.transferTo(file);
			    }
		    }
		} catch (Exception e) {
		}
		
		String smaid = SequenceUUID.getPureUUID();
		SeqMediaAsset sma = new SeqMediaAsset();
		sma.setId(smaid);
		String smatitle = m.get("ContentName")+"";
		if(StringUtils.isNullOrEmptyOrSpace(smatitle)||smatitle.toLowerCase().equals("null")) {
			map.put("ResultType", "1011");
			map.put("Message", "无专辑名称");
			return map;
		}
		sma.setSmaTitle(smatitle);
		sma.setSmaImg(StringUtils.isNullOrEmptyOrSpace(imgpath)?"默认图片":imgpath);
		sma.setDescn(StringUtils.isNullOrEmptyOrSpace(m.get("ContentDesc")+"")?"这家伙真懒，什么都没留下":(m.get("ContentDesc")+""));
		Map<String, Object> addmediamap = (Map<String, Object>) m.get("AddMediaInfo");
		if (addmediamap!=null) {
			List<Map<String, Object>> list = (List<Map<String, Object>>) addmediamap.get("List");
			for (Map<String, Object> m2 : list) {
				MediaAsset ma = new MediaAsset();
				ma.setId(m2.get("ContentId")+"");
				ma.setMaTitle(m2.get("ContentName")+"");
				mediaService.bindMa2Sma(ma, sma);
			}
		}
		sma.setCTime(new Timestamp(System.currentTimeMillis()));
		sma.setSmaPubType(3);
		sma.setSmaPubId(m.get("UserId")+"");
		sma.setSmaPublisher(m.get("UserName")+"");
		DictDetail detail = new DictDetail();
		detail.setId("zho");
		detail.setNodeName("中文");
		sma.setLang(detail);
		if (addmediamap!=null) sma.setSmaAllCount(addmediamap.size());
		else sma.setSmaAllCount(1);
		sma.setPubCount(0);
		
		System.out.println(JsonUtils.objToJson(sma));
		System.out.println(JsonUtils.objToJson(sma.convert2Po()));
		mediaService.saveSma(sma);
		
		if(mediaService.getSmaInfoById(smaid)) {
			map.put("ResultType", "1001");
			map.put("Message", "添加专辑成功");
		} else {
			map.put("ResultType", "1011");
			map.put("Message", "添加专辑失败");
		}
		return map;
	}
	
	public Map<String, Object> getContents(String userid, String mediatype){
		Map<String, Object> map = new HashMap<String,Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		if(mediatype.equals("wt_MediaAsset")) list = mediaService.getMaInfoByMaPubId(userid);
		if(mediatype.equals("wt_SeqMediaAsset")) list = mediaService.getSmaInfoBySmaPubId(userid);
		System.out.println(list);
		if(list!=null&&list.size()>0) {
			map.put("List", list);
			map.put("AllCount", list.size());
			map.put("ResultType", "1001");
		}else{
			map.put("ResultType", "1011");
			map.put("Message", "处理专辑失败");
			return map;
		}
		return map;
	}
	
	public Map<String, Object> modifyStatus(String userid, String mediatype, String mediaid, String statusnum){
		
		return null;
	}
	
	
	
	/** 计算分享地址的功能 */
    public static final String preAddr="http://www.wotingfm.com:908/CM/mweb";//分享地址前缀
    public static final String getShareUrl_JM(String preUrl, String contentId) {//的到节目的分享地址
        return preUrl+"/jm/"+contentId+"/content.html";
    }
    public static final String getShareUrl_ZJ(String preUrl, String contentId) {//的到专辑的分享地址
        return preUrl+"/zj/"+contentId+"/content.html";
    }
    public static final String getShareUrl_DT(String preUrl, String contentId) {//的到电台的分享地址
        return preUrl+"/dt/"+contentId+"/content.html";
    }
}