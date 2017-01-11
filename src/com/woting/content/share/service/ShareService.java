package com.woting.content.share.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.ext.spring.redis.RedisOperService;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.cm.core.channel.persis.po.ChannelAssetPo;
import com.woting.cm.core.media.persis.po.MediaAssetPo;
import com.woting.cm.core.media.persis.po.SeqMediaAssetPo;
import com.woting.cm.core.media.service.MediaService;
import com.woting.content.publish.utils.CacheUtils;
import com.woting.content.share.utils.SHA1;

public class ShareService {
	@Resource
	private MediaService mediaService;

	public boolean getShareHtml(String resId, String mediaType) {
		if (!StringUtils.isNullOrEmptyOrSpace(mediaType) && !resId.toLowerCase().equals("null")) {
			if (!StringUtils.isNullOrEmptyOrSpace(mediaType) && !mediaType.toLowerCase().equals("null")) {
				if (mediaType.equals("SEQU")) {
					SeqMediaAssetPo sma = mediaService.getSmaInfoById(resId);
					if (sma != null) {
						List<SeqMediaAssetPo> listpo = new ArrayList<>();
						listpo.add(sma);
						List<Map<String, Object>> smam = mediaService.makeSmaListToReturn(listpo);
						if (smam != null && smam.size() > 0) {
							Map<String, Object> map = new HashMap<>();
							map.put("ContentDetail", smam.get(0));
							List<MediaAssetPo> mas = mediaService.getMaListBySmaId(resId);
							if (mas != null && mas.size() > 0) {
								Iterator<MediaAssetPo> it = mas.iterator();
								while (it.hasNext()) {
									MediaAssetPo mediaAssetPo = (MediaAssetPo) it.next();
									String resIds = "'" + mediaAssetPo.getId() + "'";
									List<ChannelAssetPo> chas = mediaService.getCHAListByAssetId(resIds, "wt_MediaAsset");
									if (chas != null && chas.size() > 0) {
										ChannelAssetPo chapo = chas.get(0);
										if (chapo.getFlowFlag() != 2)
											it.remove();
									}
								}
								map.put("SubList", mediaService.makeMaListToReturn(mas));
								CacheUtils.publishZJ(map);
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	public Map<String, Object> getWXConfigInfo(String url) {
		long timestamp = System.currentTimeMillis()/1000;
		String nonceStr = SequenceUUID.getUUIDSubSegment(4);
		Map<String, Object> retM = new HashMap<>();
		try {
			ServletContext sc=(SystemCache.getCache(FConstants.SERVLET_CONTEXT)==null?null:(ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent());
	        if (WebApplicationContextUtils.getWebApplicationContext(sc)!=null) {
	            JedisConnectionFactory conn=(JedisConnectionFactory)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("connectionFactory");
	            RedisOperService ros=new RedisOperService(conn, 5);
	            if (ros.exist("WX_JSAPI_TICKET")) {
	            	String ticket = ros.get("WX_JSAPI_TICKET");
					Map<String, Object> sha1m = new HashMap<>();
					sha1m.put("jsapi_ticket", "="+ticket+"&");
					sha1m.put("noncestr", "="+nonceStr+"&");
					sha1m.put("timestamp", "="+timestamp+"&");
					sha1m.put("url", "="+url);
					String Signature =  SHA1.SHA1(sha1m);
					retM.put("AppId", "wxd4cb3b422606e373");
					retM.put("TimeStamp", timestamp);
					retM.put("NonceStr", nonceStr);
					retM.put("Signature", Signature);
					return retM;
				}
	        }
		} catch (Exception e) {
			retM.put("ReturnType", 1011);
			retM.put("Message", "获取异常");
		}
		return null;
	}
}
