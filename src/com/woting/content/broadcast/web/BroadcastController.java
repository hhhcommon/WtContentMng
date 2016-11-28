package com.woting.content.broadcast.web;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.framework.util.RequestUtils;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.model.Page;
import com.spiritdata.framework.ui.tree.EasyUiTree;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.StringUtils;
import com.woting.WtContentMngConstants;
import com.woting.cm.core.channel.mem._CacheChannel;
import com.woting.cm.core.channel.model.Channel;
import com.woting.cm.core.dict.mem._CacheDictionary;
import com.woting.cm.core.dict.model.DictDetail;
import com.woting.cm.core.dict.model.DictModel;
import com.woting.cm.core.dict.persis.po.DictRefResPo;
import com.woting.content.broadcast.service.BroadcastProService;
import com.woting.dataanal.gather.API.ApiGatherUtils;
import com.woting.dataanal.gather.API.mem.ApiGatherMemory;
import com.woting.dataanal.gather.API.persis.pojo.ApiLogPo;
import com.woting.passport.mobile.MobileParam;
import com.woting.passport.mobile.MobileUDKey;
import com.woting.passport.session.DeviceType;
import com.woting.passport.session.SessionService;

@Controller
@RequestMapping(value = "/content/bc/")
public class BroadcastController {
	@Resource
	private BroadcastProService bcService;
	@Resource(name = "redisSessionService")
	private SessionService sessionService;

	@RequestMapping(value = "add.do")
	@ResponseBody
	public Map<String, Object> addBc(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		bcService.add(m);
		map.put("returnType", "1001");
		return map;
	}

