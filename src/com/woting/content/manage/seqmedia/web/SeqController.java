package com.woting.content.manage.seqmedia.web;

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
import com.woting.content.manage.seqmedia.service.SeqContentService;
import com.woting.dataanal.gather.API.ApiGatherUtils;
import com.woting.dataanal.gather.API.mem.ApiGatherMemory;
import com.woting.dataanal.gather.API.persis.pojo.ApiLogPo;
import com.woting.passport.mobile.MobileParam;
import com.woting.passport.mobile.MobileUDKey;
import com.woting.passport.session.DeviceType;
import com.woting.passport.session.SessionService;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.RequestUtils;

@Controller
public class SeqController {
	@Resource
	private SeqContentService seqContentService;
	@Resource(name = "redisSessionService")
	private SessionService sessionService;
	private static String ip_address = "www.wotingfm.com";

	/**
	 * 得到主播id下的专辑列表(包括发布和未发布的)
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/seq/getSeqMediaList.do")
	@ResponseBody
	public Map<String, Object> getSeqMediaList(HttpServletRequest request) {
		// 数据收集处理==1
		ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
		alPo.setApiName("5.3.1--/content/seq/getSeqMediaList.do");
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
					Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/seq/getSeqMediaList");
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
			String shortsearch = m.get("ShortSearch") + "";
			if (StringUtils.isNullOrEmptyOrSpace(shortsearch) || shortsearch.toLowerCase().equals("null")) {
				shortsearch = "false";
			}
			//得到每页记录数
	        int pageSize=10;
	        try {pageSize=Integer.parseInt(m.get("PageSize")+"");} catch(Exception e) {};
	        //得到当前页数
	        int page=1;
	        try {page=Integer.parseInt(m.get("Page")+"");} catch(Exception e) {};
			Map<String, Object> c = seqContentService.getHostSeqMediaContents(userId, flowflag, channelid, shortsearch, page, pageSize);
			if (c != null && c.size() > 0) {
				map.put("ReturnType", "1001");
				c.remove("ReturnType");
				map.put("ResultList", c.get("List"));
				if (c.containsKey("AllCount")) {
					map.put("AllCount", c.get("AllCount"));
				}
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
	 * 创建新专辑
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/content/seq/addSeqMediaInfo.do")
	@ResponseBody
	public Map<String, Object> addSeqMediaInfo(HttpServletRequest request) {
		// 数据收集处理==1
		ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
		alPo.setApiName("5.3.2--/content/seq/addSeqMediaInfo.do");
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
					Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/seq/addSeqMediaInfo");
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
				map.put("ReturnType", "1011");
				map.put("Message", "无节目名称");
				return map;
			}
			String channelId = m.get("ChannelId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(channelId) || channelId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1011");
				map.put("Message", "无栏目信息");
				return map;
			}
			List<Map<String, Object>> tags = (List<Map<String, Object>>) m.get("TagList");
			List<Map<String, Object>> memberType = (List<Map<String, Object>>) m.get("MemberType");
			String rootpath = SystemCache.getCache(FConstants.APPOSPATH).getContent() + "";
			String contentimg = m.get("ContentImg") + "";
			if (contentimg.equals("null"))
				contentimg = "htpp://www.wotingfm.com:908/CM/mweb/templet/zj_templet/imgs/default.png";
			contentimg = contentimg.replace(rootpath, "http://" + ip_address + ":908/CM/");
			String contentdesc = m.get("ContentDesc") + "";
			String pubTime = m.get("FixedPubTime") + "";
			map = seqContentService.addSeqMediaInfo(null,userId, contentname, channelId, contentimg, tags, memberType, contentdesc, pubTime);
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
	 * 修改专辑信息
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/content/seq/updateSeqMediaInfo.do")
	@ResponseBody
	public Map<String, Object> updateSeqMediaInfo(HttpServletRequest request) {
		// 数据收集处理==1
		ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
		alPo.setApiName("5.3.3--/content/seq/updateSeqMediaInfo.do");
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
                    mUdk=MobileParam.build(m).getUserDeviceKey();
                    if (StringUtils.isNullOrEmptyOrSpace(mUdk.getDeviceId())) { //是PC端来的请求
                        mUdk.setDeviceId(request.getSession().getId());
                    }
					Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/seq/updateSeqMediaInfo");
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
				map.put("ReturnType", "1012");
				map.put("Message", "无专辑Id");
				return map;
			}
			String contentname = m.get("ContentName") + "";
			if (StringUtils.isNullOrEmptyOrSpace(contentname) || contentname.toLowerCase().equals("null")) {
				map.put("ReturnType", "1013");
				map.put("Message", "无节目名称");
				return map;
			}
			String channelId = m.get("ChannelId") + "";
			List<Map<String, Object>> tags = (List<Map<String, Object>>) m.get("TagList");
			List<Map<String, Object>> memberType = (List<Map<String, Object>>) m.get("MemberType");
			String rootpath = SystemCache.getCache(FConstants.APPOSPATH).getContent() + "";
			String contentimg = m.get("ContentImg") + "";
			if (contentimg.equals("null"))
				contentimg = "htpp://www.wotingfm.com:908/CM/mweb/templet/zj_templet/imgs/default.png";
			contentimg = contentimg.replace(rootpath, "http://" + ip_address + ":908/CM/");
			String contentdesc = m.get("ContentDesc") + "";
			String pubTime = m.get("FixedPubTime") + "";
			map = seqContentService.updateSeqInfo(userId, contentid, contentname, channelId, contentimg, tags,
					memberType, contentdesc, pubTime);
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
	 * 发布专辑
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/content/seq/updateSeqMediaStatus.do")
	@ResponseBody
	public Map<String, Object> updateSeqMediaStatus(HttpServletRequest request) {
		// 数据收集处理==1
		ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
		alPo.setApiName("5.3.4--/content/seq/updateSeqMediaStatus.do");
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
                    mUdk=MobileParam.build(m).getUserDeviceKey();
                    if (StringUtils.isNullOrEmptyOrSpace(mUdk.getDeviceId())) { //是PC端来的请求
                        mUdk.setDeviceId(request.getSession().getId());
                    }
					Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/seq/updateSeqMediaStatus");
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
			List<Map<String, Object>> updateList = (List<Map<String, Object>>) m.get("UpdateList");
			if (updateList==null || updateList.size()==0) {
				map.put("ReturnType", "1011");
				map.put("Message", "无内容内容");
				return map;
			}
			int flowFlag = 2;
			try {flowFlag=Integer.parseInt(m.get("ContentFlowFlag")+"");} catch(Exception e) {};
			List<Map<String, Object>> retLs = seqContentService.modifySeqStatus(userId, updateList, flowFlag);
			if (retLs != null && retLs.size()>0) {
				map.put("ReturnType", "1001");
				map.put("Message", "修改成功");
				map.put("ResultList", retLs);
				return map;
			} else {
				map = new HashMap<>();
				map.put("ReturnType", "1011");
				map.put("Message", "专辑发布失败");
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
	 * 删除专辑
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/seq/removeSeqMedia.do")
	@ResponseBody
	public Map<String, Object> removeSeqMedia(HttpServletRequest request) {
		// 数据收集处理==1
		ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
		alPo.setApiName("5.3.5--/content/seq/removeSeqMedia.do");
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
                    mUdk=MobileParam.build(m).getUserDeviceKey();
                    if (StringUtils.isNullOrEmptyOrSpace(mUdk.getDeviceId())) { //是PC端来的请求
                        mUdk.setDeviceId(request.getSession().getId());
                    }
					Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/seq/removeSeqMedia");
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
			String contentids = m.get("ContentIds") + "";
			if (StringUtils.isNullOrEmptyOrSpace(contentids) || contentids.toLowerCase().equals("null")) {
				map.put("ReturnType", "1011");
				map.put("Message", "无专辑信息");
				return map;
			}
			List<Map<String, Object>> retLs = seqContentService.removeSeqMediaAsset(userId, contentids);
			if (retLs!=null && retLs.size()>0) {
				map.put("ResultList", retLs);
				map.put("ReturnType", "1001");
			} else {
				map.put("ReturnType", "1011");
				map.put("Message", "删除失败");
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
	 * 删除专辑
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/seq/removeMediassToSeqMediaRef.do")
	@ResponseBody
	public Map<String, Object> removeMaToSmaRef(HttpServletRequest request) {
		// 数据收集处理==1
		ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
		alPo.setApiName("5.3.6--/content/seq/removeMediassToSeqMediaRef.do");
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
                    mUdk=MobileParam.build(m).getUserDeviceKey();
                    if (StringUtils.isNullOrEmptyOrSpace(mUdk.getDeviceId())) { //是PC端来的请求
                        mUdk.setDeviceId(request.getSession().getId());
                    }
					Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/seq/removeMediassToSeqMediaRef");
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
			if (StringUtils.isNullOrEmptyOrSpace(userId) || userId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1011");
				map.put("Message", "无用户信息");
				return map;
			}
			String seqMediaId = m.get("SeqMediaId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(seqMediaId) || seqMediaId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1012");
				map.put("Message", "无专辑Id");
				return map;
			}
			String mediaAssetId = m.get("MediaAssetId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(mediaAssetId) || mediaAssetId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1013");
				map.put("Message", "无节目Id");
				return map;
			}
			seqContentService.removeMaToSmaRefInfo(userId, seqMediaId, mediaAssetId);
			map.put("ReturnType", "1001");
			map.put("Message", "处理成功");
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
	 * 获取专辑信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/seq/getSeqMediaInfo.do")
	@ResponseBody
	public Map<String, Object> getSeqMediaInfo(HttpServletRequest request) {
		// 数据收集处理==1
		ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
		alPo.setApiName("5.3.7--/content/seq/getSeqMediaInfo.do");
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
                    mUdk=MobileParam.build(m).getUserDeviceKey();
                    if (StringUtils.isNullOrEmptyOrSpace(mUdk.getDeviceId())) { //是PC端来的请求
                        mUdk.setDeviceId(request.getSession().getId());
                    }
					Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/seq/getSeqMediaInfo");
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

			// 1.开始采集数据
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
			Map<String, Object> seqm = seqContentService.getSeqMediaAssetInfo(userId, contentid);
			if (seqm != null) {
				map.put("ReturnType", "1001");
				map.put("Result", seqm);
				return map;
			} else {
				map.put("ReturnType", "1012");
				map.put("Message", "获取失败");
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
