package com.woting.passport.login.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.woting.passport.login.service.LoginWebServiceImpl;
import com.woting.passport.login.utils.GetDataFromRequest;

/**
 * 
 * @author wbq
 *
 */
@Controller
public class LoginWebController {

	GetDataFromRequest datautils = new GetDataFromRequest();
	LoginWebServiceImpl loginWebService = new LoginWebServiceImpl();

	@RequestMapping(value = "/passport/user/login.do")
	@ResponseBody
	public Map<String, Object> loginWeb(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		/*
		 * Map<String, Object> param1 =
		 * RequestUtils.getDataFromRequestParam(request); Map<String, Object>
		 * param2 = RequestUtils.getDataFromRequestStream(request);
		 * 
		 * // 获取参数UserName String userName = param1.get("UserName") + "";
		 * System.out.println("1:"+userName); if
		 * (StringUtils.isNullOrEmptyOrSpace(userName) ||
		 * userName.toUpperCase().equals("NULL")) { userName =
		 * param2.get("UserName") + ""; System.out.println("2:"+userName); } if
		 * (StringUtils.isNullOrEmptyOrSpace(userName) ||
		 * userName.toUpperCase().equals("NULL")) { userName = null;
		 * map.put("Message", "无登陆用户信息"); map.put("ReturnType", "1002"); } else
		 * { if (userName == username) { map.put("Message", "用户登录");
		 * map.put("ReturnType", "1001"); } }
		 */
		Map<String, Object> m = datautils.getDataFromRequest(request);
		map = loginWebService.loginWebService(m);
		System.out.println(map);
		return map;
	}
}
