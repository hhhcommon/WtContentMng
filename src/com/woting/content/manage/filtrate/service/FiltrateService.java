package com.woting.content.manage.filtrate.service;

import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.woting.cm.core.media.service.MediaService;
import com.woting.content.manage.channel.service.ChannelContentService;

@Service
public class FiltrateService {
	@Resource
	private MediaService mediaService;
	@Resource
	private ChannelContentService channeContentlService;
	
	public Map<String, Object> getFiltrateByMediaType(String userid,String mediatype) {
		Map<String, Object> m = channeContentlService.getFiltrateByUserId(userid,mediatype);
		return m;
	}
}
