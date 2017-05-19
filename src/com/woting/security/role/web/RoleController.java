package com.woting.security.role.web;

import java.sql.Timestamp;
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
import com.woting.dataanal.gather.API.ApiGatherUtils;
import com.woting.dataanal.gather.API.mem.ApiGatherMemory;
import com.woting.dataanal.gather.API.persis.pojo.ApiLogPo;
import com.woting.passport.mobile.MobileParam;
import com.woting.passport.mobile.MobileUDKey;
import com.woting.passport.session.DeviceType;
import com.woting.passport.session.SessionService;
import com.woting.security.role.persis.pojo.RoleFunctionPo;
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

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/security/getRoleList.do")
    @ResponseBody
    public Map<String, Object> getRoleListInPlat(HttpServletRequest request) {
        // 数据收集处理==1
        ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
        alPo.setApiName("9.2.3--security/getRoleList.do");
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
                    Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "security/getRoleList");
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
            String pageSize=(m.get("PageSize")==null?null:m.get("PageSize").toString());
            String page=(m.get("Page")==null?null:m.get("Page").toString());

            int _pageSize=-1;
            if (!StringUtils.isNullOrEmptyOrSpace(pageSize)) {
                _pageSize=Integer.valueOf(pageSize);
            }
            int _page=-1;
            if (!StringUtils.isNullOrEmptyOrSpace(page)) {
                _page=Integer.valueOf(page);
            }
            
            Map<String, Object> roleList=roleService.getRoleList(_pageSize, _page);
            if (roleList==null || roleList.size()<=0) {
                map.put("ReturnType", "1011");
                map.put("Message", "无内容");
                return map;
            }
            List<Map<String, Object>> resultList=(List<Map<String, Object>>) roleList.get("ResultList");
            if (resultList==null || resultList.isEmpty()) {
                map.put("ReturnType", "1011");
                map.put("Message", "无内容");
            } else {
                int count=(int)roleList.get("AllCount");
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

    @RequestMapping(value = "/security/setRoleFun.do")
    @ResponseBody
    public Map<String, Object> setRoleFunInPlat(HttpServletRequest request) {
        // 数据收集处理==1
        ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
        alPo.setApiName("9.2.4--security/setRoleFun.do");
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
                    Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "security/setRoleFun");
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
            //得到参数  必须有的参数
            String roleId=(m.get("RoleId")==null?null:m.get("RoleId").toString());
            String funName=(m.get("FunName")==null?null:m.get("FunName").toString());
            String funClass=(m.get("FunClass")==null?null:m.get("FunClass").toString());
            String funType=(m.get("FunType")==null?null:m.get("FunType").toString());

            if (StringUtils.isNullOrEmptyOrSpace(roleId) || StringUtils.isNullOrEmptyOrSpace(funName)
                    || StringUtils.isNullOrEmptyOrSpace(funClass) || StringUtils.isNullOrEmptyOrSpace(funType)) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }

            //得到参数  可选参数
            String objId=(m.get("ObjId")==null?null:m.get("ObjId").toString());
            String funFlag1=(m.get("FunFlag1")==null?null:m.get("FunFlag1").toString());
            String funFlag2=(m.get("FunFlag2")==null?null:m.get("FunFlag2").toString());

            boolean result=roleService.setRoleFun(roleId, funName, funClass, funType, objId, funFlag1, funFlag2);
            if (result) {
                map.put("ReturnType", "1001");
                map.put("Message", "设置成功");
            } else {
                map.put("ReturnType", "1005");
                map.put("Message", "设置失败");
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

    @RequestMapping(value = "/security/getRoleFunlist.do")
    @ResponseBody
    public Map<String, Object> getRoleFunlist(HttpServletRequest request) {
        // 数据收集处理==1
        ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
        alPo.setApiName("9.2.5--security/getRoleFunlist.do");
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
                    Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "security/getRoleFunlist");
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
            //得到参数  必须有的参数
            String roleId=(m.get("RoleId")==null?null:m.get("RoleId").toString());
            if (StringUtils.isNullOrEmptyOrSpace(roleId)) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }
            RoleFunctionPo result=roleService.getRoleFunlist(roleId);
            if (result==null) {
                map.put("ReturnType", "1011");
                map.put("Message", "无内容");
            } else {
                map.put("ReturnType", "1001");
                map.put("Message", "获取成功");
                map.put("RoleFun", result);
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

    @RequestMapping(value = "/security/setUserRole.do")
    @ResponseBody
    public Map<String, Object> setUserRole(HttpServletRequest request) {
        // 数据收集处理==1
        ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
        alPo.setApiName("9.2.6--security/setUserRole.do");
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
                    Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "security/setUserRole");
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
            //得到参数  必须有的参数
            String roleId=(m.get("RoleId")==null?null:m.get("RoleId").toString());
            if (StringUtils.isNullOrEmptyOrSpace(roleId)) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }
            map=roleService.setUserRole(userId, roleId);
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

    @RequestMapping(value = "/security/updateRole.do")
    @ResponseBody
    public Map<String, Object> updateRole(HttpServletRequest request) {
        // 数据收集处理==1
        ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
        alPo.setApiName("9.2.7--security/updateRole.do");
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
                    Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "security/updateRole");
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
            //得到参数  必须有的参数
            String roleId=(m.get("RoleId")==null?null:m.get("RoleId").toString());
            if (StringUtils.isNullOrEmptyOrSpace(roleId)) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }
            String roleName=(m.get("RoleName")==null?null:m.get("RoleName").toString());
            String desc=(m.get("Desc")==null?null:m.get("Desc").toString());
            boolean result=roleService.updateRole(roleId, roleName, desc);
            if (result) {
                map.put("ReturnType", "1001");
                map.put("Message", "设置成功");
            } else {
                map.put("ReturnType", "1005");
                map.put("Message", "设置失败");
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

    @RequestMapping(value = "/security/getUserRole.do")
    @ResponseBody
    public Map<String, Object> getUserRole(HttpServletRequest request) {
        // 数据收集处理==1
        ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
        alPo.setApiName("9.1.6--/security/getUserRole.do");
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
                    Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "security/getUserRole");
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
            String funClass=(m.get("FunClass")==null?null:m.get("FunClass").toString());
            if (StringUtils.isNullOrEmptyOrSpace(funClass)) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }
            String funName=(m.get("FunName")==null?null:m.get("FunName").toString());
            List<Map<String, Object>> resultList=roleService.getUserRole(userId, funClass, funName);
            if (resultList==null || resultList.size()<=0) {
                map.put("ReturnType", "1011");
                map.put("Message", "无内容");
            } else {
                map.put("ReturnType", "1001");
                map.put("ResultList", resultList);
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

    @RequestMapping(value = "/security/updateUsersRole.do")
    @ResponseBody
    public Map<String, Object> updateUsersRole(HttpServletRequest request) {
        // 数据收集处理==1
        ApiLogPo alPo = ApiGatherUtils.buildApiLogDataFromRequest(request);
        alPo.setApiName("9.1.7--/security/updateUsersRole.do");
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
                    Map<String, Object> retM = sessionService.dealUDkeyEntry(mUdk, "security/updateUsersRole");
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
            String objId=(m.get("ObjId")==null?null:m.get("ObjId").toString());
            if (StringUtils.isNullOrEmptyOrSpace(objId)) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
                return map;
            }
            String funName=(m.get("FunName")==null?null:m.get("FunName").toString());
            map=roleService.updateUsersRole(userId, objId, funName);
            if (map==null || map.size()<=0) {
                map.put("ReturnType", "1011");
                map.put("Message", "无内容");
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
