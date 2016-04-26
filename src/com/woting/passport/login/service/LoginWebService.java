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

	public Map<String, Object> loginWebService(Map<String, Object> m) {
		Map<String, Object> map = new HashMap<String, Object>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean log = false;
		String loginname = null;
		String password = null;

		String sql = "select loginName,password from plat_User where loginName=? and password=?";
		if (m.isEmpty() || m.size() == 0) {
			map.put("Message", "无登陆用户信息 ");
			map.put("ReturnType", "1002");
		} else {
			if (!m.containsKey("UserName")) {
				loginname = null;
			} else {
				loginname = (String) m.get("UserName");
			}
			if (!m.containsKey("Password")) {
				password = null;
			} else {
				password = (String) m.get("Password");
			}
			if (loginname == null || password == null) {
				map.put("Message", "登陆信息不全");
				map.put("ReturnType", "1002");
			} else {
				try {
					conn = dataSource.getConnection();
					ps = conn.prepareStatement(sql);
					ps.setString(1, loginname);
					ps.setString(2, password);
					rs = ps.executeQuery();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if (rs == null) {
					map.put("Message", "密码不匹配");
					map.put("ReturnType", "1002");
				} else {
					try {
						map.put("menuListId", rs.getString("id"));
					} catch (SQLException e) {
						e.printStackTrace();
					}
					map.put("ReturnType", "1001");
				}
			}
		}
		return map;
	}
}
