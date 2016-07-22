package com.woting.content.manage.seq.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.StringUtils;
import com.woting.cm.core.media.model.SeqMediaAsset;
import com.woting.content.common.util.RequestUtils;
import com.woting.content.manage.seq.service.SeqContentService;

@Controller
public class SeqContentController {
	@Resource
	private SeqContentService seqContentService;
	
	/**
	 * 得到主播id下的专辑列表(包括发布和未发布的)
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/seq/getHostSeqMediaList.do")
	@ResponseBody
	public Map<String, Object> getSeqMediaList(HttpServletRequest request){
		Map<String, Object> map = new HashMap<String,Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId")+"";
		if(StringUtils.isNullOrEmptyOrSpace(userid)||userid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无用户信息");
			return map;
		}
		
		String url = "{\"channelName\":\"酷狗FM--CRI环球旅游广播\",\"url\":\"http://fm.shuoba.org/channel3/1/48.m3u8\"}";
		Map<String, Object> ms = (Map<String, Object>) JsonUtils.jsonToObj(url, Map.class);
		System.out.println(ms.get("url"));
		
		Map<String, Object> c = seqContentService.getHostSeqMediaContents(userid);
		if(c!=null&&c.size()>0){
			map.put("ReturnType", c.get("ReturnType"));
			c.remove("ReturnType");
			map.put("ResultList", c);
		}else{
			map.put("ReturnType", "1011");
            map.put("Message", "没有查到任何内容");
		}
		return map;
	}
	
	/**
	 * 创建新专辑
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/seq/addSeqMediaInfo.do")
	@ResponseBody
	public Map<String, Object> addSeqMediaInfo(HttpServletRequest request){
		Map<String, Object> map = new HashMap<String,Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId")+"";
		String username = m.get("UserName")+"";
		if(userid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无用户信息");
			return map;
		}
		String smaname = m.get("ContentName")+"";
		if(smaname.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无节目名称");
			return map;
		}
		String smastatus = m.get("ContentStatus")+"";
		if(smastatus.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无资源状态");
			return map;
		}
		String smaimg = m.get("ContentImg")+"";
		smaimg = smaimg.replace("/opt/tomcat8_CM/webapps", "http://www.wotingfm.com:908").replace("D:\\workIDE\\work\\WtContentMng\\WebContent\\uploadFiles\\tempuplf\\", "http://localhost:908/CM/uploadFiles/tempuplf/");
		String smadesc = m.get("ContentDesc")+"";
		String did = m.get("ContentCatalogsId")+"";
		String chid = m.get("ContentChannelId")+"";
		List<Map<String, Object>> maList = new ArrayList<Map<String,Object>>();;
		if(!m.containsKey("MediaInfo"))maList=null;
		else maList = (List<Map<String, Object>>) m.get("MediaInfo");
		map = seqContentService.addSeqInfo(userid, username, smaname, smaimg, smastatus, did, chid, smadesc, maList);
		return map;
	}
	
	/**
	 * 修改专辑信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/seq/updateSeqMediaInfo.do")
	@ResponseBody
	public Map<String, Object> updateSeqMediaInfo(HttpServletRequest request){
		SeqMediaAsset sma = new SeqMediaAsset();
		Map<String, Object> map = new HashMap<String,Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId")+"";
		if(userid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
		    map.put("Message", "无用户信息");
		    return map;
		}
		String smaid = m.get("ContentId")+"";
		if(smaid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
		    map.put("Message", "修改失败");
		    return map;
		}
		sma.setId(smaid);
		String smaname = m.get("ContentName")+"";
		if(!smaname.toLowerCase().equals("null")) sma.setSmaTitle(smaname);
		String smaimg = m.get("ContentImg")+"";
		smaimg = smaimg.replace("/opt/tomcat8_CM/webapps", "http://www.wotingfm.com:908").replace("D:\\workIDE\\work\\WtContentMng\\WebContent\\uploadFiles\\tempuplf\\", "http://localhost:908/CM/uploadFiles/tempuplf/");
		if(!smaimg.toLowerCase().equals("null")) sma.setSmaImg(smaimg);
		String smadesc = m.get("ContentDesc")+"";
		if(!smadesc.toLowerCase().equals("null")) sma.setDescn(smadesc);
		String smastatus = m.get("ContentStatus")+"";
		if(!smastatus.toLowerCase().equals("null")) sma.setSmaStatus(Integer.valueOf(smastatus));
		String did = m.get("ContentCatalogsId")+""; // 更改专辑的内容分类
		String chid = m.get("ContentChannelId")+""; // 更改专辑的栏目
		List<Map<String, Object>> malist = (List<Map<String, Object>>) m.get("MediaInfo");
		map = seqContentService.updateSeqInfo(userid,sma,did,chid,malist);
		return map;
	}
	
	/**
	 * 发布专辑
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/seq/updateSeqMediaStatus.do")
	@ResponseBody
	public Map<String, Object> updateSeqMediaStatus(HttpServletRequest request){
		Map<String, Object> map = new HashMap<String,Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		System.out.println(JsonUtils.objToJson(m));
		String userid = m.get("UserId")+"";
		if(userid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无用户信息");
			return map;
		}
		String smaid = m.get("ContentId")+"";
		if(smaid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无专辑id信息");
			return map;
		}
		String chid = m.get("ContentChannelId")+"";
		if(chid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无栏目id信息");
			return map;
		}
		List<Map<String, Object>> medialist = (List<Map<String, Object>>) m.get("MediaInfo");
		map = seqContentService.modifySeqStatus(userid, smaid, chid, 2, medialist);
		return map;
	}
	
	/**
	 * 删除专辑
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/seq/removeSeqMediaInfo.do")
	@ResponseBody
	public Map<String, Object> removeSeqMediaInfo(HttpServletRequest request){
		Map<String, Object> map = new HashMap<String,Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId")+"";
		if(userid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无用户信息");
			return map;
		}
		String contentid = m.get("ContentId")+"";
		if(contentid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无专辑信息");
			return map;
		}
		map = seqContentService.removeSeqMediaAsset(contentid);
		return map;
	}
}
