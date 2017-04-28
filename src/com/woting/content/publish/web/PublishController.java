package com.woting.content.publish.web;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.RequestUtils;
import com.spiritdata.framework.util.StringUtils;
import com.woting.content.publish.service.QueryService;
import com.woting.dataanal.gather.API.ApiGatherUtils;
import com.woting.dataanal.gather.API.mem.ApiGatherMemory;
import com.woting.dataanal.gather.API.persis.pojo.ApiLogPo;
import com.woting.passport.mobile.MobileParam;
import com.woting.passport.mobile.MobileUDKey;
import com.woting.passport.session.DeviceType;
import com.woting.passport.session.SessionService;


/**
 * 列表查询接口
 *
 * @author wbq
 *
 */
@Controller
public class PublishController {
	@Resource
	private QueryService queryService;
    @Resource(name = "redisSessionService")
    private SessionService sessionService;

    /**
	 * 查询列表信息
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/content/getContents.do")
	@ResponseBody
	public Map<String, Object> getContents(HttpServletRequest request) {
		long begtime = System.currentTimeMillis();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String ChannelId = null;
		String flowFlag = null;
		String publisherId = null;
		String mediaType = null;
		Timestamp begincontentpubtime = null;
		Timestamp endcontentpubtime = null;
		Timestamp begincontentctime = null;
		Timestamp endcontentctime = null;
//		String userId = m.get("UserId")+"";
		int page = m.get("Page") == null ? 1 : Integer.valueOf(m.get("Page")+"");
		int pagesize = m.get("PageSize") == null ? 10 : Integer.valueOf( m.get("PageSize")+"");
		if (m.containsKey("MediaType")) 
			mediaType = m.get("MediaType") == null ? null : m.get("MediaType")+"";
		if (m.containsKey("ChannelId"))
			ChannelId = (String) m.get("ChannelId");
		if (m.containsKey("ContentFlowFlag"))
			flowFlag = m.get("ContentFlowFlag") == null ? null : m.get("ContentFlowFlag")+"";
		if (m.containsKey("SourceId"))
			publisherId = (String) m.get("SourceId");
		if (m.containsKey("BeginContentPubTime") && m.get("BeginContentPubTime")!=null && !m.get("BeginContentPubTime").equals("null"))
			begincontentpubtime = new Timestamp(Long.valueOf(m.get("BeginContentPubTime")+""));
		if (m.containsKey("EndContentPubTime") && m.get("EndContentPubTime")!=null && !m.get("EndContentPubTime").equals("null"))
			endcontentpubtime =  new Timestamp(Long.valueOf(m.get("EndContentPubTime")+""));
		if (m.containsKey("BeginContentCTime") && m.get("BeginContentCTime")!=null && !m.get("BeginContentCTime").equals("null"))
			begincontentctime =  new Timestamp(Long.valueOf(m.get("BeginContentCTime")+""));
		if (m.containsKey("EndContentCTime") && m.get("EndContentCTime")!=null && !m.get("EndContentCTime").equals("null"))
			endcontentctime =  new Timestamp(Long.valueOf(m.get("EndContentCTime")+""));
		if (page > 0 && pagesize > 0) {
			Map<String, Object> maplist = queryService.getContent(flowFlag, page, pagesize, mediaType, ChannelId, publisherId, begincontentpubtime, endcontentpubtime, begincontentctime, endcontentctime);
			List<Map<String, Object>> list = (List<Map<String, Object>>)maplist.get("List");
			if (list.size() > 0) {
				map.put("ResultList", maplist.get("List"));
				map.put("ReturnType", "1001");
				map.put("AllCount", maplist.get("Count"));
			} else {
				map.put("ReturnType", "1011");
				map.put("Message", "无内容");
				map.put("AllCount", maplist.get("Count"));
			}
		} else {
			map.put("ReturnType", "1012");
			map.put("Message", "请求信息有误");
		}
		map.put("TestDurtion", System.currentTimeMillis()-begtime);
		return map;
	}

	/**
	 * 查询节目详细信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/getContentInfo.do")
	@ResponseBody
	public Map<String, Object> getContentInfo(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
//		String userId = (String) m.get("UserId");
		int pageSize = 10;
		try {pageSize=Integer.parseInt(m.get("PageSize")+"");} catch(Exception e) {};
		int page = 1;
		try {page=Integer.parseInt(m.get("Page")+"");} catch(Exception e) {};
		String contentId = m.get("ContentId") + "";
		if (StringUtils.isNullOrEmptyOrSpace(contentId) || contentId.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无内容Id");
			return map;
		}
		String mediatype = m.get("MediaType")+"";
		if (StringUtils.isNullOrEmptyOrSpace(mediatype) || mediatype.toLowerCase().equals("null")) {
			map.put("ReturnType", "1012");
			map.put("Message", "无内容类型");
			return map;
		}
		Map<String, Object> mapdetail = queryService.getContentInfo(pageSize, page, contentId, mediatype);
		if (mediatype.equals("wt_SeqMediaAsset")) {
			if (mapdetail!=null && mapdetail.get("audio") != null) {
				map.put("ContentDetail", mapdetail.get("sequ"));
				map.put("SubList", mapdetail.get("audio"));
				map.put("ReturnType", "1001");
			} else {
				map.put("ReturnType", "1011");
				map.put("Message", "没有相关内容 ");
			}
		} else {
			if (mediatype.equals("wt_MediaAsset")) {
				if (mapdetail!=null && map.isEmpty()) {
					map.put("ContentDetail", mapdetail);
					map.put("ReturnType", "1001");
				} else {
					map.put("ReturnType", "1011");
					map.put("Message", "没有相关内容 ");
				}
			} else {
				if (mapdetail!=null && mediatype.equals("wt_Broadcast")) {
					map.put("SubList", mapdetail);
					map.put("ReturnType", "1001");
				} else {
					map.put("ReturnType", "1011");
					map.put("Message", "没有相关内容 ");
				}
			}
		}
		return map;
	}

	/**
	 * 修改序号和审核状态
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/content/updateContentStatus.do")
	@ResponseBody
	public Map<String, Object> updateContentStatus(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
//		String userId = (String) m.get("UserId");
		List<Map<String, Object>> contentIds = new ArrayList<>();
		if (m.containsKey("ContentIds")) {
			contentIds = (List<Map<String, Object>>) m.get("ContentIds");
			if (contentIds==null || contentIds .size()==0) {
				map.put("ReturnType", "1012");
				map.put("Message", "无内容Id");
				return map;
			}
		} else {
			map.put("ReturnType", "1012");
			map.put("Message", "无内容Id");
			return map;
		}
		String opeType = m.get("OpeType") + "";
		if (StringUtils.isNullOrEmptyOrSpace(opeType) || opeType.toLowerCase().equals("null")) {
			map.put("ReturnType", "1014");
			map.put("Message", "无操作类型");
			return map;
		}
		String channelIds = m.get("ChannelIds") + "";
		if (StringUtils.isNullOrEmptyOrSpace(channelIds) || channelIds.toLowerCase().equals("null")) {
			channelIds = null;
		}
		String reDescn = null;
		try {reDescn=m.get("ReDescn").toString();} catch(Exception e) {};
		boolean isok = false;
		if (opeType.equals("sort")) {
			String numbers = (String) m.get("ContentSort");
			int number = 0;
			try {
				number = Integer.valueOf(numbers);
			} catch (Exception e) {
				e.printStackTrace();
				map.put("ReturnType", "1015");
				map.put("Message", "无排序号");
				return map;
			}
			isok = queryService.modifyInfo(contentIds, number, opeType, reDescn);
		} else {
			if (opeType.equals("pass") || opeType.equals("nopass") || opeType.equals("revoke") || opeType.equals("revocation") || opeType.equals("norevocation")) {
				isok = queryService.modifyInfo(contentIds, 0, opeType, reDescn);
			}
		}
		if (isok) {
			map.put("ReturnType", "1001");
			map.put("Message", "修改成功");
			return map;
		} else {
			map.put("ReturnType", "1011");
			map.put("Message", "修改失败");
			return map;
		}
	}

	/**
	 * 查询栏目分类和发布组织信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/getConditions.do")
	@ResponseBody
	public Map<String, Object> getCatalogs(HttpServletRequest request) {
//		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
//		String userId = (String) m.get("UserId");
		Map<String, Object> map = new HashMap<String, Object>();
		map = queryService.getConditionsInfo();
		map.put("ReturnType", "1001");
		return map;
	}

	/**
	 * 分享页的分页加载请求
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/getZJSubPage.do")
	@ResponseBody
	public Map<String, Object> getZJSubPage(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String contentId = m.get("ContentId") + "";
		if (StringUtils.isNullOrEmptyOrSpace(contentId) || contentId.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无内容ID");
			return map;
		}
		String page = m.get("Page") + "";
		if (StringUtils.isNullOrEmptyOrSpace(page) || page.toLowerCase().equals("null")) {
			map.put("ReturnType", "1011");
			map.put("Message", "无页码");
			return map;
		}
		map = queryService.getZJSubPage(contentId, page);
		return map;
	}
	
	/**
	 * 搜索内容请求
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/content/searchContents.do")
	@ResponseBody
	public Map<String, Object> getSearchContents(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String searchWord = m.get("SearchWord") + "";
		if (StringUtils.isNullOrEmptyOrSpace(searchWord) || searchWord.toLowerCase().equals("null")) {
			map.put("ReturnType", "1012");
			map.put("Message", "无搜索内容");
			return map;
		}
		String flowFlag = m.get("ContentFlowFlag") + "";
		if (StringUtils.isNullOrEmptyOrSpace(flowFlag) || flowFlag.toLowerCase().equals("null")) {
			map.put("ReturnType", "1013");
			map.put("Message", "无发布状态");
			return map;
		}
		String mediaType = null;
		if (m.containsKey("MediaType"))
			mediaType = m.get("MediaType") == null ? null : m.get("MediaType")+"";
		String channelId = null;
		if (m.containsKey("ChannelId"))
			channelId = (String) m.get("ChannelId");
		String publisherId = null; 
		if (m.containsKey("SourceId"))
			publisherId = (String) m.get("SourceId");
		Timestamp begincontentpubtime = null;
		if (m.containsKey("BeginContentPubTime") && m.get("BeginContentPubTime")!=null && !m.get("BeginContentPubTime").equals("null"))
			begincontentpubtime = new Timestamp(Long.valueOf(m.get("BeginContentPubTime")+""));
		Timestamp endcontentpubtime = null;
		if (m.containsKey("EndContentPubTime") && m.get("EndContentPubTime")!=null && !m.get("EndContentPubTime").equals("null"))
			endcontentpubtime =  new Timestamp(Long.valueOf(m.get("EndContentPubTime")+""));
		Timestamp begincontentctime = null;
		if (m.containsKey("BeginContentCTime") && m.get("BeginContentCTime")!=null && !m.get("BeginContentCTime").equals("null"))
			begincontentctime =  new Timestamp(Long.valueOf(m.get("BeginContentCTime")+""));
		Timestamp endcontentctime = null;
		if (m.containsKey("EndContentCTime") && m.get("EndContentCTime")!=null && !m.get("EndContentCTime").equals("null"))
			endcontentctime =  new Timestamp(Long.valueOf(m.get("EndContentCTime")+""));
		//得到每页记录数
        int pageSize=10;
        try {pageSize=Integer.parseInt(m.get("PageSize")+"");} catch(Exception e) {};
        //得到当前页数
        int page=1;
        try {page=Integer.parseInt(m.get("Page")+"");} catch(Exception e) {};
		Map<String, Object> retM = queryService.getSearchContentList(searchWord, flowFlag, page, pageSize, mediaType, channelId, publisherId, begincontentpubtime, endcontentpubtime, begincontentctime, endcontentctime);
		if (retM!=null) {
			List<Map<String, Object>> ls = (List<Map<String, Object>>) retM.get("List");
			if (ls!=null && ls.size()>0) {
				map.put("ReturnType", "1001");
			    map.put("ResultList", ls);
			    map.put("AllCount", retM.get("Count"));
			    return map;
			} 
		} 
		map.put("ReturnType", "1011");
		return map;
	}
	
	/**
	 * 搜索内容请求
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/content/getAppRevocation.do")
	@ResponseBody
	public Map<String, Object> getAppRevocation(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		String mediaType = "SEQU";
		if (m.containsKey("MediaType")) 
			mediaType = m.get("MediaType") == null ? null : m.get("MediaType")+"";
		String channelId = null;
		if (m.containsKey("ChannelId"))
			channelId = (String) m.get("ChannelId");
		String publisherId = null;
		if (m.containsKey("PubliusherId"))
			publisherId = (String) m.get("PubliusherId");
		Timestamp begincontentpubtime = null;
		if (m.containsKey("BeginContentPubTime") && m.get("BeginContentPubTime")!=null && !m.get("BeginContentPubTime").equals("null"))
			begincontentpubtime = new Timestamp(Long.valueOf(m.get("BeginContentPubTime")+""));
		Timestamp endcontentpubtime = null;
		if (m.containsKey("EndContentPubTime") && m.get("EndContentPubTime")!=null && !m.get("EndContentPubTime").equals("null"))
			endcontentpubtime =  new Timestamp(Long.valueOf(m.get("EndContentPubTime")+""));
		Timestamp begincontentctime = null;
		if (m.containsKey("BeginContentCTime") && m.get("BeginContentCTime")!=null && !m.get("BeginContentCTime").equals("null"))
			begincontentctime =  new Timestamp(Long.valueOf(m.get("BeginContentCTime")+""));
		Timestamp endcontentctime = null;
		if (m.containsKey("EndContentCTime") && m.get("EndContentCTime")!=null && !m.get("EndContentCTime").equals("null"))
			endcontentctime =  new Timestamp(Long.valueOf(m.get("EndContentCTime")+""));
		int applyFlowFlag = 1;
        try {applyFlowFlag=Integer.parseInt(m.get("ApplyFlowFlag")+"");} catch(Exception e) {};
        int reFlowFlag = 0;
        try {reFlowFlag=Integer.parseInt(m.get("ReFlowFlag")+"");} catch(Exception e) {};
		//得到每页记录数
        int pageSize=10;
        try {pageSize=Integer.parseInt(m.get("PageSize")+"");} catch(Exception e) {};
        //得到当前页数
        int page=1;
        try {page=Integer.parseInt(m.get("Page")+"");} catch(Exception e) {};
		Map<String, Object> retM = queryService.getAppRevocationList(applyFlowFlag, reFlowFlag, page, pageSize, mediaType, channelId, publisherId, begincontentpubtime, endcontentpubtime, begincontentctime, endcontentctime);
		if (retM!=null) {
			List<Map<String, Object>> ls = (List<Map<String, Object>>) retM.get("List");
			if (ls!=null && ls.size()>0) {
				map.put("ReturnType", "1001");
			    map.put("ResultInfo", retM);
			    return map;
			} 
		} 
		map.put("ReturnType", "1011");
		return map;
	}

    @RequestMapping(value = "/content/setTop.do")
    @ResponseBody
    public Map<String, Object> setTop(HttpServletRequest request) {
        // 数据收集处理==1
        ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
        alPo.setApiName("6.3.1--/content/setTop.do");
        alPo.setObjType("010");//内容发布
        alPo.setDealFlag(1);// 处理成功
        alPo.setOwnerType(201);
        alPo.setOwnerId("--");

        Map<String, Object> map=new HashMap<String, Object>();
        try {
            // 0-获取参数
            String userId=null; //用户判断用户权限，目前不起作用
            MobileUDKey mUdk=null;
            Map<String, Object> m=RequestUtils.getDataFromRequest(request);
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
                if (mUdk!=null) {
                    Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/setTop");
                    if ((retM.get("ReturnType") + "").equals("2003")) {
                        map.put("ReturnType", "200");
                        map.put("Message", "需要登录");
                    } else {
                        map.putAll(retM);
                        if ((retM.get("ReturnType") + "").equals("1001")) map.remove("ReturnType");
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
                if (mUdk != null) alPo.setOwnerId(mUdk.getDeviceId());
                else alPo.setOwnerId("0");
            }
            if (mUdk != null) {
                alPo.setDeviceType(mUdk.getPCDType());
                alPo.setDeviceId(mUdk.getDeviceId());
            }
            if (m!=null) {
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

            if (StringUtils.isNullOrEmptyOrSpace(userId)) {
                map.put("ReturnType", "1002");
                map.put("Message", "用户不存在");
                return map;
            }
            //得到参数
            String mediaType=(m.get("MediaType")==null?null:m.get("MediaType").toString());
            String contentId=(m.get("ContentId")==null?null:m.get("ContentId").toString());
            String channelId=(m.get("ChannelId")==null?null:m.get("ChannelId").toString());
            String _err=","+(StringUtils.isNullOrEmptyOrSpace(mediaType)?"媒体类型为空":"");
            _err+=","+(StringUtils.isNullOrEmptyOrSpace(mediaType)?"内容Id为空":"");
            _err+=","+(StringUtils.isNullOrEmptyOrSpace(mediaType)?"栏目Id为空":"");
            _err=_err.substring(1);
            if (StringUtils.isNullOrEmptyOrSpace(_err)) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数:"+_err);
                return map;
            }
            int onlyTop=1;//栏目中仅仅一个置顶
            try {onlyTop=Integer.parseInt(m.get("IsOnlyTop")+"");} catch(Exception e) {};
            int topSort=1;//置顶排序，0是取消置顶
            try {topSort=Integer.parseInt(m.get("Top")+"");} catch(Exception e) {};

            map.putAll(queryService.setTop(mediaType, contentId, channelId, onlyTop, topSort));
            return map;
        } catch(Exception e) {
            e.printStackTrace();
            map.put("ReturnType", "T");
            map.put("TClass", e.getClass().getName());
            map.put("Message", StringUtils.getAllMessage(e));
            alPo.setDealFlag(2);
            return map;
        } finally {
            //数据收集处理=3
            alPo.setEndTime(new Timestamp(System.currentTimeMillis()));
            alPo.setReturnData(JsonUtils.objToJson(map));
            try {
                ApiGatherMemory.getInstance().put2Queue(alPo);
            } catch (InterruptedException e) {}
        }
    }

    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/content/getLoopImages.do")
    @ResponseBody
    public Map<String, Object> getContentsWithLoopImgInChannel(HttpServletRequest request) {
        // 数据收集处理==1
        ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
        alPo.setApiName("6.4.2--/content/getLoopImages.do");
        alPo.setObjType("010");//内容发布
        alPo.setDealFlag(1);// 处理成功
        alPo.setOwnerType(201);
        alPo.setOwnerId("--");

        Map<String, Object> map=new HashMap<String, Object>();
        try {
            // 0-获取参数
            String userId=null; //用户判断用户权限，目前不起作用
            MobileUDKey mUdk=null;
            Map<String, Object> m=RequestUtils.getDataFromRequest(request);
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
                if (mUdk!=null) {
                    Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/getLoopImages");
                    if ((retM.get("ReturnType") + "").equals("2003")) {
                        map.put("ReturnType", "200");
                        map.put("Message", "需要登录");
                    } else {
                        map.putAll(retM);
                        if ((retM.get("ReturnType") + "").equals("1001")) map.remove("ReturnType");
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
                if (mUdk != null) alPo.setOwnerId(mUdk.getDeviceId());
                else alPo.setOwnerId("0");
            }
            if (mUdk != null) {
                alPo.setDeviceType(mUdk.getPCDType());
                alPo.setDeviceId(mUdk.getDeviceId());
            }
            if (m!=null) {
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

            if (StringUtils.isNullOrEmptyOrSpace(userId)) {
                map.put("ReturnType", "1002");
                map.put("Message", "用户不存在");
                return map;
            }
            //得到参数
            String channelId=(m.get("ChannelId")==null?null:m.get("ChannelId").toString());
            if (StringUtils.isNullOrEmptyOrSpace(channelId)) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数:栏目Id");
                return map;
            }
            String mediaType=(m.get("MediaType")==null?null:m.get("MediaType").toString());
            //获取分页信息
            int page=0;//获取页数
            try {page=Integer.parseInt(m.get("Page")+"");} catch(Exception e) {};
            int pageSize=10;//得到每页条数
            try {pageSize=Integer.parseInt(m.get("PageSize")+"");} catch(Exception e) {};

            Map<String, Object> _map=queryService.getLoopImgList(mediaType, channelId, pageSize, page);
            List<Map<String, Object>> resultList=(List<Map<String, Object>>) _map.get("ResultList");
            if (resultList==null||resultList.isEmpty()) {
                map.put("ReturnType", "1011");
            } else {
                int count=(int)_map.get("AllCount");
                map.put("ReturnType", "1001");
                map.put("ResultList", resultList);
                map.put("AllCount", count);
            }
            return map;
        } catch(Exception e) {
            e.printStackTrace();
            map.put("ReturnType", "T");
            map.put("TClass", e.getClass().getName());
            map.put("Message", StringUtils.getAllMessage(e));
            alPo.setDealFlag(2);
            return map;
        } finally {
            //数据收集处理=3
            alPo.setEndTime(new Timestamp(System.currentTimeMillis()));
            alPo.setReturnData(JsonUtils.objToJson(map));
            try {
                ApiGatherMemory.getInstance().put2Queue(alPo);
            } catch (InterruptedException e) {}
        }
    }
    
    @RequestMapping(value = "/content/sortLoopImage.do")
    @ResponseBody
    public Map<String, Object> updateLoopSortInChannel(HttpServletRequest request) {
        // 数据收集处理==1
        ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
        alPo.setApiName("6.4.3--/content/sortLoopImage.do");
        alPo.setObjType("010");//内容发布
        alPo.setDealFlag(1);// 处理成功
        alPo.setOwnerType(201);
        alPo.setOwnerId("--");

        Map<String, Object> map=new HashMap<String, Object>();
        try {
            // 0-获取参数
            String userId=null; //用户判断用户权限，目前不起作用
            MobileUDKey mUdk=null;
            Map<String, Object> m=RequestUtils.getDataFromRequest(request);
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
                if (mUdk!=null) {
                    Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/sortLoopImage");
                    if ((retM.get("ReturnType") + "").equals("2003")) {
                        map.put("ReturnType", "200");
                        map.put("Message", "需要登录");
                    } else {
                        map.putAll(retM);
                        if ((retM.get("ReturnType") + "").equals("1001")) map.remove("ReturnType");
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
                if (mUdk != null) alPo.setOwnerId(mUdk.getDeviceId());
                else alPo.setOwnerId("0");
            }
            if (mUdk != null) {
                alPo.setDeviceType(mUdk.getPCDType());
                alPo.setDeviceId(mUdk.getDeviceId());
            }
            if (m!=null) {
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

            if (StringUtils.isNullOrEmptyOrSpace(userId)) {
                map.put("ReturnType", "1002");
                map.put("Message", "用户不存在");
                return map;
            }
            //得到参数
            String channelId=(m.get("ChannelId")==null?null:m.get("ChannelId").toString());
            String contentId=(m.get("ContentId")==null?null:m.get("ContentId").toString());
            int loopSort=(m.get("LoopSort")==null?0:Integer.parseInt(m.get("LoopSort").toString()));
            if (StringUtils.isNullOrEmptyOrSpace(channelId) || StringUtils.isNullOrEmptyOrSpace(contentId) || loopSort==0) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }
            String mediaType=(m.get("MediaType")==null?null:m.get("MediaType").toString());

            boolean result=queryService.updateLoopSort(mediaType, channelId, contentId, loopSort);
            if (result) {
            	map.put("ReturnType", "1001");
                map.put("Message", "操作成功");
            } else {
            	map.put("ReturnType", "1005");
            	map.put("Message", "操作失败");
            }
            return map;
        } catch(Exception e) {
            e.printStackTrace();
            map.put("ReturnType", "T");
            map.put("TClass", e.getClass().getName());
            map.put("Message", StringUtils.getAllMessage(e));
            alPo.setDealFlag(2);
            return map;
        } finally {
            //数据收集处理=3
            alPo.setEndTime(new Timestamp(System.currentTimeMillis()));
            alPo.setReturnData(JsonUtils.objToJson(map));
            try {
                ApiGatherMemory.getInstance().put2Queue(alPo);
            } catch (InterruptedException e) {}
        }
    }
    
    @RequestMapping(value = "/content/delLoopImage.do")
    @ResponseBody
    public Map<String, Object> deleteLoopImgInChannel(HttpServletRequest request) {
        // 数据收集处理==1
        ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
        alPo.setApiName("6.4.4--/content/delLoopImage.do");
        alPo.setObjType("010");//内容发布
        alPo.setDealFlag(1);// 处理成功
        alPo.setOwnerType(201);
        alPo.setOwnerId("--");

        Map<String, Object> map=new HashMap<String, Object>();
        try {
            // 0-获取参数
            String userId=null; //用户判断用户权限，目前不起作用
            MobileUDKey mUdk=null;
            Map<String, Object> m=RequestUtils.getDataFromRequest(request);
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
                if (mUdk!=null) {
                    Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/delLoopImage");
                    if ((retM.get("ReturnType") + "").equals("2003")) {
                        map.put("ReturnType", "200");
                        map.put("Message", "需要登录");
                    } else {
                        map.putAll(retM);
                        if ((retM.get("ReturnType") + "").equals("1001")) map.remove("ReturnType");
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
                if (mUdk != null) alPo.setOwnerId(mUdk.getDeviceId());
                else alPo.setOwnerId("0");
            }
            if (mUdk != null) {
                alPo.setDeviceType(mUdk.getPCDType());
                alPo.setDeviceId(mUdk.getDeviceId());
            }
            if (m!=null) {
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

            if (StringUtils.isNullOrEmptyOrSpace(userId)) {
                map.put("ReturnType", "1002");
                map.put("Message", "用户不存在");
                return map;
            }
            //得到参数
            String channelId=(m.get("ChannelId")==null?null:m.get("ChannelId").toString());
            String contentId=(m.get("ContentId")==null?null:m.get("ContentId").toString());
            if (StringUtils.isNullOrEmptyOrSpace(channelId) || StringUtils.isNullOrEmptyOrSpace(contentId)) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }
            String mediaType=(m.get("MediaType")==null?null:m.get("MediaType").toString());

            boolean result=queryService.deleteLoopImg(mediaType, channelId, contentId);
            if (result) {
            	map.put("ReturnType", "1001");
                map.put("Message", "删除成功");
            } else {
            	map.put("ReturnType", "1005");
            	map.put("Message", "删除失败");
            }
            return map;
        } catch(Exception e) {
            e.printStackTrace();
            map.put("ReturnType", "T");
            map.put("TClass", e.getClass().getName());
            map.put("Message", StringUtils.getAllMessage(e));
            alPo.setDealFlag(2);
            return map;
        } finally {
            //数据收集处理=3
            alPo.setEndTime(new Timestamp(System.currentTimeMillis()));
            alPo.setReturnData(JsonUtils.objToJson(map));
            try {
                ApiGatherMemory.getInstance().put2Queue(alPo);
            } catch (InterruptedException e) {}
        }
    }
    
    @RequestMapping(value = "/content/addLoopImage.do")
    @ResponseBody
    public Map<String, Object> addLoopImgInChannel(HttpServletRequest request) {
        // 数据收集处理==1
        ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
        alPo.setApiName("6.4.1--/content/addLoopImage.do");
        alPo.setObjType("010");//内容发布
        alPo.setDealFlag(1);// 处理成功
        alPo.setOwnerType(201);
        alPo.setOwnerId("--");

        Map<String, Object> map=new HashMap<String, Object>();
        try {
            // 0-获取参数
            String userId=null; //用户判断用户权限，目前不起作用
            MobileUDKey mUdk=null;
            Map<String, Object> m=RequestUtils.getDataFromRequest(request);
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
                if (mUdk!=null) {
                    Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "content/addLoopImage");
                    if ((retM.get("ReturnType") + "").equals("2003")) {
                        map.put("ReturnType", "200");
                        map.put("Message", "需要登录");
                    } else {
                        map.putAll(retM);
                        if ((retM.get("ReturnType") + "").equals("1001")) map.remove("ReturnType");
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
                if (mUdk != null) alPo.setOwnerId(mUdk.getDeviceId());
                else alPo.setOwnerId("0");
            }
            if (mUdk != null) {
                alPo.setDeviceType(mUdk.getPCDType());
                alPo.setDeviceId(mUdk.getDeviceId());
            }
            if (m!=null) {
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

            if (StringUtils.isNullOrEmptyOrSpace(userId)) {
                map.put("ReturnType", "1002");
                map.put("Message", "用户不存在");
                return map;
            }
            //得到参数
            String channelId=(m.get("ChannelId")==null?null:m.get("ChannelId").toString());
            String contentId=(m.get("ContentId")==null?null:m.get("ContentId").toString());
            String imgeUrl=(m.get("ImgeUrl")==null?null:m.get("ImgeUrl").toString());
            if (StringUtils.isNullOrEmptyOrSpace(channelId) || StringUtils.isNullOrEmptyOrSpace(contentId) || StringUtils.isNullOrEmptyOrSpace(imgeUrl)) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }
            String mediaType=(m.get("MediaType")==null?null:m.get("MediaType").toString());
            int loopSort=(m.get("LoopSort")==null?0:Integer.valueOf(m.get("LoopSort").toString()));

            boolean result=queryService.addLoopImg(mediaType, channelId, contentId, imgeUrl, loopSort);
            if (result) {
            	map.put("ReturnType", "1001");
                map.put("Message", "添加成功");
            } else {
            	map.put("ReturnType", "1005");
            	map.put("Message", "添加失败");
            }
            return map;
        } catch(Exception e) {
            e.printStackTrace();
            map.put("ReturnType", "T");
            map.put("TClass", e.getClass().getName());
            map.put("Message", StringUtils.getAllMessage(e));
            alPo.setDealFlag(2);
            return map;
        } finally {
            //数据收集处理=3
            alPo.setEndTime(new Timestamp(System.currentTimeMillis()));
            alPo.setReturnData(JsonUtils.objToJson(map));
            try {
                ApiGatherMemory.getInstance().put2Queue(alPo);
            } catch (InterruptedException e) {}
        }
    }
}
