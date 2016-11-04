package com.woting.content.manage.filtrate.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.framework.util.RequestUtils;
import com.spiritdata.framework.util.StringUtils;
import com.woting.content.manage.filtrate.service.FiltrateService;

@Controller
public class FiltrateController {
	@Resource
	private FiltrateService filtrateService;
	
	/**
	 * 得到主播管理页面的筛选条件
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/getFiltrates.do")
	@ResponseBody
	public Map<String, Object> getFiltrates(HttpServletRequest request){
		Map<String, Object> map = new HashMap<String,Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId")+"";
		if(StringUtils.isNullOrEmptyOrSpace(userid)||userid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无用户信息");
			return map;
		}
		String mediatype = m.get("MediaType")+"";
		if (mediatype.toLowerCase().equals("null")) {
			map.put("ReturnType", "1012");
			map.put("Message", "无资源类型信息");
			return map;
		}
		Map<String, Object> ms = null;
		ms = filtrateService.getFiltrateByMediaType(userid, mediatype);
		if (ms!=null) {
			map.put("ReturnType", "1001");
			map.put("ResultList", ms);
			return map;
		} else {
			map.put("ReturnType", "1011");
			map.put("Message", "没有查到任何内容");
			return map;
		}
	}
}