	@RequestMapping(value = "addBroadcast.do")
	@ResponseBody
	public Map<String, Object> addBroadcast(HttpServletRequest request) {
		// 数据收集处理==1
		ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
		alPo.setApiName("5.4.4--/content/bc/addBroadcast.do");
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
					Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/bc/addBroadcast.do");
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
			String bcTitle = m.get("BcTitle") + "";
			if (StringUtils.isNullOrEmptyOrSpace(bcTitle) || bcTitle.toLowerCase().equals("null")) {
				map.put("ReturnType", "1012");
				map.put("Message", "无电台名称");
				return map;
			}
			String bcImg = m.get("BcImg") + "";
			if (StringUtils.isNullOrEmptyOrSpace(bcImg) || bcImg.toLowerCase().equals("null")) {
				map.put("ReturnType", "1013");
				map.put("Message", "无电台图片");
				return map;
			}
			String bcAreaId = m.get("BcAreaId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(bcAreaId) || bcAreaId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1014");
				map.put("Message", "无电台地区Id");
				return map;
			}
			String bcTypeId = m.get("BcTypeId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(bcTypeId) || bcTypeId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1015");
				map.put("Message", "无电台分类Id");
				return map;
			}
			String bcPlayPath = m.get("BcPlayPath") + "";
			if (StringUtils.isNullOrEmptyOrSpace(bcPlayPath) || bcPlayPath.toLowerCase().equals("null")) {
				map.put("ReturnType", "1016");
				map.put("Message", "无电台直播流");
				return map;
			}
			String bcPublisher = m.get("BcPublisher") + "";
			if (StringUtils.isNullOrEmptyOrSpace(bcPublisher) || bcPublisher.toLowerCase().equals("null")) {
				map.put("ReturnType", "1017");
				map.put("Message", "无电台发布者");
				return map;
			}
			String isMain = m.get("IsMain") + "";
			if (StringUtils.isNullOrEmptyOrSpace(isMain) || isMain.toLowerCase().equals("null")) {
				isMain = "0";
			}
			String bcDescn = m.get("BcDescn") + "";
			if (StringUtils.isNullOrEmptyOrSpace(bcDescn) || bcDescn.toLowerCase().equals("null")) {
				bcDescn = null;
			}
			bcService.addBroadcast(userId, bcTitle, bcImg, bcAreaId, bcTypeId, bcPlayPath, bcPublisher, isMain,
					bcDescn);
			map.put("ReturnType", "1001");
			map.put("Message", "电台添加成功");
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

	@RequestMapping(value = "updateBroadcast.do")
	@ResponseBody
	public Map<String, Object> updateBroadcast(HttpServletRequest request) {
		// 数据收集处理==1
		ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
		alPo.setApiName("5.4.5--/content/bc/updateBroadcast.do");
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
				if (StringUtils.isNullOrEmptyOrSpace(mp.getImei()) && DeviceType.buildDtByPCDType(StringUtils.isNullOrEmptyOrSpace(mp.getPCDType()) ? -1 : Integer.parseInt(mp.getPCDType())) == DeviceType.PC) { // 是PC端来的请求
					mp.setImei(request.getSession().getId());
				}
				mUdk = mp.getUserDeviceKey();
				if (mUdk != null) {
					Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/bc/updateBroadcast.do");
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
			String bcId = m.get("BcId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(bcId) || bcId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1011");
				map.put("Message", "无电台Id");
				return map;
			}
			String bcTitle = m.get("BcTitle") + "";
			if (StringUtils.isNullOrEmptyOrSpace(bcTitle) || bcTitle.toLowerCase().equals("null")) {
				map.put("ReturnType", "1012");
				map.put("Message", "无电台名称");
				return map;
			}
			String bcImg = m.get("BcImg") + "";
			if (StringUtils.isNullOrEmptyOrSpace(bcImg) || bcImg.toLowerCase().equals("null")) {
				map.put("ReturnType", "1013");
				map.put("Message", "无电台图片");
				return map;
			}
			String bcAreaId = m.get("BcAreaId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(bcAreaId) || bcAreaId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1014");
				map.put("Message", "无电台地区Id");
				return map;
			}
			String bcTypeId = m.get("BcTypeId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(bcTypeId) || bcTypeId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1015");
				map.put("Message", "无电台分类Id");
				return map;
			}
			String bcPlayPath = m.get("BcPlayPath") + "";
			if (StringUtils.isNullOrEmptyOrSpace(bcPlayPath) || bcPlayPath.toLowerCase().equals("null")) {
				map.put("ReturnType", "1016");
				map.put("Message", "无电台直播流");
				return map;
			}
			String bcPublisher = m.get("BcPublisher") + "";
			if (StringUtils.isNullOrEmptyOrSpace(bcPublisher) || bcPublisher.toLowerCase().equals("null")) {
				map.put("ReturnType", "1017");
				map.put("Message", "无电台发布者");
				return map;
			}
			String isMain = m.get("IsMain") + "";
			if (StringUtils.isNullOrEmptyOrSpace(isMain) || isMain.toLowerCase().equals("null")) {
				isMain = "0";
			}
			String bcDescn = m.get("BcDescn") + "";
			if (StringUtils.isNullOrEmptyOrSpace(bcDescn) || bcDescn.toLowerCase().equals("null")) {
				bcDescn = null;
			}
			bcService.updateBroadcast(userId, bcId, bcTitle, bcImg, bcAreaId, bcTypeId, bcPlayPath, bcPublisher, isMain,
					bcDescn);
			map.put("ReturnType", "1001");
			map.put("Message", "修改成功");
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

	@RequestMapping(value = "loadBc.do")
	@ResponseBody
	public Page<Map<String, Object>> loadBc(HttpServletRequest request) {
		Page<Map<String, Object>> _p = new Page<Map<String, Object>>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		_p = bcService.getViewList(m);

		Collection<Map<String, Object>> retResult = _p.getResult();
		if (retResult != null && retResult.size() > 0) {
			String ids = "";
			for (Map<String, Object> one : retResult) {// 此次扫描，得到所有的Id
				ids += ",'" + one.get("id") + "'";
			}
			List<DictRefResPo> rcrpL = bcService.getCataRefList(ids.substring(1));
			if (rcrpL != null && rcrpL.size() > 0) {
				for (Map<String, Object> one : retResult) {// 此次扫描，填充数据
					ids = "" + one.get("id");
					String areaName = "", typeName = "";
					boolean up = false, down = false;
					for (int i = 0; i < rcrpL.size(); i++) {
						if (up && down)
							break;
						DictRefResPo rcrp = rcrpL.get(i);
						if (rcrp.getResId().equals(ids)) {
							if (!up)
								up = true;
							// if (rcrp.getDictMid().equals("1"))
							// typeName+=","+rcrp.getTitle();
							// else if (rcrp.getDictMid().equals("2"))
							// areaName+=","+rcrp.getPathNames();
							if (i == rcrpL.size() - 1)
								down = true;
						} else {
							if (up)
								down = true;
						}
					}
					if (up && down) {
						one.put("typeName", (StringUtils.isNullOrEmptyOrSpace(typeName) ? "" : typeName.substring(1)));
						one.put("areaName", (StringUtils.isNullOrEmptyOrSpace(areaName) ? "" : areaName.substring(1)));
					}
				}
			}
		}
		System.out.println(JsonUtils.objToJson(_p));
		return _p;
	}

	@RequestMapping(value = "delBc.do")
	@ResponseBody
	public Map<String, Object> delBc(HttpServletRequest request) {
		// 数据收集处理==1
		ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
		alPo.setApiName("5.4.6--/content/bc/delBc.do");
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
					Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/bc/delBc.do");
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
			//
			bcService.del(m.get("Ids") + "");
			map.put("ReturnType", "1001");
			map.put("Message", "删除成功");
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

	@RequestMapping(value = "getCataTrees4View.do")
	@ResponseBody
	// 这是一个临时方法
	public Map<String, Object> getCataTrees4View(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> l = new ArrayList<Map<String, Object>>();
		@SuppressWarnings("unchecked")
		CacheEle<_CacheDictionary> cache = ((CacheEle<_CacheDictionary>) SystemCache
				.getCache(WtContentMngConstants.CACHE_DICT));
		if (cache == null) {
			map.put("jsonType", "0");
			map.put("data", "没有数据");
		} else {
			try {
				_CacheDictionary _cd = ((CacheEle<_CacheDictionary>) SystemCache
						.getCache(WtContentMngConstants.CACHE_DICT)).getContent();
				DictModel dm = _cd.getDictModelById("1");
				EasyUiTree<DictDetail> eu1 = new EasyUiTree<DictDetail>(dm.dictTree);
				l.add(eu1.toTreeMap());
				dm = _cd.getDictModelById("2");
				eu1 = new EasyUiTree<DictDetail>(dm.dictTree);
				l.add(eu1.toTreeMap());
				dm = _cd.getDictModelById("3");
				eu1 = new EasyUiTree<DictDetail>(dm.dictTree);
				l.add(eu1.toTreeMap());
				_CacheChannel _cc = ((CacheEle<_CacheChannel>) SystemCache
						.getCache(WtContentMngConstants.CACHE_CHANNEL)).getContent();
				EasyUiTree<Channel> eu2 = new EasyUiTree<Channel>(_cc.channelTree);
				l.add(eu2.toTreeMap());
				map.put("jsonType", "1");
				map.put("data", l);
			} catch (CloneNotSupportedException e) {
				map.put("jsonType", "2");
				map.put("err", e.getMessage());
				e.printStackTrace();
			}
		}
		return map;
	}

	@RequestMapping(value = "getBcProgramme.do")
	@ResponseBody
	public Map<String, Object> getBcProgramme(HttpServletRequest request) {
		// 数据收集处理==1
		ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
		alPo.setApiName("5.4.3--/content/bc/getBcProgramme.do");
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
						&& DeviceType.buildDtByPCDType(StringUtils.isNullOrEmptyOrSpace(mp.getPCDType()) ? -1 : Integer.parseInt(mp.getPCDType())) == DeviceType.PC) { // 是PC端来的请求
					mp.setImei(request.getSession().getId());
				}
				mUdk = mp.getUserDeviceKey();
				if (mUdk != null) {
					Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/bc/getBcInfo.do");
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
			
			//
			userId = m.get("UserId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(userId) || userId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1011");
				map.put("Message", "无用户Id");
				return map;
			}
			String bcId = m.get("BcId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(bcId) || bcId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1012");
				map.put("Message", "无内容Id");
				return map;
			}
			List<Map<String, Object>> l = bcService.getBcProgrammes(bcId);
			if (l != null && l.size() > 0) {
				map.put("ResultInfo", l);
				map.put("ReturnType", "1001");
			} else {
				map.put("ReturnType", "1011");
				map.put("Message", "查询失败");
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
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "updateBcProgramme.do")
	@ResponseBody
	public Map<String, Object> updateBcProgramme(HttpServletRequest request) {
		// 数据收集处理==1
		ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
		alPo.setApiName("5.4.5--/content/bc/updateBcProgramme.do");
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
				if (StringUtils.isNullOrEmptyOrSpace(mp.getImei()) && DeviceType.buildDtByPCDType(StringUtils.isNullOrEmptyOrSpace(mp.getPCDType()) ? -1 : Integer.parseInt(mp.getPCDType())) == DeviceType.PC) { // 是PC端来的请求
					mp.setImei(request.getSession().getId());
				}
				mUdk = mp.getUserDeviceKey();
				if (mUdk != null) {
					Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/bc/updateBroadcast.do");
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
			String bcId = m.get("BcId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(bcId) || bcId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1012");
				map.put("Message", "无电台Id");
				return map;
			}
			List<Map<String, Object>> programmes = (List<Map<String, Object>>) m.get("ProgrammeList");
			if (programmes==null || programmes.size()==0) {
				map.put("ReturnType", "1013");
				map.put("Message", "无节目列表");
				return map;
			}
			bcService.updateBcProgrammes(userId,bcId,programmes);
			map.put("ReturnType", "1001");
			map.put("Message", "修改成功");
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

	@RequestMapping(value = "getBcList.do")
	@ResponseBody
	public Map<String, Object> getBcList(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String userid = m.get("UserId") + "";
		String page = m.get("Page") + "";
		String pagesize = m.get("PageSize") + "";
		String errMsg = "";

		System.out.println(userid + "#" + page + "#" + pagesize);

		if (userid.toLowerCase().equals("null"))
			errMsg += ",无用户信息";
		if (page.toLowerCase().equals("null"))
			errMsg += ",无页码信息";
		if (pagesize.toLowerCase().equals("null"))
			errMsg += ",无每页条数信息";
		if (!StringUtils.isNullOrEmptyOrSpace(errMsg)) {
			errMsg = errMsg.substring(1);
			map.put("ReturnType", "1002");
			map.put("Message", errMsg);
			return map;
		}
		int pagenum = Integer.valueOf(page);
		int pagesizenum = Integer.valueOf(pagesize);
		List<Map<String, Object>> bclist = bcService.getBroadcastListInfo(pagenum, pagesizenum);

		if (bclist == null) {
			map.put("ReturnType", "1002");
			map.put("Message", "无数据");
			return map;
		}
		map.put("ReturnType", "1001");
		map.put("ResultList", bclist);
		map.put("AllCount", bclist.size());
		return map;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "getBroadcastList.do")
	@ResponseBody
	public Map<String, Object> getBroadcastList(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String datastr = JsonUtils.objToJson(m);
		Map<String, String> datas = (Map<String, String>) JsonUtils.jsonToObj(datastr, Map.class);
		Document doc = null;
		try {
			doc = Jsoup.connect("http://123.56.254.75:808/wt/content/getContents.do").ignoreContentType(true)
					.data(datas).post();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (doc != null) {
			String elem = doc.body().html();
			elem = elem.replace("&quot;", "\"");
			map = (Map<String, Object>) JsonUtils.jsonToObj(elem, Map.class);
		}
		return map;
	}
	
	@RequestMapping(value = "getBcInfo.do")
	@ResponseBody
	public Map<String, Object> getBcInfo(HttpServletRequest request) {
		// 数据收集处理==1
		ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
		alPo.setApiName("5.4.3--/content/bc/getBcInfo.do");
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
					Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/bc/getBcInfo.do");
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
			
			//
			userId = m.get("UserId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(userId) || userId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1011");
				map.put("Message", "无用户Id");
				return map;
			}
			String bcId = m.get("BcId") + "";
			if (StringUtils.isNullOrEmptyOrSpace(bcId) || bcId.toLowerCase().equals("null")) {
				map.put("ReturnType", "1012");
				map.put("Message", "无内容Id");
				return map;
			}
			List<Map<String, Object>> l = bcService.getBroadcastInfo(bcId);
			if (l != null && l.size() > 0) {
				map.put("ResultInfo", l);
				map.put("ReturnType", "1001");
			} else {
				map.put("ReturnType", "1011");
				map.put("Message", "查询失败");
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
}