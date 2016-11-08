package com.woting.content.manage.filtrate.web;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.RequestUtils;
import com.spiritdata.framework.util.StringUtils;
import com.woting.content.manage.filtrate.service.FiltrateService;
import com.woting.dataanal.gather.API.ApiGatherUtils;
import com.woting.dataanal.gather.API.mem.ApiGatherMemory;
import com.woting.dataanal.gather.API.persis.pojo.ApiLogPo;
import com.woting.passport.mobile.MobileParam;
import com.woting.passport.mobile.MobileUDKey;
import com.woting.passport.session.DeviceType;
import com.woting.passport.session.SessionService;

@Controller
public class FiltrateController {
	@Resource
	private FiltrateService filtrateService;
	@Resource(name="redisSessionService")
    private SessionService sessionService;
	
	/**
	 * 得到主播管理页面的筛选条件
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/getFiltrates.do")
	@ResponseBody
	public Map<String, Object> getFiltrates(HttpServletRequest request) {
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
			String mediatype = m.get("MediaType") + "";
			if (mediatype.toLowerCase().equals("null")) {
				map.put("ReturnType", "1012");
				map.put("Message", "无资源类型信息");
				return map;
			}
			Map<String, Object> ms = null;
			ms = filtrateService.getFiltrateByMediaType(userid, mediatype);
			if (ms != null) {
				map.put("ReturnType", "1001");
				map.put("ResultList", ms);
				return map;
			} else {
				map.put("ReturnType", "1011");
				map.put("Message", "没有查到任何内容");
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
			}
		}
	}
}