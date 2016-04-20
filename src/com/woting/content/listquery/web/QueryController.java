package com.woting.content.listquery.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.woting.content.listquery.service.QueryService;
import com.woting.passport.login.utils.RequestDataUtils;

@Controller
public class QueryController {
	@Resource
	private QueryService queryService;

	@RequestMapping(value = "/content/listquery/query.do")
	@ResponseBody
	public Map<String, Object> listQuery(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestDataUtils.getDataFromRequest(request);
		System.out.println(m);
		String userId = m.get("userId") == null ? null : (String) m.get("userId");
		int flowFlag = m.get("flowFlag") == null ? -1 : Integer.valueOf((String) m.get("flowFlag"));
		int currentpage = m.get("page") == null ? -1 : Integer.valueOf((String) m.get("page"));
		int pagesize = m.get("pageSize") == null ? -1 : Integer.valueOf((String) m.get("pageSize"));
		System.out.println(userId+"#"+flowFlag+"#"+currentpage+"#"+pagesize);
		if (userId != null) {
			if (flowFlag > 0 && currentpage > 0 && pagesize > 0) {
				List<Map<String, Object>> list = (List<Map<String, Object>>) queryService.querylist(flowFlag, currentpage, pagesize);
				map.put("resultList", list);
				map.put("returnType", "1001");
				map.put("count", list.size());
			}else {
				map.put("returnType", "1002");
				map.put("message", "请求信息有误");
			}
		}else{
			map.put("returnType", "1002");
			map.put("message", "用户信息有误");
		}
		return map;
	}
}
