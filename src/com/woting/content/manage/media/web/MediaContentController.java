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
		alPo.setApiName("5.2.1--/content/media/getMediaList.do");
		alPo.setObjType("005");// 用户组对象
		alPo.setDealFlag(1);// 处理成功
		alPo.setOwnerType(201);
		alPo.setOwnerId("--");

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 0-获取参数
			String userId = "";
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
					Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/media/getMediaList");
					if ((retM.get("ReturnType") + "").equals("2003")) {
						map.put("ReturnType", "200");
						map.put("Message", "需要登录");
					} else {
						map.putAll(retM);
						if ((retM.get("ReturnType") + "").equals("1001"))
							map.remove("ReturnType");
					}
					userId = retM.get("UserId") == null ? null : retM.get("UserId") + "";
				} else {
					map.put("ReturnType", "0000");
					map.put("Message", "无法获取需要的参数");
				}
			}
			// 数据收集处理==2
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

			// 数据采集
			userId = m.get("UserId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(userId) || userId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1011");
				map.put("Message", "无用户信息");
				return map;
			}
			String flowflag = m.get("FlowFlag") + "";
			if (StringUtils.isNullOrEmptyOrSpace(flowflag) || flowflag.toLowerCase().equals("null")) {
				flowflag = "0";
			}
			String channelid = m.get("ChannelId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(channelid) || channelid.toLowerCase().equals("null")) {
				channelid = "0";
			}
			String seqmediaid = m.get("SeqMediaId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(seqmediaid) || seqmediaid.toLowerCase().equals("null")) {
				seqmediaid = "0";
			}
			Map<String, Object> c = mediaContentService.getMediaContents(userId, flowflag, channelid, seqmediaid);
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
				e.printStackTrace();
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
		alPo.setApiName("5.2.2--/content/media/addMediaInfo.do");
		alPo.setObjType("005");// 用户组对象
		alPo.setDealFlag(1);// 处理成功
		alPo.setOwnerType(201);
		alPo.setOwnerId("--");

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 0-获取参数
			String userId = "";
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
					Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/media/addMediaInfo");
					if ((retM.get("ReturnType") + "").equals("2003")) {
						map.put("ReturnType", "200");
						map.put("Message", "需要登录");
					} else {
						map.putAll(retM);
						if ((retM.get("ReturnType") + "").equals("1001"))
							map.remove("ReturnType");
					}
					userId = retM.get("UserId") == null ? null : retM.get("UserId") + "";
				} else {
					map.put("ReturnType", "0000");
					map.put("Message", "无法获取需要的参数");
				}
			}
			// 数据收集处理==2
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

			// 数据采集
			userId = m.get("UserId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(userId) || userId.toLowerCase().equals("null")) {
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
			String timelong = m.get("TimeLong") + "";
			if (StringUtils.isNullOrEmptyOrSpace(timelong) || timelong.toLowerCase().equals("null")) {
				map.put("ReturnType", "1013");
				map.put("Message", "无节目时长");
				return map;
			}
			String seqmediaId = m.get("SeqMediaId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(seqmediaId) || seqmediaId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1014");
				map.put("Message", "无专辑Id");
				return map;
			}
			String rootpath = SystemCache.getCache(FConstants.APPOSPATH).getContent() + "";
			String contentimg = m.get("ContentImg") + "";
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
			String pubTime = m.get("FixedPubTime") + "";
			String flowFlag = m.get("FlowFlag") + "";
			if (StringUtils.isNullOrEmptyOrSpace(flowFlag) || flowFlag.toLowerCase().equals("null")) {
				flowFlag = "1";
			}
			map = mediaContentService.addMediaAssetInfo(userId, contentname, contentimg, seqmediaId, timelong, contenturi, tags,
					membertypes, contentdesc, pubTime, flowFlag);
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
				e.printStackTrace();
			}
		}
	}

	/**
	 * 修改单体节目信息
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/content/media/updateMediaInfo.do")
	@ResponseBody
	public Map<String, Object> updateMediaInfo(HttpServletRequest request) {
		// 数据收集处理==1
		ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
		alPo.setApiName("5.2.3--/content/media/updateMediaInfo.do");
		alPo.setObjType("005");// 用户组对象
		alPo.setDealFlag(1);// 处理成功
		alPo.setOwnerType(201);
		alPo.setOwnerId("--");

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 0-获取参数
			String userId = "";
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
					Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/media/updateMediaInfo");
					if ((retM.get("ReturnType") + "").equals("2003")) {
						map.put("ReturnType", "200");
						map.put("Message", "需要登录");
					} else {
						map.putAll(retM);
						if ((retM.get("ReturnType") + "").equals("1001"))
							map.remove("ReturnType");
					}
					userId = retM.get("UserId") == null ? null : retM.get("UserId") + "";
				} else {
					map.put("ReturnType", "0000");
					map.put("Message", "无法获取需要的参数");
				}
			}
			// 数据收集处理==2
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

			// 数据采集
			userId = m.get("UserId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(userId) || userId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1011");
				map.put("Message", "无用户信息");
				return map;
			}
			String contentId = m.get("ContentId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(contentId) || contentId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1012");
				map.put("Message", "无节目Id");
				return map;
			}
			String contentname = m.get("ContentName") + "";
			if (StringUtils.isNullOrEmptyOrSpace(contentname) || contentname.toLowerCase().equals("null")) {
				map.put("ReturnType", "1013");
				map.put("Message", "无节目名称");
				return map;
			}
			String seqmediaId = m.get("SeqMediaId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(seqmediaId) || seqmediaId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1014");
				map.put("Message", "无专辑Id");
				return map;
			}
			String contenturi = m.get("ContentURI") + "";
			if (StringUtils.isNullOrEmptyOrSpace(contenturi) || contenturi.toLowerCase().equals("null")) {
				map.put("ReturnType", "1014");
				map.put("Message", "无播放资源");
				return map;
			}
			String rootpath = SystemCache.getCache(FConstants.APPOSPATH).getContent() + "";
			String contentimg = m.get("ContentImg") + "";
			if (contentimg.toLowerCase().equals("null"))
				contentimg = "htpp://www.wotingfm.com:908/CM/mweb/templet/zj_templet/imgs/default.png";
			contentimg = contentimg.replace(rootpath, "http://" + ip_address + ":908/CM/");
			contenturi = contenturi.replace(rootpath, "http://" + ip_address + ":908/CM/");
			List<Map<String, Object>> tags = (List<Map<String, Object>>) m.get("TagList");
			List<Map<String, Object>> membertypes = (List<Map<String, Object>>) m.get("MemberType");
			String contentdesc = m.get("ContentDesc") + "";
			String pubTime = m.get("FixedPubTime") + "";
			boolean isok = mediaContentService.updateMediaInfo(userId, contentId, contentname, contentimg, seqmediaId,
					contenturi, tags, membertypes, contentdesc, pubTime);
			if (isok) {
				map.put("ReturnType", "1001");
				map.put("Message", "修改成功");
				return map;
			} else {
				map.put("ReturnType", "1011");
				map.put("Message", "修改失败");
				return map;
			}
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
				e.printStackTrace();
			}
		}
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
		// 数据收集处理==1
		ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
		alPo.setApiName("5.2.4--/content/media/updateMediaStatus.do");
		alPo.setObjType("005");// 用户组对象
		alPo.setDealFlag(1);// 处理成功
		alPo.setOwnerType(201);
		alPo.setOwnerId("--");

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 0-获取参数
			String userId = "";
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
					Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/media/updateMediaStatus");
					if ((retM.get("ReturnType") + "").equals("2003")) {
						map.put("ReturnType", "200");
						map.put("Message", "需要登录");
					} else {
						map.putAll(retM);
						if ((retM.get("ReturnType") + "").equals("1001"))
							map.remove("ReturnType");
					}
					userId = retM.get("UserId") == null ? null : retM.get("UserId") + "";
				} else {
					map.put("ReturnType", "0000");
					map.put("Message", "无法获取需要的参数");
				}
			}
			// 数据收集处理==2
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

			// 数据采集
			userId = m.get("UserId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(userId) || userId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1011");
				map.put("Message", "无用户信息");
				return map;
			}
			String contentId = m.get("ContentId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(contentId) || contentId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1011");
				map.put("Message", "无节目id信息");
				return map;
			}
			String seqMediaId = m.get("SeqMediaId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(seqMediaId) || seqMediaId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1011");
				map.put("Message", "无专辑id信息");
				return map;
			}
			boolean isok = mediaContentService.modifyMediaStatus(userId, contentId, seqMediaId, 2);
			if (isok) {
				map.put("ReturnType", "1001");
				map.put("Message", "修改成功");
				return map;
			} else {
				map.put("ReturnType", "1011");
				map.put("Message", "修改失败");
				return map;
			}
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
				e.printStackTrace();
			}
		}
	}

	/**
	 * 删除单体节目信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/media/removeMedia.do")
	@ResponseBody
	public Map<String, Object> removeMediaInfo(HttpServletRequest request) {
		// 数据收集处理==1
		ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
		alPo.setApiName("5.2.5--/content/media/removeMedia.do");
		alPo.setObjType("005");// 用户组对象
		alPo.setDealFlag(1);// 处理成功
		alPo.setOwnerType(201);
		alPo.setOwnerId("--");

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 0-获取参数
			String userId = "";
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
					Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/media/removeMedia");
					if ((retM.get("ReturnType") + "").equals("2003")) {
						map.put("ReturnType", "200");
						map.put("Message", "需要登录");
					} else {
						map.putAll(retM);
						if ((retM.get("ReturnType") + "").equals("1001"))
							map.remove("ReturnType");
					}
					userId = retM.get("UserId") == null ? null : retM.get("UserId") + "";
				} else {
					map.put("ReturnType", "0000");
					map.put("Message", "无法获取需要的参数");
				}
			}
			// 数据收集处理==2
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

			// 数据采集
			userId = m.get("UserId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(userId) || userId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1011");
				map.put("Message", "无用户信息");
				return map;
			}
			String contentid = m.get("ContentId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(contentid) || contentid.toLowerCase().equals("null")) {
				map.put("ReturnType", "1011");
				map.put("Message", "无专辑信息");
				return map;
			}
			map = mediaContentService.removeMediaAsset(contentid);
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
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取单体节目信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/media/getMediaInfo.do")
	@ResponseBody
	public Map<String, Object> getMediaInfo(HttpServletRequest request) {
		// 数据收集处理==1
		ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
		alPo.setApiName("5.2.6--/content/media/getMediaInfo.do");
		alPo.setObjType("005");// 用户组对象
		alPo.setDealFlag(1);// 处理成功
		alPo.setOwnerType(201);
		alPo.setOwnerId("--");

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 0-获取参数
			String userId = "";
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
					Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/media/getMediaInfo");
					if ((retM.get("ReturnType") + "").equals("2003")) {
						map.put("ReturnType", "200");
						map.put("Message", "需要登录");
					} else {
						map.putAll(retM);
						if ((retM.get("ReturnType") + "").equals("1001"))
							map.remove("ReturnType");
					}
					userId = retM.get("UserId") == null ? null : retM.get("UserId") + "";
				} else {
					map.put("ReturnType", "0000");
					map.put("Message", "无法获取需要的参数");
				}
			}
			// 数据收集处理==2
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
			
			//数据采集
			userId = m.get("UserId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(userId) || userId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1011");
				map.put("Message", "无用户信息");
				return map;
			}
			String contentid = m.get("ContentId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(contentid) || contentid.toLowerCase().equals("null")) {
				map.put("ReturnType", "1011");
				map.put("Message", "无专辑信息");
				return map;
			}
			Map<String, Object> rem = mediaContentService.getMediaAssetInfo(userId, contentid);
			if (rem != null) {
				map.put("ReturnType", "1001");
				map.put("Result", rem);
				return map;
			} else {
				map.put("ReturnType", "1012");
				map.put("Message", "获取信息失败");
				return map;
			}
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
				e.printStackTrace();
			}
		}
	}
}
