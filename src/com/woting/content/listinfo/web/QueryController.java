package com.woting.content.listinfo.web;

import java.sql.Timestamp;
import java.util.HashMap;
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

	/**
	 * 查询列表信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/listinfo/getlist.do")
	@ResponseBody
	public Map<String, Object> getList(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestDataUtils.getDataFromRequest(request);
		String catalogsid = null;
		int flowFlag = 0;
		String source = null;
		Timestamp begincontentpubtime = null;
		Timestamp endcontentpubtime = null;
		Timestamp begincontentctime = null;
		Timestamp endcontentctime = null;
		String userId = (String) m.get("UserId");
		int page = m.get("Page") == null ? -1 : Integer.valueOf((String) m.get("Page"));
		int pagesize = m.get("PageSize") == null ? -1 : Integer.valueOf((String) m.get("PageSize"));
		if(m.containsKey("CatalogsId")){
			catalogsid = (String) m.get("CatalogsId");}
		if(m.containsKey("ContentFlowFlag")){
			flowFlag = m.get("ContentFlowFlag") == null ? -1 : Integer.valueOf((String) m.get("ContentFlowFlag"));}
		if(m.containsKey("SourceId")){
			source = (String) m.get("SourceId");}
		if(m.containsKey("BeginContentPubTime")){
			begincontentpubtime = (Timestamp) m.get("BeginContentPubTime");}
		if(m.containsKey("EndContentPubTime")){
			endcontentpubtime = (Timestamp) m.get("EndContentPubTime");}
		if(m.containsKey("BeginContentCTime")){
			begincontentctime = (Timestamp) m.get("BeginContentCTime");}
		if(m.containsKey("EndContentCTime")){
			endcontentctime = (Timestamp) m.get("EndContentCTime");}
		System.out.println(userId + "#" + flowFlag + "#" + page + "#" + pagesize);
		if (userId != null) {
			if (flowFlag > 0 && page > 0 && pagesize > 0) {
				Map<String, Object> maplist = queryService.getList(flowFlag,
						page, pagesize,catalogsid,source,begincontentpubtime,endcontentpubtime,begincontentctime,endcontentctime);
				map.put("ResultList", maplist.get("List"));
				map.put("ReturnType", "1001");
				map.put("ContentCount", maplist.get("Count"));
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

	/**
	 * 查询节目详细信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/listinfo/getlistinfo.do")
	@ResponseBody
	public Map<String, Object> getListInfo(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestDataUtils.getDataFromRequest(request);
		String userId = (String) m.get("UserId");
		int pagesize = m.get("PageSize") == null ? -1 : Integer.valueOf((String) m.get("PageSize"));
		int page = m.get("Page") == null ? -1 : Integer.valueOf((String) m.get("Page"));
		String id = (String) m.get("ContentId");
		String mediatype = (String) m.get("MediaType");
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

	/**
	 * 修改序号和审核状态
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/listinfo/modifsort.do")
	@ResponseBody
	public Map<String, Object> modifSort(HttpServletRequest request) {
		Map<String, Object> m = RequestDataUtils.getDataFromRequest(request);
		int flowFlag = m.get("ContentFlowFlag") == null ? -1 : Integer.valueOf((String) m.get("ContentFlowFlag"));
		String userId = (String) m.get("UserId");
		String ids = (String) m.get("Id");
		String numbers = (String) m.get("ContentSort");
		String opeType = (String) m.get("OpeType");
		System.out.println(flowFlag + "#" + ids + "#" + numbers +"#"+ opeType);
		Map<String, Object> map = queryService.modifInfo(ids, numbers, flowFlag,opeType);
		return map;
	}

	/**
	 * 获得分类和发布组织信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/listinfo/getcriteriainfo.do")
	@ResponseBody
	public Map<String, Object> getCatalogs(HttpServletRequest request) {
		Map<String, Object> m = RequestDataUtils.getDataFromRequest(request);
		System.out.println(m);
		String userId = (String) m.get("UserId");
		Map<String, Object> map = new HashMap<String, Object>();
		map = queryService.getCriteriaInfo();
		map.put("ReturnType", "1001");
		return map;
	}

}
