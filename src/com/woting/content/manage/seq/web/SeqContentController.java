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
		String smaimg = m.get("ContentImg")+"";
		smaimg = smaimg.replace("/opt/tomcat8_CM/webapps", "http://www.wotingfm.com:908");
		String smadesc = m.get("ContentDesc")+"";
		String did = m.get("ContentCatalogsId")+"";
		List<Map<String, Object>> maList = new ArrayList<Map<String,Object>>();;
		if(!m.containsKey("AddMediaInfo"))maList=null;
		else maList = (List<Map<String, Object>>) m.get("AddMediaInfo");
		map = seqContentService.addSeqInfo(userid, username, smaname, smaimg, did, smadesc, maList);
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
		smaimg = smaimg.replace("/opt/tomcat8_CM/webapps", "http://www.wotingfm.com:908");
		if(!smaimg.toLowerCase().equals("null")) sma.setSmaImg(smaimg);
		String smadesc = m.get("ContentDesc")+"";
		if(!smadesc.toLowerCase().equals("null")) sma.setDescn(smadesc);
		String did = m.get("ContentCatalogsId")+""; //更改专辑的内容分类
//		String subjectwords = m.get("SubjectWords")+"";
//		if(subjectwords.toLowerCase().equals("null")) mauri=null;
//		String keywords = m.get("KeyWords")+"";
//		if(keywords.toLowerCase().equals("null")) mauri=null;
		map = seqContentService.updateSeqInfo(sma,did);
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
		String smaname = m.get("ContentName")+"";
		String smaimg = m.get("ContentImg")+"";
		String desc = m.get("ContentDesc")+"";
		map = seqContentService.modifySeqStatus(userid, smaid, smaname, chid, desc, smaimg);
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
