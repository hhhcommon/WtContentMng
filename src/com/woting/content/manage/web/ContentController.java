package com.woting.content.manage.web;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.spiritdata.framework.util.StringUtils;
import com.woting.content.common.util.RequestUtils;
import com.woting.content.manage.service.ContentService;

@Controller
public class ContentController {
	@Resource
	private ContentService contentService;
	
	@RequestMapping(value = "/content/getListContents.do")
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
		String flowflag = m.get("ContentFlowFlag")+"";
		if (StringUtils.isNullOrEmptyOrSpace(mediatype)||mediatype.toLowerCase().equals("null")||StringUtils.isNullOrEmptyOrSpace(flowflag)||flowflag.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "查询信息不全");
			return map;
		}
		Map<String, Object> c = contentService.getContents(userid, mediatype, flowflag);
		if(c!=null&&c.size()>0){
			map.put("ResultType", c.get("ResultType"));
			c.remove("ResultType");
			map.put("ResultList", c);
		}else{
			map.put("ReturnType", "1011");
            map.put("Message", "没有查到任何内容");
		}
		return map;
	}
	
	@RequestMapping(value = "/content/addMediaContents.do")
	@ResponseBody
	public Map<String, Object> addMediaContent(HttpServletRequest request,@RequestParam(value = "thefile", required = false) MultipartFile[] myfiles){
		System.out.println("上传文件");
		Map<String, Object> map = new HashMap<String,Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId")+"";
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
		String CatalogsId = m.get("CatalogsId")+"";
		if(StringUtils.isNullOrEmptyOrSpace(CatalogsId)||CatalogsId.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无节目分类信息");
			return map;
		}
		System.out.println(userid+"#"+contentname+"#"+CatalogsId);
		contentService.saveFileInfo(myfiles,m);
		return map;
	}
	
}
