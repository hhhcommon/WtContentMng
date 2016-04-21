package com.woting.content.listquery.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.framework.util.StringUtils;
import com.woting.content.listquery.service.QueryService;
import com.woting.passport.login.utils.RequestDataUtils;

/**
 * 列表查询接口
 * 
 * @author wbq
 *
 */
@Controller
public class QueryController {
	@Resource
	private QueryService queryService;

	// 查询列表信息
	@RequestMapping(value = "/content/listquery/query.do")
	@ResponseBody
	public Map<String, Object> listQuery(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestDataUtils.getDataFromRequest(request);
		System.out.println(m);
		String userId = m.get("UserId") == null ? null : (String) m.get("UserId");
		int flowFlag = m.get("FlowFlag") == null ? -1 : Integer.valueOf((String) m.get("FlowFlag"));
		int currentpage = m.get("Page") == null ? -1 : Integer.valueOf((String) m.get("Page"));
		int pagesize = m.get("PageSize") == null ? -1 : Integer.valueOf((String) m.get("PageSize"));
		System.out.println(userId + "#" + flowFlag + "#" + currentpage + "#" + pagesize);
		if (userId != null) {
			if (flowFlag > 0 && currentpage > 0 && pagesize > 0) {
				List<Map<String, Object>> list = (List<Map<String, Object>>) queryService.queryList(flowFlag,
						currentpage, pagesize);
				map.put("ResultList", list);
				map.put("ReturnType", "1001");
				map.put("Count", list.size());
			} else {
				map.put("ReturnType", "1002");
				map.put("Message", "请求信息有误");
			}
		} else {
			map.put("ReturnType", "1002");
			map.put("Message", "用户信息有误");
		}
		return map;
	}

	@RequestMapping(value = "/content/listquery/detailquery.do")
	@ResponseBody
	public Map<String, Object> detailQuery(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestDataUtils.getDataFromRequest(request);
		System.out.println(m);
		String userId = m.get("UserId") == null ? null : (String) m.get("UserId");
		int pagesize = m.get("PageSize") == null ? -1 : Integer.valueOf((String) m.get("PageSize"));
		int page = m.get("Page") == null ? -1 : Integer.valueOf((String) m.get("Page"));
		String id = m.get("Id") == null ? null : (String) m.get("Id");
		String acttype = m.get("ActType") == null ? null : (String) m.get("ActType");
		System.out.println(page + "#" + pagesize + "#" + id + "#" + acttype);
		Map<String, Object> mapdetail = queryService.queryDetail(pagesize, page, id, acttype);
		if (mapdetail.get("audio") != null) {
			map.put("ActDetail", mapdetail.get("sequ"));
			map.put("ItemList", mapdetail.get("audio"));
			map.put("ReturnType", "1001");
			map.put("ItemCount", mapdetail.get("count"));
		}else{
			map.put("ReturnType", "1011");
			map.put("Message", "没有相关内容 ");
		}
		return map;
	}
}
