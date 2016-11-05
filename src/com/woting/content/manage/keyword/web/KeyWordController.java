package com.woting.content.manage.keyword.web;

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
import com.woting.content.manage.keyword.service.KeyWordProService;

@Controller
public class KeyWordController {
	@Resource
	private KeyWordProService keyWordProService;
	
	/**
	 * 删除单体节目信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/getTags.do")
	@ResponseBody
	public Map<String, Object> getTags(HttpServletRequest request){
		Map<String, Object> map = new HashMap<String,Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId")+"";
		if(StringUtils.isNullOrEmptyOrSpace(userid)||userid.toLowerCase().equals("null")){
			map.put("ReturnType", "1011");
			map.put("Message", "无用户信息");
			return map;
		}
		String mediatype = m.get("MediaType")+"";
		if(StringUtils.isNullOrEmptyOrSpace(mediatype)||mediatype.toLowerCase().equals("null")){
			map.put("ReturnType", "1012");
			map.put("Message", "无内容类型信息");
			return map;
		}
		String tagType = m.get("TagType")+"";
		if(StringUtils.isNullOrEmptyOrSpace(tagType)||tagType.toLowerCase().equals("null")){
			tagType = "1";
		}
		String tagsize = m.get("TagSize")+"";
		if(StringUtils.isNullOrEmptyOrSpace(tagsize)||tagsize.toLowerCase().equals("null")){
			tagsize = "10";
		}
		List<Map<String, Object>> ls = new ArrayList<>();
		if (mediatype.equals("1")) {
			String channelId = m.get("ChannelIds")+"";
			if(StringUtils.isNullOrEmptyOrSpace(channelId)||channelId.toLowerCase().equals("null")){
				map.put("ReturnType", "1014");
				map.put("Message", "无栏目Id");
				return map;
			}
			ls = keyWordProService.getKeyWordList(tagType, userid, channelId, tagsize);
		}
		if (mediatype.equals("2")) {
			String seqMediaId = m.get("SeqMediaId")+"";
		    if(StringUtils.isNullOrEmptyOrSpace(seqMediaId)||seqMediaId.toLowerCase().equals("null")){
			    map.put("ReturnType", "1015");
			    map.put("Message", "无专辑Id");
			    return map;
		    }
		    ls = keyWordProService.getKeyWordListBySeqMedia(seqMediaId, tagType, userid, tagsize);
		}
		if (ls!=null && ls.size()>0) {
			map.put("ReturnType", "1001");
			map.put("ResultList", ls);
			map.put("AllCount", ls.size());
			return map;
		} else {
			map.put("ReturnType", "1016");
			map.put("Message", "无内容");
			return map;
		}
	}
	
}
