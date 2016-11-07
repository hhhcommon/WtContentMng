package com.woting.content.manage.media.web;

import java.sql.Timestamp;
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
import com.woting.cm.core.media.model.MediaAsset;
import com.woting.cm.core.media.model.SeqMediaAsset;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.RequestUtils;
import com.woting.content.manage.media.service.MediaContentService;
import com.woting.dataanal.gather.API.ApiGatherUtils;
import com.woting.dataanal.gather.API.mem.ApiGatherMemory;
import com.woting.dataanal.gather.API.persis.pojo.ApiLogPo;
import com.woting.passport.mobile.MobileParam;
import com.woting.passport.mobile.MobileUDKey;
import com.woting.passport.session.DeviceType;
import com.woting.passport.session.SessionService;

@Controller
public class MediaContentController {
	@Resource
	private MediaContentService mediaContentService;
	@Resource(name = "redisSessionService")
	private SessionService sessionService;
	private static String ip_address = "www.wotingfm.com";

	/**
	 * 得到主播单体节目列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/media/getMediaList.do")
	@ResponseBody
	public Map<String, Object> getMediaList(HttpServletRequest request) {
		// 数据收集处理==1
		ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
		alPo.setApiName("1.1.1-common/entryApp");
		alPo.setObjType("000");// 一般信息
		alPo.setDealFlag(1);// 处理成功

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 0-获取参数
			MobileUDKey mUdk = null;
			Map<String, Object> m = RequestUtils.getDataFromRequest(request);
			alPo.setReqParam(JsonUtils.objToJson(m));
			if (m == null || m.size() == 0) {
				map.put("ReturnType", "0000");
				map.put("Message", "无法获取需要的参数");
			} else {
				MobileParam mp = MobileParam.build(m);
				if (StringUtils.isNullOrEmptyOrSpace(mp.getImei())
						&& DeviceType.buildDtByPCDType(StringUtils.isNullOrEmptyOrSpace(mp.getPCDType()) ? -1
								: Integer.parseInt(mp.getPCDType())) == DeviceType.PC) { // 是PC端来的请求
					mp.setImei(request.getSession().getId());
				}
				mUdk = mp.getUserDeviceKey();
				if (mUdk != null) {
					Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "common/entryApp");
					map.putAll(retM);
				}
				map.put("ServerStatus", "1"); // 服务器状态
			}
			// 数据收集处理==2
			alPo.setOwnerType(201);
			if (map.get("UserId") != null && !StringUtils.isNullOrEmptyOrSpace(map.get("UserId") + "")) {
				alPo.setOwnerId(map.get("UserId") + "");
			} else {
				// 过客
				if (mUdk != null)
					alPo.setOwnerId(mUdk.getDeviceId());
				else
					alPo.setOwnerId("0");
			}
			if (mUdk != null) {
				alPo.setDeviceType(mUdk.getPCDType());
				alPo.setDeviceId(mUdk.getDeviceId());
			}
			if (m != null) {
				if (mUdk != null && DeviceType.buildDtByPCDType(mUdk.getPCDType()) == DeviceType.PC) {
					if (m.get("MobileClass") != null && !StringUtils.isNullOrEmptyOrSpace(m.get("MobileClass") + "")) {
						alPo.setExploreVer(m.get("MobileClass") + "");
					}
					if (m.get("exploreName") != null && !StringUtils.isNullOrEmptyOrSpace(m.get("exploreName") + "")) {
						alPo.setExploreName(m.get("exploreName") + "");
					}
				} else {
					if (m.get("MobileClass") != null && !StringUtils.isNullOrEmptyOrSpace(m.get("MobileClass") + "")) {
						alPo.setDeviceClass(m.get("MobileClass") + "");
					}
				}
			}
			if (map.get("ReturnType") != null)
				return map;
			String userid = m.get("UserId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(userid) || userid.toLowerCase().equals("null")) {
				map.put("ReturnType", "1011");
				map.put("Message", "无用户信息");
				return map;
			}
			String flagflow = m.get("FlagFlow") + "";
			if (StringUtils.isNullOrEmptyOrSpace(flagflow) || flagflow.toLowerCase().equals("null")) {
				flagflow = "0";
			}
			String channelid = m.get("ChannelId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(channelid) || channelid.toLowerCase().equals("null")) {
				channelid = "0";
			}
			String seqmediaid = m.get("SeqMediaId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(seqmediaid) || seqmediaid.toLowerCase().equals("null")) {
				seqmediaid = "0";
			}
			Map<String, Object> c = mediaContentService.getMediaContents(userid, flagflow, channelid, seqmediaid);
			if (c != null && c.size() > 0) {
				map.put("ReturnType", c.get("ReturnType"));
				c.remove("ReturnType");
				map.put("ResultList", c);
			} else {
				map.put("ReturnType", "1011");
				map.put("Message", "没有查到任何内容");
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			map.put("ReturnType", "T");
			map.put("TClass", e.getClass().getName());
			map.put("Message", StringUtils.getAllMessage(e));
			alPo.setDealFlag(2);
			return map;
		} finally {
			// 数据收集处理=3
			alPo.setEndTime(new Timestamp(System.currentTimeMillis()));
			alPo.setReturnData(JsonUtils.objToJson(map));
			try {
				ApiGatherMemory.getInstance().put2Queue(alPo);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * 创建单体节目
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/content/media/addMediaInfo.do")
	@ResponseBody
	public Map<String, Object> addMediaInfo(HttpServletRequest request) {
		// 数据收集处理==1
		ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
		alPo.setApiName("1.1.1-common/entryApp");
		alPo.setObjType("000");// 一般信息
		alPo.setDealFlag(1);// 处理成功

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 0-获取参数
			MobileUDKey mUdk = null;
			Map<String, Object> m = RequestUtils.getDataFromRequest(request);
			alPo.setReqParam(JsonUtils.objToJson(m));
			if (m == null || m.size() == 0) {
				map.put("ReturnType", "0000");
				map.put("Message", "无法获取需要的参数");
			} else {
				MobileParam mp = MobileParam.build(m);
				if (StringUtils.isNullOrEmptyOrSpace(mp.getImei())
						&& DeviceType.buildDtByPCDType(StringUtils.isNullOrEmptyOrSpace(mp.getPCDType()) ? -1
								: Integer.parseInt(mp.getPCDType())) == DeviceType.PC) { // 是PC端来的请求
					mp.setImei(request.getSession().getId());
				}
				mUdk = mp.getUserDeviceKey();
				if (mUdk != null) {
					Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "common/entryApp");
					map.putAll(retM);
				}
				map.put("ServerStatus", "1"); // 服务器状态
			}
			// 数据收集处理==2
			alPo.setOwnerType(201);
			if (map.get("UserId") != null && !StringUtils.isNullOrEmptyOrSpace(map.get("UserId") + "")) {
				alPo.setOwnerId(map.get("UserId") + "");
			} else {
				// 过客
				if (mUdk != null)
					alPo.setOwnerId(mUdk.getDeviceId());
				else
					alPo.setOwnerId("0");
			}
			if (mUdk != null) {
				alPo.setDeviceType(mUdk.getPCDType());
				alPo.setDeviceId(mUdk.getDeviceId());
			}
			if (m != null) {
				if (mUdk != null && DeviceType.buildDtByPCDType(mUdk.getPCDType()) == DeviceType.PC) {
					if (m.get("MobileClass") != null && !StringUtils.isNullOrEmptyOrSpace(m.get("MobileClass") + "")) {
						alPo.setExploreVer(m.get("MobileClass") + "");
					}
					if (m.get("exploreName") != null && !StringUtils.isNullOrEmptyOrSpace(m.get("exploreName") + "")) {
						alPo.setExploreName(m.get("exploreName") + "");
					}
				} else {
					if (m.get("MobileClass") != null && !StringUtils.isNullOrEmptyOrSpace(m.get("MobileClass") + "")) {
						alPo.setDeviceClass(m.get("MobileClass") + "");
					}
				}
			}
			if (map.get("ReturnType") != null) return map;
			String userid = m.get("UserId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(userid) || userid.toLowerCase().equals("null")) {
				map.put("ReturnType", "1011");
				map.put("Message", "无用户信息");
				return map;
			}
			String contentname = m.get("ContentName") + "";
			if (StringUtils.isNullOrEmptyOrSpace(contentname) || contentname.toLowerCase().equals("null")) {
				map.put("ReturnType", "1012");
				map.put("Message", "无节目名称");
				return map;
			}
			String seqmediaId = m.get("SeqMediaId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(seqmediaId) || seqmediaId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1013");
				map.put("Message", "无专辑Id");
				return map;
			}
			String rootpath = SystemCache.getCache(FConstants.APPOSPATH).getContent() + "";
			String contentimg = m.get("ContentImg")+"";
			if (contentimg.toLowerCase().equals("null"))
				contentimg = "htpp://www.wotingfm.com:908/CM/mweb/templet/zj_templet/imgs/default.png";
			contentimg = contentimg.replace(rootpath, "http://" + ip_address + ":908/CM/");
			String contenturi = m.get("ContentURI") + "";
			if (StringUtils.isNullOrEmptyOrSpace(contenturi) || contenturi.toLowerCase().equals("null")) {
				map.put("ReturnType", "1014");
				map.put("Message", "无播放资源");
				return map;
			}
			contenturi = contenturi.replace(rootpath, "http://" + ip_address + ":908/CM/");
			List<Map<String, Object>> tags = (List<Map<String, Object>>) m.get("TagList");
			List<Map<String, Object>> membertypes = (List<Map<String, Object>>) m.get("MemberType");
			String contentdesc = m.get("ContentDesc") + "";
			String pubTime = m.get("FixedPubTime")+"";
			map = mediaContentService.addMediaAssetInfo(userid, contentname, contentimg, seqmediaId, contenturi, tags, membertypes, contentdesc, pubTime);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			map.put("ReturnType", "T");
			map.put("TClass", e.getClass().getName());
			map.put("Message", StringUtils.getAllMessage(e));
			alPo.setDealFlag(2);
			return map;
		} finally {
			// 数据收集处理=3
			alPo.setEndTime(new Timestamp(System.currentTimeMillis()));
			alPo.setReturnData(JsonUtils.objToJson(map));
			try {
				ApiGatherMemory.getInstance().put2Queue(alPo);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * 修改单体节目信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/media/updateMediaInfo.do")
	@ResponseBody
	public Map<String, Object> updateMediaInfo(HttpServletRequest request) {
		MediaAsset ma = new MediaAsset();
		SeqMediaAsset sma = new SeqMediaAsset();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String maid = m.get("ContentId") + "";
		ma.setId(maid);
		String maname = m.get("ContentName") + "";
		if (!maname.toLowerCase().equals("null"))
			ma.setMaTitle(maname);
		String rootpath = SystemCache.getCache(FConstants.APPOSPATH).getContent() + "";
		String maimg = m.get("ContentImg") + "";
		if (maimg.equals("null"))
			maimg = "htpp://www.wotingfm.com:908/CM/mweb/templet/zj_templet/imgs/default.png";
		maimg = maimg.replace(rootpath, "http://" + ip_address + ":908/CM/");
		if (!maimg.toLowerCase().equals("null"))
			ma.setMaImg(maimg);
		String mauri = m.get("ContentURI") + "";
		if (!mauri.toLowerCase().equals("null")) {
			mauri = mauri.replace(rootpath, "http://" + ip_address + ":908/CM/");
			ma.setMaURL(mauri);
		}
		String seqid = m.get("ContentSeqId") + "";
		if (!seqid.toLowerCase().equals("null"))
			sma.setId(seqid);
		String madesc = m.get("ContentDesc") + "";
		if (!madesc.toLowerCase().equals("null"))
			ma.setDescn(madesc);
		String mastatus = m.get("ContentStatus") + "";
		if (!mastatus.toLowerCase().equals("null"))
			ma.setMaStatus(Integer.valueOf(mastatus));
		if (seqid.toLowerCase().equals("null"))
			map = mediaContentService.updateMediaInfo(ma, null);
		else
			map = mediaContentService.updateMediaInfo(ma, sma);
		return map;
	}

	/**
	 * 发布单体节目(发布单体节目时，必须要绑定在专辑的下面)
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/media/updateMediaStatus.do")
	@ResponseBody
	public Map<String, Object> updateMediaStatus(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId") + "";
		if (userid.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无用户信息");
			return map;
		}
		String maid = m.get("ContentId") + "";
		if (maid.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无节目id信息");
			return map;
		}
		String smaid = m.get("ContentSeqId") + "";
		if (smaid.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无专辑id信息");
			return map;
		}
		map = mediaContentService.modifyMediaStatus(userid, maid, smaid, 2);
		return map;
	}

	/**
	 * 删除单体节目信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/media/removeMediaInfo.do")
	@ResponseBody
	public Map<String, Object> removeMediaInfo(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId") + "";
		if (StringUtils.isNullOrEmptyOrSpace(userid) || userid.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无用户信息");
			return map;
		}
		String contentid = m.get("ContentId") + "";
		if (contentid.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无专辑信息");
			return map;
		}
		map = mediaContentService.removeMediaAsset(contentid);
		return map;
	}
}
