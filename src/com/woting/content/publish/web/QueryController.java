package com.woting.content.publish.web;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.spiritdata.framework.util.RequestUtils;
import com.spiritdata.framework.util.StringUtils;
import com.woting.content.publish.service.QueryService;
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
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/getContents.do")
	@ResponseBody
	public Map<String, Object> getContents(HttpServletRequest request) {
		long begtime = System.currentTimeMillis();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String catalogsid = null;
		String flowFlag = null;
		String publisherId = null;
		Timestamp begincontentpubtime = null;
		Timestamp endcontentpubtime = null;
		Timestamp begincontentctime = null;
		Timestamp endcontentctime = null;
//		String userId = m.get("UserId")+"";
		int page = m.get("Page") == null ? 1 : Integer.valueOf(m.get("Page")+"");
		int pagesize = m.get("PageSize") == null ? 10 : Integer.valueOf((String) m.get("PageSize"));
		if (m.containsKey("CatalogsId"))
			catalogsid = (String) m.get("CatalogsId");
		if (m.containsKey("ContentFlowFlag"))
			flowFlag = m.get("ContentFlowFlag") == null ? null : m.get("ContentFlowFlag")+"";
		if (m.containsKey("SourceId"))
			publisherId = (String) m.get("SourceId");
		if (m.containsKey("BeginContentPubTime") && m.get("BeginContentPubTime")!=null && !m.get("BeginContentPubTime").equals("null"))
			begincontentpubtime = new Timestamp(Long.valueOf(m.get("BeginContentPubTime")+""));
		if (m.containsKey("EndContentPubTime") && m.get("EndContentPubTime")!=null && !m.get("EndContentPubTime").equals("null"))
			endcontentpubtime =  new Timestamp(Long.valueOf(m.get("EndContentPubTime")+""));
		if (m.containsKey("BeginContentCTime") && m.get("BeginContentCTime")!=null && !m.get("BeginContentCTime").equals("null"))
			begincontentctime =  new Timestamp(Long.valueOf(m.get("BeginContentCTime")+""));
		if (m.containsKey("EndContentCTime") && m.get("EndContentCTime")!=null && !m.get("EndContentCTime").equals("null"))
			endcontentctime =  new Timestamp(Long.valueOf(m.get("EndContentCTime")+""));
		if (page > 0 && pagesize > 0) {
			Map<String, Object> maplist = queryService.getContent(flowFlag, page, pagesize, catalogsid, publisherId, begincontentpubtime, endcontentpubtime, begincontentctime, endcontentctime);
			if ((Long)maplist.get("Count") > 0) {
				map.put("ResultList", maplist.get("List"));
				map.put("ReturnType", "1001");
				map.put("AllCount", maplist.get("Count"));
			} else {
				map.put("ReturnType", "1011");
				map.put("AllCount", maplist.get("Count"));
			}
		} else {
			map.put("ReturnType", "1012");
			map.put("Message", "请求信息有误");
		}
		map.put("TestDurtion", System.currentTimeMillis()-begtime);
		return map;
	}

	/**
	 * 查询节目详细信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/getContentInfo.do")
	@ResponseBody
	public Map<String, Object> getContentInfo(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
//		String userId = (String) m.get("UserId");
		int pagesize = m.get("PageSize") == null ? -1 : Integer.valueOf((String) m.get("PageSize"));
		int page = m.get("Page") == null ? -1 : Integer.valueOf((String) m.get("Page"));
		String id = (String) m.get("ContentId");
		String mediatype = m.get("MediaType")+"";
		Map<String, Object> mapdetail = queryService.getContentInfo(pagesize, page, id, mediatype);
		if (mediatype.equals("wt_SeqMediaAsset")) {
			if (mapdetail.get("audio") != null) {
				map.put("ContentDetail", mapdetail.get("sequ"));
				map.put("SubList", mapdetail.get("audio"));
				map.put("ReturnType", "1001");
			} else {
				map.put("ReturnType", "1011");
				map.put("Message", "没有相关内容 ");
			}
		} else {
			if (mediatype.equals("wt_MediaAsset")) {
				if (map.isEmpty()) {
					map.put("ContentDetail", mapdetail);
					map.put("ReturnType", "1001");
				} else {
					map.put("ReturnType", "1011");
					map.put("Message", "没有相关内容 ");
				}
			} else {
				if (mediatype.equals("wt_Broadcast")) {
					map.put("SubList", mapdetail);
					map.put("ReturnType", "1001");
				} else {
					map.put("ReturnType", "1011");
					map.put("Message", "没有相关内容 ");
				}
			}
		}
		return map;
	}

	/**
	 * 修改序号和审核状态
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/content/updateContentStatus.do")
	@ResponseBody
	public Map<String, Object> updateContentStatus(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
//		String userId = (String) m.get("UserId");
		List<Map<String, Object>> contentIds = new ArrayList<>();
		if (m.containsKey("ContentIds")) {
			contentIds = (List<Map<String, Object>>) m.get("ContentIds");
			if (contentIds==null || contentIds .size()==0) {
				map.put("ReturnType", "1012");
				map.put("Message", "无内容Id");
				return map;
			}
		} else {
			map.put("ReturnType", "1012");
			map.put("Message", "无内容Id");
			return map;
		}
		String opeType = m.get("OpeType") + "";
		if (StringUtils.isNullOrEmptyOrSpace(opeType) || opeType.toLowerCase().equals("null")) {
			map.put("ReturnType", "1014");
			map.put("Message", "无操作类型");
			return map;
		}
		String channelIds = m.get("ChannelIds") + "";
		if (StringUtils.isNullOrEmptyOrSpace(channelIds) || channelIds.toLowerCase().equals("null")) {
			channelIds = null;
		}
		boolean isok = false;
		if (opeType.equals("sort")) {
			String numbers = (String) m.get("ContentSort");
			int number = 0;
			try {
				number = Integer.valueOf(numbers);
			} catch (Exception e) {
				e.printStackTrace();
				map.put("ReturnType", "1015");
				map.put("Message", "无排序号");
				return map;
			}
			isok = queryService.modifyInfo(contentIds, number, opeType);
		} else {
			if (opeType.equals("pass") || opeType.equals("nopass") || opeType.equals("revoke")) {
				isok = queryService.modifyInfo(contentIds, 0, opeType);
			}
		}
		if (isok) {
			map.put("ReturnType", "1001");
			map.put("Message", "修改成功");
			return map;
		} else {
			map.put("ReturnType", "1011");
			map.put("Message", "修改失败");
			return map;
		}
	}

	/**
	 * 查询栏目分类和发布组织信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/getConditions.do")
	@ResponseBody
	public Map<String, Object> getCatalogs(HttpServletRequest request) {
//		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
//		String userId = (String) m.get("UserId");
		Map<String, Object> map = new HashMap<String, Object>();
		map = queryService.getConditionsInfo();
		map.put("ReturnType", "1001");
		return map;
	}

	/**
	 * 分享页的分页加载请求
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/getZJSubPage.do")
	@ResponseBody
	public Map<String, Object> getZJSubPage(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String contentId = m.get("ContentId") + "";
		if (StringUtils.isNullOrEmptyOrSpace(contentId) || contentId.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无内容ID");
			return map;
		}
		String page = m.get("Page") + "";
		if (StringUtils.isNullOrEmptyOrSpace(page) || page.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无页码");
			return map;
		}
		map = queryService.getZJSubPage(contentId, page);
		return map;
	}
}
