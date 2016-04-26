package com.woting.passport.login.web;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.woting.passport.login.service.LoginWebService;
import com.woting.passport.login.utils.RequestDataUtils;

/**
 * 
 * @author wbq
 *
 */
@Controller
public class LoginWebController {
	@Resource
	private LoginWebService loginWebService;
	
	@RequestMapping(value = "/passport/user/login.do")
	@ResponseBody
	public Map<String, Object> loginWeb(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestDataUtils.getDataFromRequest(request);
		map = loginWebService.loginWebService(m);
		System.out.println(map);
		return map;
	}
}
