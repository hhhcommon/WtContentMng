package com.woting.passport.login.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.woting.content.common.util.RequestUtils;
/**
 * 
 * @author wbq
 *
 */
@Controller
public class TestLoginController {

	private String username = "zhangsan";
	private String password = "111111";

	@RequestMapping(value = "/passport/login.do")
	@ResponseBody
	public Map<String, Object> loginWeb(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequestParam(request);
		if (m == null || m.size() == 0) {
			map.put("ReturnType", 1002);
			map.put("Message", "无登录名用户");
		} else {
			if (m.get("UserName") != username && m.get("Password") != password) {
				map.put("ReturnType", 1002);
				map.put("Message", "用户密码不匹配");
			} else {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				Map<String, Object> m2 = new HashMap<String, Object>();
				m2.put("menuListId", "353434344");
				m2.put("menuListName", "待审核");
				m2.put("menuListStatus", "0");
				list.add(m2);
				map.put("menuGroupId", "0023fasf2fasd");
				map.put("menuGroupName", "发布管理");
				map.put("List", list);
			}
		}
		return map;
	}
}
