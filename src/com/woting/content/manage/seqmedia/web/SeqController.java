package com.woting.content.manage.seqmedia.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.StringUtils;
import com.woting.cm.core.media.model.SeqMediaAsset;
import com.woting.content.manage.seqmedia.service.SeqContentService;
import com.spiritdata.framework.util.RequestUtils;

@Controller
public class SeqController {
	@Resource
	private SeqContentService seqContentService;
	private static String ip_address = "182.92.175.134";

	/**
	 * 得到主播id下的专辑列表(包括发布和未发布的)
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/seq/getSeqMediaList.do")
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
		String flagflow = m.get("FlagFlow")+"";
		if(StringUtils.isNullOrEmptyOrSpace(flagflow)||flagflow.toLowerCase().equals("null")){
			flagflow = "0";
		}
		String channelid = m.get("ChannelId")+"";
		if(StringUtils.isNullOrEmptyOrSpace(channelid)||channelid.toLowerCase().equals("null")){
			channelid = "0";
		}
		String shortsearch = m.get("ShortSearch")+"";
		if(StringUtils.isNullOrEmptyOrSpace(shortsearch)||shortsearch.toLowerCase().equals("null")){
			shortsearch = "false";
		}
		List<Map<String, Object>> c = seqContentService.getHostSeqMediaContents(userid,flagflow,channelid,shortsearch);
		if(c!=null&&c.size()>0){
			map.put("ReturnType", "1001");
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
	@SuppressWarnings("unchecked")
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
		String tags = m.get("ContentTags")+"";
		List<String> tagslist = new ArrayList<String>();
		if(!tags.equals("null")){
			String[] tagid = tags.split(",");
			for (String str : tagid) {
				tagslist.add(str);
			}
		}
		String rootpath = SystemCache.getCache(FConstants.APPOSPATH).getContent()+"";
		String smaimg = m.get("ContentImg")+"";
		if(smaimg.equals("null"))
			smaimg = "htpp://www.wotingfm.com:908/CM/mweb/templet/zj_templet/imgs/default.png";
		smaimg = smaimg.replace(rootpath, "http://"+ip_address+":908/CM/");
		String smadesc = m.get("ContentDesc")+"";
		String did = m.get("ContentCatalogsId")+"";
		String chid = m.get("ContentChannelId")+"";
		map = seqContentService.addSeqInfo(userid, username, smaname, smaimg, smastatus, did, chid, smadesc, tagslist);
		return map;
	}
	
	/**
	 * 修改专辑信息
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
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
		String rootpath = SystemCache.getCache(FConstants.APPOSPATH).getContent()+"";
		String smaimg = m.get("ContentImg")+"";
		smaimg = smaimg.replace(rootpath, "http://"+ip_address+":908/CM/");
		if(!smaimg.toLowerCase().equals("null")) sma.setSmaImg(smaimg);
		String smadesc = m.get("ContentDesc")+"";
		if(!smadesc.toLowerCase().equals("null")) sma.setDescn(smadesc);
		String smastatus = m.get("ContentStatus")+"";
		if(!smastatus.toLowerCase().equals("null")) sma.setSmaStatus(Integer.valueOf(smastatus));
		String did = m.get("ContentCatalogsId")+""; // 更改专辑的内容分类
		String chid = m.get("ContentChannelId")+""; // 更改专辑的栏目
		map = seqContentService.updateSeqInfo(userid,sma,did,chid);
		return map;
	}
	
	/**
	 * 发布专辑
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
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
		String subcount = m.get("SubCount")+"";
		if(subcount.equals("null") || (!subcount.equals("null") && Integer.valueOf(subcount)==0)){
			map.put("ReturnType","1011");
			map.put("Message","专辑无下级单体");
			return map;
		}
		map = seqContentService.modifySeqStatus(userid, smaid, chid, 2);
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
