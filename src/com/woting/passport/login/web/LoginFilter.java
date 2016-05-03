package com.woting.passport.login.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginFilter implements Filter {
	private static Logger logger = LoggerFactory.getLogger(LoginFilter.class);
	private String ingores;
	private String noLogin;
	private String hasNewLogin;
	private String errorPage;

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.ingores = config.getInitParameter("ingores");
		this.noLogin = config.getInitParameter("noLogin");
		this.hasNewLogin = config.getInitParameter("hasNewLogin");
		this.errorPage = config.getInitParameter("errorPage");
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession();
		String path = request.getServletPath();
		String[] ingoresArray = ingores.split(",");
		if (isIngore(path, ingoresArray))
			chain.doFilter(req, res);
		else if (path.endsWith(".css")||path.endsWith(".js")||path.endsWith(".json")) chain.doFilter(req, res);
		else {
			if (session.getAttribute("userInfo") != null) {
				System.out.println("跳转"+request.getContextPath() + noLogin);
				response.sendRedirect(request.getContextPath() + noLogin);
			} else {
				System.out.println("继续执行");
				chain.doFilter(request, response);
			}
		}
	}

	private boolean isIngore(String path, String ingores[]) {
		String _path = (path.indexOf("?") > 0) ? path.substring(0, path.indexOf("?")) : path;
		_path = (_path.indexOf(".do") > 0)
				? (_path.indexOf("!") > 0 ? (_path.substring(0, _path.indexOf("!")) + ".do") : _path) : _path;
		for (int i = 0; i < ingores.length; i++) {
			String ingore = ingores[i];
			if (_path.indexOf(ingore) >= 0)
				return true;
		}
		return false;
	}

}
