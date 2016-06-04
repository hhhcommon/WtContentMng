package com.woting.content.manage.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.spiritdata.framework.util.StringUtils;
import com.woting.cm.core.channel.model.ChannelAsset;
import com.woting.cm.core.media.model.MediaAsset;
import com.woting.cm.core.media.model.SeqMediaAsset;
import com.woting.content.common.util.RequestUtils;
import com.woting.content.manage.service.ContentService;

@Controller
public class ContentController {
	@Resource
	private ContentService contentService;
	
	/**
	 * 查询主播id下的单体列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/getHostContents.do")
	@ResponseBody
	public Map<String, Object> getListContents(HttpServletRequest request){
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
		Map<String, Object> c = contentService.getContents(userid, mediatype);
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
	
	/**
	 * 新增单体和编辑单体
	 * @param request
	 * @param myfiles
	 * @return
	 */
	@RequestMapping(value = "/content/addMediaInfo.do")
	@ResponseBody
	public Map<String, Object> addMediaContent(HttpServletRequest request){
		System.out.println("上传文件");
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
		map = contentService.addMediaInfo(userid, username, contentname, contentimg, contenturl, contentkeywords, contentdescn, seqid, seqname);

		return map;
	}
	
	/**
	 * 新增专辑
	 * @param request
	 * @param myfiles
	 * @return
	 */
	@RequestMapping(value = "/content/addSeqMediaInfo.do")
	@ResponseBody
	public Map<String, Object> addSequContent(HttpServletRequest request){
		System.out.println("上传文件");
		Map<String, Object> map = new HashMap<String,Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId")+"";
		String username = m.get("UserName")+"";
		if(userid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无用户信息");
			return map;
		}
		String contentname = m.get("ContentName")+"";
		if(contentname.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无节目名称");
			return map;
		}
		String contentimg = m.get("ContentImg")+"";
		contentimg = contentimg.replace("D:\\workIDE\\work\\WtContentMng\\WebContent\\uploadFiles\\tempuplf\\", "../uploadFiles/tempuplf/");
		String contentdesc = m.get("ContentDesc")+"";
		String did = m.get("ContentCatalogsId")+"";
		List<Map<String, Object>> maList = new ArrayList<Map<String,Object>>();;
		if(!m.containsKey("AddMediaInfo"))maList=null;
		else maList = (List<Map<String, Object>>) m.get("AddMediaInfo");
		map = contentService.addSequInfo(userid, username, contentname, contentimg, did, contentdesc, maList);
		return map;
	}
	
	@RequestMapping(value= "/content/updateMediaInfo.do")
	@ResponseBody
	public Map<String, Object> updateMedia(HttpServletRequest request){
		MediaAsset ma = new MediaAsset();
		Map<String, Object> map = new HashMap<String,Object>();
		SeqMediaAsset sma = new SeqMediaAsset();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		System.out.println(m);
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
		map = contentService.updateMediaInfo(ma,sma);
		return map;
	}
	
	@RequestMapping(value= "/content/updateSeqInfo.do")
	@ResponseBody
	public Map<String, Object> updateSeq(HttpServletRequest request){
		MediaAsset ma = new MediaAsset();
		SeqMediaAsset sma = new SeqMediaAsset();
		Map<String, Object> map = new HashMap<String,Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		System.out.println(m);
		String maid = m.get("ContentId")+"";
		ma.setId(maid);
		String maname = m.get("ContentName")+"";
		if(!maname.toLowerCase().equals("null")) ma.setMaTitle(maname);
		String maimg = m.get("ContentImg")+"";
		if(!maimg.toLowerCase().equals("null")) ma.setMaImg(maimg);
		String madesc = m.get("ContentDesc")+"";
		if(!madesc.toLowerCase().equals("null")) ma.setDescn(madesc);
//		String subjectwords = m.get("SubjectWords")+"";
//		if(subjectwords.toLowerCase().equals("null")) mauri=null;
//		String keywords = m.get("KeyWords")+"";
//		if(keywords.toLowerCase().equals("null")) mauri=null;
		map = contentService.updateSeqInfo(sma);
		return map;
	}
	
	@RequestMapping(value = "/content/updateSeqMediaStatus.do")
	@ResponseBody
	public Map<String, Object> updateStatus(HttpServletRequest request){
		Map<String, Object> map = new HashMap<String,Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId")+"";
		if(userid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无用户信息");
			return map;
		}
		String smaid = m.get("ContentId")+"";
		if(smaid.toLowerCase().equals("null")){
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
		String smaname = m.get("ContentName")+"";
		String smaimg = m.get("ContentImg")+"";
		String desc = m.get("ContentDesc")+"";
		map = contentService.modifySeqStatus(userid, smaid, smaname, chid, desc, smaimg);
		return map;
	}
	
	@RequestMapping(value = "/content/removeMediaInfo.do")
	@ResponseBody
	public Map<String, Object> removeMediaInfo(HttpServletRequest request){
		Map<String, Object> map = new HashMap<String,Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId")+"";
		if(userid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无用户信息");
			return map;
		}
		String contentid = m.get("ContentId")+"";
		if(contentid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无单体信息");
			return map;
		}
		contentService.removeMediaAsset(contentid);
		map.put("ReturnType", "1001");
		map.put("Message", "单体删除成功");
		return map;
	}
	
	@RequestMapping(value = "/content/removeSeqMedia.do")
	@ResponseBody
	public Map<String, Object> removeSeqInfo(HttpServletRequest request){
		Map<String, Object> map = new HashMap<String,Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId")+"";
		if(userid.toLowerCase().equals("null")){
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
		contentService.removeSeqMedia(contentid);
		map.put("ReturnType", "1001");
		map.put("Message", "专辑删除成功");
		return map;
	}
	
}
