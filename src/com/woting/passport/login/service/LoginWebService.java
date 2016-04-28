package com.woting.passport.login.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class LoginWebService {
	private String username = "woting";
	private String password = "111111";

	public Map<String, Object> loginWebService(Map<String, Object> m) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (m.isEmpty() || m.size() == 0) {
			map.put("Message", "无登陆用户信息 ");
			map.put("ReturnType", "1002");
		} else {
			if (m.get("UserName") == null || m.get("Password") == null) {
				map.put("Message", "登陆信息不全");
				map.put("ReturnType", "1002");
			} else {
				if (!m.get("UserName").equals(username) || !m.get("Password").equals(password)) {
					map.put("Message", "密码不匹配");
					map.put("ReturnType", "1002");
				} else {
					if (m.get("UserName").equals(username) || m.get("Password").equals(password)) {
						List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
						List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
						Map<String, Object> m1 = new HashMap<String, Object>();
						Map<String, Object> m2 = new HashMap<String, Object>();
						Map<String, Object> m3 = new HashMap<String, Object>();
						Map<String, Object> m4 = new HashMap<String, Object>();
						Map<String, Object> m5 = new HashMap<String, Object>();
						m1.put("MenuListId", "353434344");
						m1.put("MenuListName", "待审核");
						m1.put("MenuListStatus", "1");
						m1.put("MenuListUrl", "./review/publish1.html");
						list1.add(m1);
						m2.put("MenuListId", "353242344");
						m2.put("MenuListName", "已审核");
						m2.put("MenuListStatus", "2");
						m2.put("MenuListUrl", "./review/publish2.html");
						list1.add(m2);
						m3.put("MenuListId", "351111344");
						m3.put("MenuListName", "未通过");
						m3.put("MenuListStatus", "3");
						m3.put("MenuListUrl", "./review/publish3.html");
						list1.add(m3);
						m4.put("MenuListId", "3453221344");
						m4.put("MenuListName", "已撤回");
						m4.put("MenuListStatus", "4");
						m4.put("MenuListUrl", "./review/publish4.html");
						list1.add(m4);
						m5.put("ItemList", list1);
						m5.put("MenuGroupId", "0023fasf2fasd");
						m5.put("MenuGroupName", "发布管理");
						list2.add(m5);
						map.put("MenuList", list2);
						map.put("ReturnType", "1001");
					}
				}
			}
		}
		return map;
	}
}
