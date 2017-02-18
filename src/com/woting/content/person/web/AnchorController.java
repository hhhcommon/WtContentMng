package com.woting.content.person.web;

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
	@RequestMapping(value = "/person/getPersonInfo.do")
	@ResponseBody
	public Map<String, Object> getPersonInfo(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String personId = m.get("PersonId") + "";
		if (StringUtils.isNullOrEmptyOrSpace(personId) || personId.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无主播Id");
			return map;
		}
		Map<String, Object> retM = anchorService.getPersonInfo(personId);
		if (retM!=null) {
			map.put("ReturnType", "1001");
			map.put("ResultInfo", retM);
			return map;
		} 
		map.put("ReturnType", "1011");
		return map;
	}
	
	/**
	 * 获得主播列表请求
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/person/getPersons.do")
	@ResponseBody
	public Map<String, Object> getPersons(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String sourceId = null;
		try {sourceId=(String) m.get("SourceId");} catch(Exception e) {}
		String statusType = null;
		try {statusType=(String) m.get("StatusType");} catch(Exception e) {}
		String searchWord = null;
		try {searchWord=(String) m.get("SearchWord");} catch(Exception e) {}
		//得到每页记录数
        int pageSize=10;
        try {pageSize=Integer.parseInt(m.get("PageSize")+"");} catch(Exception e) {};
        //得到当前页数
        int page=1;
        try {page=Integer.parseInt(m.get("Page")+"");} catch(Exception e) {};
		Map<String, Object> retM = anchorService.getPersonList(searchWord,sourceId,statusType,page,pageSize);
		if (retM!=null) {
			List<Map<String, Object>> ls = (List<Map<String, Object>>) retM.get("List");
			if (ls!=null && ls.size()>0) {
				map.put("ReturnType", "1001");
			    map.put("ResultInfo", retM);
			    return map;
			} 
		} 
		map.put("ReturnType", "1011");
		return map;
	}
	
	/**
	 * 获得主播列表请求
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/person/getPersonContents.do")
	@ResponseBody
	public Map<String, Object> getPersonContents(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String personId = m.get("PersonId") + "";
		if (StringUtils.isNullOrEmptyOrSpace(personId) || personId.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无主播Id");
			return map;
		}
		String mediaType = "SEQU";
		try {mediaType=(String) m.get("MediaType");} catch(Exception e) {}
		int sortType = 1;
		try {sortType= (int) m.get("SortType");} catch(Exception e) {}
		//得到每页记录数
        int pageSize=10;
        try {pageSize=Integer.parseInt(m.get("PageSize")+"");} catch(Exception e) {};
        //得到当前页数
        int page=1;
        try {page=Integer.parseInt(m.get("Page")+"");} catch(Exception e) {};
		Map<String, Object> retM = anchorService.getPersonContentList(personId, mediaType, sortType, page, pageSize);
		if (retM!=null) {
			List<Map<String, Object>> ls = (List<Map<String, Object>>) retM.get("List");
			if (ls!=null && ls.size()>0) {
				map.put("ReturnType", "1001");
			    map.put("ResultInfo", retM);
			    return map;
			} 
		} 
		map.put("ReturnType", "1011");
		return map;
	}
	
	/**
	 * 获得主播列表请求
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/person/updatePersonStatus.do")
	@ResponseBody
	public Map<String, Object> updatePersonStatus(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String personIds = m.get("PersonIds") + "";
		if (StringUtils.isNullOrEmptyOrSpace(personIds) || personIds.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无主播Id");
			return map;
		}
		String statusType = m.get("StatusType") + "";
		if (StringUtils.isNullOrEmptyOrSpace(statusType) || statusType.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无状态Id");
			return map;
		}
		List<Map<String, Object>> retL = anchorService.updatePersonStatus(personIds,statusType);
		if (retL!=null) {
			map.put("ReturnType", "1011");
			map.put("ResultInfo", retL);
			return map;
		} 
		map.put("ReturnType", "1001");
		return map;
	}
}
