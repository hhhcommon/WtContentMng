package com.woting.content.share.web;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.spiritdata.framework.util.RequestUtils;
import com.spiritdata.framework.util.StringUtils;
import com.woting.content.share.service.ShareService;
import com.woting.passport.session.SessionService;

@Controller
public class ShareController {
	@Resource
	private ShareService shareService;
	@Resource(name = "redisSessionService")
	private SessionService sessionService;

	/**
	 * 发布所有已审核的节目 只用于测试用
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/share/makeContentShareHtml.do")
	@ResponseBody
	public Map<String, Object> makeShareHtml(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String contentId = m.get("ContentId") + "";
		if (StringUtils.isNullOrEmptyOrSpace(contentId) || contentId.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无内容ID");
			return map;
		}
		String mediaType = m.get("MediaType") + "";
		if (StringUtils.isNullOrEmptyOrSpace(mediaType) || mediaType.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无内容类型");
			return map;
		}
		boolean isok = shareService.getShareHtml(contentId, mediaType);
		if (isok) {
			map.put("ReturnType", "1001");
			map.put("Message", "静态页面生成成功");
		} else {
			map.put("ReturnType", "1011");
			map.put("Message", "静态页面生成失败");
		}
		return map;
	}

	/**
	 * 生成微信认证的签名
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/share/getWXConfig.do")
	@ResponseBody
	public Map<String, Object> getWXConfig(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String sharePath = m.get("SharePath") + "";
		if (StringUtils.isNullOrEmptyOrSpace(sharePath) || sharePath.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无分享地址");
			return map;
		}
		Map<String, Object> retM = shareService.getWXConfigInfo(sharePath);
		if (retM != null) {
			map.put("ReturnType", "1001");
			map.put("Result", retM);
			return map;
		} else {
			map.put("ReturnType", "1012");
			map.put("Message", "获取失败");
			return map;
		}
	}
	
	@RequestMapping(value = "/share/makeOSSInfo.do")
	@ResponseBody
	public Map<String, Object> makeOSSInfo(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String id = m.get("Id") + "";
		if (StringUtils.isNullOrEmptyOrSpace(id) || id.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无分享地址");
			return map;
		}
		shareService.makeOSSInfo(id);

		map.put("ReturnType", "1001");
		return map;
	}
}
