package com.woting.security.approve.web;

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
import com.woting.dataanal.gather.API.ApiGatherUtils;
import com.woting.dataanal.gather.API.mem.ApiGatherMemory;
import com.woting.dataanal.gather.API.persis.pojo.ApiLogPo;
import com.woting.passport.mobile.MobileParam;
import com.woting.passport.mobile.MobileUDKey;
import com.woting.passport.session.DeviceType;
import com.woting.passport.session.SessionService;
import com.woting.security.approve.persis.pojo.PlatUserProgressPo;
import com.woting.security.approve.service.ApproveRoleService;

/**
 * 用户认证
 * @author Administrator
 */
@Controller
public class ApproveController {
    @Resource
    private ApproveRoleService approveService;
    @Resource(name = "redisSessionService")
    private SessionService sessionService;

    @RequestMapping(value = "/security/approveRole.do")
    @ResponseBody
    public Map<String, Object> addRoleInPlat(HttpServletRequest request) {
        // 数据收集处理==1
        ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
        alPo.setApiName("9.1.1--/security/approveRole.do");
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
                    Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "security/approveRole");
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
            String iDCard=(m.get("IDCard")==null?null:m.get("IDCard").toString());
            String frontImg=(m.get("FrontImg")==null?null:m.get("FrontImg").toString());
            String reverseImg=(m.get("ReverseImg")==null?null:m.get("ReverseImg").toString());
            String mixImg=(m.get("MixImg")==null?null:m.get("MixImg").toString());
            String applyRoleId=(m.get("ApplyRoleId")==null?null:m.get("ApplyRoleId").toString());
            if (StringUtils.isNullOrEmptyOrSpace(iDCard) || StringUtils.isNullOrEmptyOrSpace(frontImg)
                    || StringUtils.isNullOrEmptyOrSpace(reverseImg) || StringUtils.isNullOrEmptyOrSpace(mixImg) || StringUtils.isNullOrEmptyOrSpace(applyRoleId)) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }
            String anchorCardImg=(m.get("AnchorCardImg")==null?null:m.get("AnchorCardImg").toString());
            String applyDescn=(m.get("ApplyDescn")==null?null:m.get("ApplyDescn").toString());

            map=approveService.approveRole(userId, iDCard, frontImg, reverseImg, mixImg, anchorCardImg, applyDescn, applyRoleId);
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

    @RequestMapping(value = "/security/getApproveProgress.do")
    @ResponseBody
    public Map<String, Object> getUserApproveProgress(HttpServletRequest request) {
        // 数据收集处理==1
        ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
        alPo.setApiName("9.1.2--/security/getApproveProgress.do");
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
                    Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "security/getApproveProgress");
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
            PlatUserProgressPo p=approveService.getUserApproveProgress(userId);
            if (p==null) {
                map.put("ReturnType", "1011");
                map.put("Message", "无内容");
            } else {
                map.put("ReturnType", "1001");
                map.put("Message", "获取用户认证信息成功");
                map.put("approveInfo", p);
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
