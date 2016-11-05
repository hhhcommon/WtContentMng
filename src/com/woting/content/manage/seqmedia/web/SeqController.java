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
import com.woting.cm.core.media.model.SeqMediaAsset;
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
			String shortsearch = m.get("ShortSearch") + "";
			if (StringUtils.isNullOrEmptyOrSpace(shortsearch) || shortsearch.toLowerCase().equals("null")) {
				shortsearch = "false";
			}
			List<Map<String, Object>> c = seqContentService.getHostSeqMediaContents(userid, flagflow, channelid,
					shortsearch);
			if (c != null && c.size() > 0) {
				map.put("ReturnType", "1001");
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
			String contentname = m.get("ContentName") + "";
			if (contentname.toLowerCase().equals("null")) {
				map.put("ReturnType", "1011");
				map.put("Message", "无节目名称");
				return map;
			}
			String channelId = m.get("ChannelId")+"";
			List<Map<String, Object>> imgs = (List<Map<String, Object>>) m.get("ContentImg");
			List<Map<String, Object>> tags = (List<Map<String, Object>>) m.get("TagList");
			String rootpath = SystemCache.getCache(FConstants.APPOSPATH).getContent() + "";
//			String smaimg = m.get("ContentImg") + "";
//			if (smaimg.equals("null"))
//				smaimg = "htpp://www.wotingfm.com:908/CM/mweb/templet/zj_templet/imgs/default.png";
//			smaimg = smaimg.replace(rootpath, "http://" + ip_address + ":908/CM/");
			String contentdesc = m.get("ContentDesc") + "";
			String pubTime = m.get("FixedPubTime")+"";
			map = seqContentService.addSeqMediaInfo(userid, contentname, channelId, imgs, tags, contentdesc, pubTime);
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
	 * 修改专辑信息
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/content/seq/updateSeqMediaInfo.do")
	@ResponseBody
	public Map<String, Object> updateSeqMediaInfo(HttpServletRequest request) {
		SeqMediaAsset sma = new SeqMediaAsset();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId") + "";
		if (userid.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无用户信息");
			return map;
		}
		String smaid = m.get("ContentId") + "";
		if (smaid.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "修改失败");
			return map;
		}
		sma.setId(smaid);
		String smaname = m.get("ContentName") + "";
		if (!smaname.toLowerCase().equals("null"))
			sma.setSmaTitle(smaname);
		String rootpath = SystemCache.getCache(FConstants.APPOSPATH).getContent() + "";
		String smaimg = m.get("ContentImg") + "";
		smaimg = smaimg.replace(rootpath, "http://" + ip_address + ":908/CM/");
		if (!smaimg.toLowerCase().equals("null"))
			sma.setSmaImg(smaimg);
		String smadesc = m.get("ContentDesc") + "";
		if (!smadesc.toLowerCase().equals("null"))
			sma.setDescn(smadesc);
		String smastatus = m.get("ContentStatus") + "";
		if (!smastatus.toLowerCase().equals("null"))
			sma.setSmaStatus(Integer.valueOf(smastatus));
		String did = m.get("ContentCatalogsId") + ""; // 更改专辑的内容分类
		String chid = m.get("ContentChannelId") + ""; // 更改专辑的栏目
		map = seqContentService.updateSeqInfo(userid, sma, did, chid);
		return map;
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
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId") + "";
		if (userid.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无用户信息");
			return map;
		}
		String smaid = m.get("ContentId") + "";
		if (smaid.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无专辑id信息");
			return map;
		}
		String chid = m.get("ContentChannelId") + "";
		if (chid.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无栏目id信息");
			return map;
		}
		String subcount = m.get("SubCount") + "";
		if (subcount.equals("null") || (!subcount.equals("null") && Integer.valueOf(subcount) == 0)) {
			map.put("ReturnType", "1011");
			map.put("Message", "专辑无下级单体");
			return map;
		}
		map = seqContentService.modifySeqStatus(userid, smaid, chid, 2);
		return map;
	}

	/**
	 * 删除专辑
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/seq/removeSeqMediaInfo.do")
	@ResponseBody
	public Map<String, Object> removeSeqMediaInfo(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId") + "";
		if (userid.toLowerCase().equals("null")) {
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
		map = seqContentService.removeSeqMediaAsset(contentid);
		return map;
	}
}
