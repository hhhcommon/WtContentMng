package com.woting.security.role.web;

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
import com.woting.security.role.service.SecurityRoleService;

/**
 * 公众用户权限管理
 * @author Administrator
 */
@Controller
public class RoleController {
    @Resource
    private SecurityRoleService roleService;
    @Resource(name = "redisSessionService")
    private SessionService sessionService;

    @RequestMapping(value = "/security/addRole.do")
    @ResponseBody
    public Map<String, Object> addRoleInPlat(HttpServletRequest request) {
        // 数据收集处理==1
        ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
        alPo.setApiName("9.2.1--/security/addRole.do");
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
                    Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "security/addRole");
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
            String roleName=(m.get("RoleName")==null?null:m.get("RoleName").toString());
            if (StringUtils.isNullOrEmptyOrSpace(roleName)) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }
            String desc=(m.get("Desc")==null?null:m.get("Desc").toString());

            boolean result=roleService.addRole(roleName, desc);
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
    
    @RequestMapping(value = "/security/delRole.do")
    @ResponseBody
    public Map<String, Object> delRoleInPlat(HttpServletRequest request) {
        // 数据收集处理==1
        ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
        alPo.setApiName("9.2.2--security/delRole.do");
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
                    Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "security/delRole");
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
            String roleId=(m.get("RoleId")==null?null:m.get("RoleId").toString());
            if (StringUtils.isNullOrEmptyOrSpace(roleId)) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }

            boolean result=roleService.delRole(roleId);
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
}
