package com.woting.content.manage.web;

import java.util.HashMap;
import java.util.List;
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
	
	/**
	 * 查询主播id下的单体列表
	 * @param request
	 * @return
	 */
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
	@RequestMapping(value = "/content/addMediaContents.do")
	@ResponseBody
	public Map<String, Object> addMediaContent(HttpServletRequest request,@RequestParam(value = "thefile", required = true) MultipartFile[] myfiles){
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
		if(myfiles==null||myfiles.length==0){
			map.put("ReturnType", "1011");
			map.put("Message", "无上传文件");
			return map;
		}
		map = contentService.addMediaInfo(myfiles,m);
		return map;
	}
	
	@RequestMapping(value = "/content/addSequContents.do")
	@ResponseBody
	public Map<String, Object> addSequContent(HttpServletRequest request,@RequestParam(value = "thefile", required = false) MultipartFile myfiles){
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
		if(myfiles==null){
			map.put("ReturnType", "1011");
			map.put("Message", "无上传文件");
			return map;
		}
		System.out.println(userid+"#"+contentname);
		map = contentService.addSequInfo(myfiles,m);
		return map;
	}
	
	@RequestMapping(value = "/content/updateZBContentStatus.do")
	@ResponseBody
	public Map<String, Object> updateStatus(HttpServletRequest request){
		Map<String, Object> map = new HashMap<String,Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId")+"";
		if(StringUtils.isNullOrEmptyOrSpace(userid)||userid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无用户信息");
			return map;
		}
		List<Map<String, Object>> list = (List<Map<String, Object>>) m.get("List");
		if (list==null||!(list.size()>0)) {
			map.put("ReturnType", "1011");
			map.put("Message", "数据参数不全");
			return map;
		}
		contentService.modifyStatus(userid, list);
		return null;
	}
	
}
