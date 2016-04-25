package com.woting.passport.login.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.stereotype.Service;

@Service
public class LoginWebService {
	@Resource
	private DataSource dataSource;
	private String username = "zhangsan";
	private String password = "111111";

	public Map<String, Object> loginWebService(Map<String, Object> m) {
		Map<String, Object> map = new HashMap<String, Object>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean log = false;
		String sql = "select loginName,password from plat_User where loginName=? and password=?";
		if (m.isEmpty() || m.size() == 0) {
			map.put("Message", "无登陆用户信息 ");
			map.put("ReturnType", "1002");
		} else {
			if (m.get("userName") == null || m.get("password") == null) {
				map.put("Message", "登陆信息不全");
				map.put("ReturnType", "1002");
			} else {
				try {
					conn = dataSource.getConnection();
					ps = conn.prepareStatement(sql);
					ps.setString(1, (String) m.get("UserName"));
					ps.setString(2, (String) m.get("Password"));
					log = ps.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if (!log) {
					map.put("Message", "密码不匹配");
					map.put("ReturnType", "1002");
				} else {
					List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
					List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
					Map<String, Object> m2 = new HashMap<String, Object>();
					Map<String, Object> m3 = new HashMap<String, Object>();
					m2.put("menuListId", "353434344");
					m2.put("menuListName", "待审核");
					m2.put("menuListStatus", "0");
					list1.add(m2);
					m3.put("List", list1);
					m3.put("menuGroupId", "0023fasf2fasd");
					m3.put("menuGroupName", "发布管理");
					list2.add(m3);
					map.put("menuList", list2);
					map.put("ReturnType", "1001");
				}
			}

		}
		return map;
	}
}
