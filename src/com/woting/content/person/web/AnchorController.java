package com.woting.content.person.web;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.framework.util.RequestUtils;
import com.woting.content.person.service.AnchorService;

@Controller
public class AnchorController {
	@Resource
	private AnchorService anchorService;
	
	/**
	 * 获得主播列表请求
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/person/getPersons.do")
	@ResponseBody
	public Map<String, Object> getPersons(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String sourceId = null;
		try {sourceId=(String) m.get("SourceId");} catch(Exception e) {}
		String statusType = null;
		try {statusType=(String) m.get("StatusType");} catch(Exception e) {}
		//得到每页记录数
        int pageSize=10;
        try {pageSize=Integer.parseInt(m.get("PageSize")+"");} catch(Exception e) {};
        //得到当前页数
        int page=1;
        try {page=Integer.parseInt(m.get("Page")+"");} catch(Exception e) {};
		Map<String, Object> retM = anchorService.getPersonList(sourceId,statusType,page,pageSize);
		if (retM!=null) {
			map.put("ReturnType", "1001");
			map.put("ResultInfo", retM);
		} else {
			map.put("ReturnType", "1011");
		}
		return map;
	}
}
