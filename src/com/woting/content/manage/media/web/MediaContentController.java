package com.woting.content.manage.media.web;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.framework.util.StringUtils;
import com.woting.cm.core.media.model.MediaAsset;
import com.woting.cm.core.media.model.SeqMediaAsset;
import com.woting.content.common.util.RequestUtils;
import com.woting.content.manage.media.service.MediaContentService;

public class MediaContentController {
	@Resource
	private MediaContentService mediaContentService;

	@RequestMapping(value = "/content/media/getHostMediaList.do")
	@ResponseBody
	public Map<String, Object> getMediaList(HttpServletRequest request){
		Map<String, Object> map = new HashMap<String,Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId")+"";
		if(StringUtils.isNullOrEmptyOrSpace(userid)||userid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无用户信息");
			return map;
		}
		String mediatype = m.get("MediaType")+"";
		if (StringUtils.isNullOrEmptyOrSpace(mediatype)||mediatype.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "查询信息不全");
			return map;
		}
		Map<String, Object> c = mediaContentService.getHostMediaContents(userid, mediatype);
		if(c!=null&&c.size()>0){
			map.put("ReturnType", c.get("ReturnType"));
			c.remove("ReturnType");
			map.put("ResultList", c);
		}else{
			map.put("ReturnType", "1011");
            map.put("Message", "没有查到任何内容");
		}
		return map;
	}
	
	@RequestMapping(value = "/content/media/addMediaInfo.do")
	@ResponseBody
	public Map<String, Object> addMediaInfo(HttpServletRequest request){
		Map<String, Object> map = new HashMap<String,Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId")+"";
		String username = m.get("UserName")+"";
		if(StringUtils.isNullOrEmptyOrSpace(userid)||userid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无用户信息");
			return map;
		}
		String contentname = m.get("ContentName")+"";
		if(StringUtils.isNullOrEmptyOrSpace(contentname)||contentname.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无节目名称");
			return map;
		}

		String contentimg = m.get("ContentImg")+"";
		contentimg=contentimg.replace("D:\\workIDE\\work\\WtContentMng\\WebContent\\uploadFiles\\tempuplf\\", "../uploadFiles/tempuplf/");
		String contenturl = m.get("ContentURI")+"";
		contenturl=contenturl.replace("D:\\workIDE\\work\\WtContentMng\\WebContent\\uploadFiles\\tempuplf\\", "../uploadFiles/tempuplf/");
		String contentdescn = m.get("ContentDesc")+"";
		String contentkeywords = m.get("KeyWords")+"";
		String seqid = m.get("ContentSequId")+"";
		String seqname = m.get("ContentSequName")+"";
		map = mediaContentService.addMediaInfo(userid, username, contentname, contentimg, contenturl, contentkeywords, contentdescn, seqid, seqname);

		return map;
	}
	
	@RequestMapping(value = "/content/media/updateMediaInfo.do")
	@ResponseBody
	public Map<String, Object> updateMediaInfo(HttpServletRequest request){
		MediaAsset ma = new MediaAsset();
		Map<String, Object> map = new HashMap<String,Object>();
		SeqMediaAsset sma = new SeqMediaAsset();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String maid = m.get("ContentId")+"";
		ma.setId(maid);
		String maname = m.get("ContentName")+"";
		if(!maname.toLowerCase().equals("null")) ma.setMaTitle(maname);
		String maimg = m.get("ContentImg")+"";
		if(!maimg.toLowerCase().equals("null")) ma.setMaImg(maimg);
		String mauri = m.get("ContentURI")+"";
		if(!mauri.toLowerCase().equals("null")) ma.setMaURL(mauri);
		String seqid = m.get("ContentSeqId")+"";
		if(!seqid.toLowerCase().equals("null")) sma.setId(seqid);
		String seqname = m.get("ContentSeqName")+"";
		if(!seqname.toLowerCase().equals("null")) sma.setSmaTitle(seqname);
		String madesc = m.get("ContentDesc")+"";
		if(!madesc.toLowerCase().equals("null")) ma.setDescn(madesc);
//		String subjectwords = m.get("SubjectWords")+"";
//		if(subjectwords.toLowerCase().equals("null")) mauri=null;
//		String keywords = m.get("KeyWords")+"";
//		if(keywords.toLowerCase().equals("null")) mauri=null;
		map = mediaContentService.updateMediaInfo(ma,sma);
		
		return map;
	}
	
	@RequestMapping(value = "/content/media/updateMediaStatus.do")
	@ResponseBody
	public Map<String, Object> updateMediaStatus(HttpServletRequest request){
		Map<String, Object> map = new HashMap<String,Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId")+"";
		if(userid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无用户信息");
			return map;
		}
		String maid = m.get("ContentId")+"";
		if(maid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无专辑id信息");
			return map;
		}
		String chid = m.get("ContentChannelId")+"";
		if(chid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无栏目id信息");
			return map;
		}
		String maname = m.get("ContentName")+"";
		String maimg = m.get("ContentImg")+"";
		String desc = m.get("ContentDesc")+"";
		map = mediaContentService.modifyMediaStatus(userid, maid, maname, chid, desc, maimg);
		return map;
	}
	
	@RequestMapping(value = "/content/media/removeMediaInfo.do")
	@ResponseBody
	public Map<String, Object> removeMediaInfo(HttpServletRequest request){
		Map<String, Object> map = new HashMap<String,Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId")+"";
		if(StringUtils.isNullOrEmptyOrSpace(userid)||userid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无用户信息");
			return map;
		}
		String contentid = m.get("ContentId")+"";
		if(contentid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无专辑信息");
			return map;
		}
		mediaContentService.removeMediaAsset(contentid);
		map.put("ReturnType", "1001");
		map.put("Message", "单体删除成功");
		return map;
	}
}
