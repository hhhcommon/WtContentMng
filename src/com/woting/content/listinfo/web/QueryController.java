package com.woting.content.listinfo.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.woting.content.listinfo.service.QueryService;
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
	@RequestMapping(value = "/content/listinfo/getlist.do")
	@ResponseBody
	public Map<String, Object> getList(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestDataUtils.getDataFromRequest(request);
		System.out.println(m);
		int flowFlag = m.get("ContentFlowFlag") == null ? -1 : Integer.valueOf((String) m.get("ContentFlowFlag"));
		String userId = m.get("UserId") == null ? null : (String) m.get("UserId");
		int currentpage = m.get("Page") == null ? -1 : Integer.valueOf((String) m.get("Page"));
		int pagesize = m.get("PageSize") == null ? -1 : Integer.valueOf((String) m.get("PageSize"));
		System.out.println(userId + "#" + flowFlag + "#" + currentpage + "#" + pagesize);
		if (userId != null) {
			if (flowFlag > 0 && currentpage > 0 && pagesize > 0) {
				List<Map<String, Object>> list = (List<Map<String, Object>>) queryService.getList(flowFlag,
						currentpage, pagesize);
				map.put("ResultList", list);
				map.put("ReturnType", "1001");
				map.put("ContentCount", list.size());
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

	// 详细信息查询
	@RequestMapping(value = "/content/listinfo/getlistinfo.do")
	@ResponseBody
	public Map<String, Object> getListInfo(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestDataUtils.getDataFromRequest(request);
		System.out.println(m);
		String userId = m.get("UserId") == null ? null : (String) m.get("UserId");
		int pagesize = m.get("PageSize") == null ? -1 : Integer.valueOf((String) m.get("PageSize"));
		int page = m.get("Page") == null ? -1 : Integer.valueOf((String) m.get("Page"));
		String id = m.get("ContentId") == null ? null : (String) m.get("ContentId");
		String mediatype = m.get("MediaType") == null ? null : (String) m.get("MediaType");
		System.out.println(page + "#" + pagesize + "#" + id + "#" + mediatype);
		Map<String, Object> mapdetail = queryService.getListInfo(pagesize, page, id, mediatype);
		if (mapdetail.get("audio") != null) {
			map.put("ContentDetail", mapdetail.get("sequ"));
			map.put("SubList", mapdetail.get("audio"));
			map.put("ReturnType", "1001");
			map.put("ContentCount", mapdetail.get("count"));
		} else {
			map.put("ReturnType", "1011");
			map.put("Message", "没有相关内容 ");
		}
		return map;
	}

	// 修改排序号
	@RequestMapping(value = "/content/listinfo/modifsort.do")
	@ResponseBody
	public Map<String, Object> modifSort(HttpServletRequest request) {
		Map<String, Object> m = RequestDataUtils.getDataFromRequest(request);
		System.out.println(m);
		int flowFlag = m.get("ContentFlowFlag") == null ? -1 : Integer.valueOf((String) m.get("ContentFlowFlag"));
		String userId = m.get("UserId") == null ? null : (String) m.get("UserId");
		String id = m.get("Id") == null ? null : (String) m.get("Id");
		int sort = m.get("ContentSort") == null ? -1 : Integer.valueOf((String) m.get("ContentSort"));
		System.out.println(flowFlag + "#" + id + "#" + sort);
		Map<String, Object> map = queryService.modifSort(id, sort, flowFlag);
		return map;
	}

	/**
	 * 按条件查询所有
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/listinfo/findallbyrequest.do")
	@ResponseBody
	public Map<String, Object> findAllByRequest(HttpServletRequest request) {
		Map<String, Object> m = RequestDataUtils.getDataFromRequest(request);
		System.out.println(m);
		String userId = m.get("UserId") == null ? null : (String) m.get("UserId");
		int pagesize = m.get("PageSize") == null ? -1 : Integer.valueOf((String) m.get("PageSize"));
		int page = m.get("Page") == null ? -1 : Integer.valueOf((String) m.get("Page"));
		int flowFlag = m.get("ContentFlowFlag") == null ? -1 : Integer.valueOf((String) m.get("ContentFlowFlag"));
		String contentcatalogs = m.get("ContentCatalogs") == null ? null : (String) m.get("ContentCatalogs");
		String contentsource = m.get("ContentSource") == null ? null : (String) m.get("ContentSource");
		String begincontentpubtime = m.get("BeginContentPubTime") == null ? null
				: (String) m.get("BeginContentPubTime");
		String endcontentpubtime = m.get("EndContentPubTime") == null ? null : (String) m.get("EndContentPubTime");
		System.out.println(page + "#" + pagesize + "#" + flowFlag + "#" + contentcatalogs + "#" + contentsource + "#"
				+ begincontentpubtime + "#" + endcontentpubtime);
		return null;
	}

	/**
	 * 获得分类信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/listinfo/getcriteriainfo.do")
	@ResponseBody
	public Map<String, Object> getCatalogs(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		map = queryService.getCriteriaInfo();
		return map;
	}

}
