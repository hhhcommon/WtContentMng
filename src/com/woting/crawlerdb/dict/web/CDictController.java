package com.woting.crawlerdb.dict.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.spiritdata.framework.util.RequestUtils;
import com.woting.crawlerdb.dict.service.CDictService;

/**
 * 映射关系控制
 * @author wbq
 *
 */
@Controller
@RequestMapping(value="/common/")
public class CDictController {
	@Resource
	CDictService cDictService;

	/**
	 * 获取抓取库的分类数据
	 * @param request
	 * @return
	 */
	@RequestMapping(value="getCCategory.do")
    @ResponseBody
	public Map<String, Object> getCDictD(HttpServletRequest request) {
		Map<String,Object> map=new HashMap<String, Object>();
		Map<String, Object> m=RequestUtils.getDataFromRequest(request);
		if (m==null||m.size()==0) {
            map.put("ReturnType", "1011");
            map.put("Message", "参数为空");
            return map;
        }
		String cdictmid = m.get("CDictMId")+"";
		if(cdictmid.equals("null")) {
			map.put("ReturnType", "1011");
            map.put("Message", "CDictMId参数为空");
            return map;
		}
		String isvali = m.get("IsValidate")+"";
		if(isvali.equals("null")) {
			map.put("ReturnType", "1011");
            map.put("Message", "IsValidate参数为空");
            return map;
		}
		String publisher = m.get("Publisher")+"";
		if(publisher.equals("null")) {
			map.put("ReturnType", "1011");
            map.put("Message", "Publisher参数为空");
            return map;
		}
		String maxdepth = m.get("MaxDepth")+"";
		if(maxdepth.equals("null")) maxdepth = "1";
		int isvalidate = Integer.valueOf(isvali);
		List<Map<String, Object>> cdds = cDictService.getCDictDList(cdictmid, isvalidate, publisher, maxdepth);
		if(cdds==null || cdds.size()==0) {
			map.put("ReturnType", "1012");
            map.put("Message", "数据为空");
            return map;
		}
		map.put("ReturnType", "1001");
		map.put("AllCount", cdds.size());
		map.put("ResultList", cdds);
		return map;
	}
	
	/**
	 * 添加资源库内容分类与抓取库的映射关系
	 * @param request
	 * @return
	 */
	@RequestMapping(value="addCCateRef.do")
    @ResponseBody
    public Map<String, Object> addCDictDAndDictDRef(HttpServletRequest request) {
		Map<String,Object> map=new HashMap<String, Object>();
		Map<String, Object> m=RequestUtils.getDataFromRequest(request);
		if (m==null||m.size()==0) {
            map.put("ReturnType", "1011");
            map.put("Message", "参数为空");
            return map;
        }
		String applyType = m.get("ApplyType")+"";
		if(applyType.equals("null")) {
			map.put("ReturnType", "1011");
            map.put("Message", "ApplyType参数为空");
            return map;
		}
		String id = m.get("Id")+"";
		if(id.equals("null")) {
			map.put("ReturnType", "1011");
            map.put("Message", "栏目Id参数为空");
            return map;
		}
		String refIds = m.get("RefIds")+"";
		if(refIds.equals("null")) {
			map.put("ReturnType", "1011");
            map.put("Message", "关联栏目Ids参数为空");
            return map;
		}
		boolean isok = cDictService.addCDDAndDDRef(applyType, id, refIds);
		if(isok) {
			map.put("ReturnType", "1001");
			return map;
		} else {
			map.put("ReturnType", "1013");
			map.put("Message", "创建失败");
			return map;
		}
	}
	
	/**
	 * 查询资源库与抓取库字典映射关系
	 * @param request
	 * @return
	 */
	@RequestMapping(value="getCCateRefs.do")
    @ResponseBody
    public Map<String, Object> getCCateRefInfo(HttpServletRequest request) {
		Map<String,Object> map=new HashMap<String, Object>();
		Map<String, Object> m=RequestUtils.getDataFromRequest(request);
		if (m==null||m.size()==0) {
            map.put("ReturnType", "1011");
            map.put("Message", "参数为空");
            return map;
        }
		String applyType = m.get("ApplyType")+"";
		if(applyType.equals("null")) {
			map.put("ReturnType", "1011");
            map.put("Message", "ApplyType为空");
            return map;
		}
		String id = m.get("Id")+"";
		if(id.equals("null")) {
			map.put("ReturnType", "1011");
            map.put("Message", "id参数为空");
            return map;
		}
		String publishers = null;
		try {publishers = m.get("Publishers").toString();} catch (Exception e) {}

		List<Map<String, Object>> res = cDictService.getCCateResRef(applyType, id, publishers);
		if(res!=null && res.size()>0) {
			map.put("ReturnType", "1001");
			map.put("AllCount", res.size());
			map.put("ResultList", res);
		} else {
			map.put("ReturnType", "1012");
			map.put("Message", "数据为空");
		}
		return map;
	}
	
	/**
	 * 删除资源库与抓取库字典映射关系
	 * @param request
	 * @return
	 */
	@RequestMapping(value="removeCCateRefs.do")
    @ResponseBody
    public Map<String, Object> RemoveCCateRefs(HttpServletRequest request) {
		Map<String,Object> map=new HashMap<String, Object>();
		Map<String, Object> m=RequestUtils.getDataFromRequest(request);
		if (m==null||m.size()==0) {
            map.put("ReturnType", "1011");
            map.put("Message", "参数为空");
            return map;
        }
		String id = m.get("Id")+"";
		if(id.equals("null")) {
			map.put("ReturnType", "1011");
            map.put("Message", "Id参数为空");
            return map;
		}
		boolean isok = cDictService.delDictResRef(id);
		if(isok) {
			map.put("ReturnType", "1001");
			map.put("Message", "删除成功");
		} else {
			map.put("ReturnType", "1012");
			map.put("Message", "删除失败");
		}
		return map;
	}
	
	@RequestMapping(value="saveCCateRefs.do")
    @ResponseBody
    public Map<String, Object> SaveCCateRefs(HttpServletRequest request) {
		Map<String,Object> map=new HashMap<String, Object>();
		boolean isok = cDictService.saveCrawlerFile();
		if(isok) {
			map.put("ReturnType", "1001");
			map.put("Message", "文件生成成功");
		} else {
			map.put("ReturnType", "1012");
			map.put("Message", "文件生成失败");
		}
		return map;
	}
}
