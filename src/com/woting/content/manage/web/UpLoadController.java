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
import com.woting.content.manage.service.UpLoadService;

@Controller
public class UpLoadController {
	@Resource
	private UpLoadService uploadService;
	
	@RequestMapping(value = "/content/uploadMediaContents.do")
	@ResponseBody
	public Map<String, Object> uploadMediaContent(HttpServletRequest request,@RequestParam(value = "thefile", required = false) MultipartFile[] myfiles){
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
		String sequid = m.get("ContentSequId")+"";
		
		System.out.println(userid+"#"+contentname+"#"+CatalogsId);
	    uploadService.saveFileInfo(myfiles,m);
		return map;
	}
	
}
