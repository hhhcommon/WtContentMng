package com.woting.content.listquery.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.woting.content.listquery.service.QueryService;
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
    @RequestMapping(value = "/content/listquery/query.do")
    @ResponseBody
    public Map<String, Object> listQuery(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> m = RequestDataUtils.getDataFromRequest(request);
        System.out.println(m);
        int flowFlag = m.get("ContentFlowFlag") == null? -1 : Integer.valueOf((String) m.get("ContentFlowFlag"));
        String userId = m.get("UserId") == null ? null : (String) m.get("UserId");
        int currentpage = m.get("Page") == null ? -1 : Integer.valueOf((String) m.get("Page"));
        int pagesize = m.get("PageSize") == null ? -1 : Integer.valueOf((String) m.get("PageSize"));
        System.out.println(userId + "#" + flowFlag + "#" + currentpage + "#" + pagesize);
        if (userId != null) {
            if (flowFlag > 0 && currentpage > 0 && pagesize > 0) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) queryService.queryList(flowFlag,
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

    //详细信息查询
    @RequestMapping(value = "/content/listquery/detailquery.do")
    @ResponseBody
    public Map<String, Object> detailQuery(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> m = RequestDataUtils.getDataFromRequest(request);
        System.out.println(m);
        String userId = m.get("UserId") == null ? null : (String) m.get("UserId");
        int pagesize = m.get("PageSize") == null ? -1 : Integer.valueOf((String) m.get("PageSize"));
        int page = m.get("Page") == null ? -1 : Integer.valueOf((String) m.get("Page"));
        String id = m.get("ContentId") == null ? null : (String) m.get("ContentId");
        String mediatype = m.get("MediaType") == null ? null : (String) m.get("MediaType");
        System.out.println(page + "#" + pagesize + "#" + id + "#" + mediatype);
        Map<String, Object> mapdetail = queryService.queryDetail(pagesize, page, id, mediatype);
        if (mapdetail.get("audio") != null) {
            map.put("ContentDetail", mapdetail.get("sequ"));
            map.put("SubList", mapdetail.get("audio"));
            map.put("ReturnType", "1001");
            map.put("ContentCount", mapdetail.get("count"));
        }else{
            map.put("ReturnType", "1011");
            map.put("Message", "没有相关内容 ");
        }
        return map;
    }

    //修改排序号
    @RequestMapping(value = "/content/listquery/checkquery.do")
    @ResponseBody
    public Map<String, Object> checkQuery(HttpServletRequest request) {
        Map<String, Object> m = RequestDataUtils.getDataFromRequest(request);
        System.out.println(m);
        int flowFlag = m.get("ContentFlowFlag") == null? -1 : Integer.valueOf((String) m.get("ContentFlowFlag"));
        String userId = m.get("UserId") == null ? null : (String) m.get("UserId");
        String id = m.get("Id") == null ? null : (String) m.get("Id");
        int sort = m.get("ContentSort") == null ? -1 : Integer.valueOf((String) m.get("ContentSort"));
        System.out.println(flowFlag + "#"  + id + "#" + sort);
        Map<String, Object> map = queryService.modifSort(id, sort, flowFlag);
        return map;
    }
}
