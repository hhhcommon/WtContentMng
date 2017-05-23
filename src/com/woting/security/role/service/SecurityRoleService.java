package com.woting.security.role.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.core.model.Page;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.WtContentMngConstants;
import com.woting.cm.core.channel.mem._CacheChannel;
import com.woting.cm.core.channel.model.Channel;
import com.woting.security.role.persis.pojo.PlatRolePo;
import com.woting.security.role.persis.pojo.RoleFunctionPo;
import com.woting.security.role.persis.pojo.UserFunctionPo;
import com.woting.security.role.persis.pojo.UserRolePo;

public class SecurityRoleService {
    @Resource(name="defaultDAO")
    private MybatisDAO<PlatRolePo> platRoleDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<RoleFunctionPo> roleFunctionDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<UserRolePo> userRoleDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<UserFunctionPo> userFunDao;

    @PostConstruct
    public void initParam() {
        platRoleDao.setNamespace("PLAT_ROLE");
        roleFunctionDao.setNamespace("PLAT_ROLE");
        userRoleDao.setNamespace("PLAT_ROLE");
        userFunDao.setNamespace("PLAT_ROLE");
    }

    /**
     * 添加角色
     * @param roleName 角色名
     * @param desc 角色说明
     */
    public boolean addRole(String roleName, String desc) {
        if (StringUtils.isNullOrEmptyOrSpace(roleName)) return false;
        Map<String, Object> param=new HashMap<String, Object>();
        String roleId=SequenceUUID.getPureUUID();
        param.put("id", roleId);
        param.put("roleName", roleName);
        if (!StringUtils.isNullOrEmptyOrSpace(desc)) {
            param.put("descn", desc);
        }
        Map<String, Object> _param=new HashMap<String, Object>();
        _param.put("id", SequenceUUID.getPureUUID());
        _param.put("roleId", roleId);
        _param.put("funName", "栏目权限");
        _param.put("funClass", "1");
        _param.put("funType", "Channel-Add");
        try {
            platRoleDao.insert("insert", param);
            roleFunctionDao.insert("insertRole", _param);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除角色
     * @param roleId 角色Id
     */
    public boolean delRole(String roleId) {
        if (StringUtils.isNullOrEmptyOrSpace(roleId)) return false;
        if (roleId.contains("，")) roleId.replaceAll("，", ",");
        String[] roleIdArr=roleId.split(",");
        List<String> roleIdList=new ArrayList<String>();
        for (String id : roleIdArr) {
            roleIdList.add(id);
        }
        if (roleIdList!=null && roleIdList.size()>0) {
            Map<String, Object> param=new HashMap<String, Object>();
            param.put("roleId", roleIdList);
            try {
                platRoleDao.delete("deleteRole", param);
                roleFunctionDao.delete("deleteRoleFun", param);
                userRoleDao.delete("deleteUserRole", param);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } 
        }
        return false;
    }
 
    /**
     * 获取角色列表
     * @param pageSize 每页需要的数量
     * @param page 页码
     * @return Map
     */
    public Map<String, Object> getRoleList(int pageSize, int page) {
        List<Map<String, Object>> _ret=null;
        int count=0;
        // 获取全部角色信息
        if (pageSize==0 || page==0) {
            _ret=platRoleDao.queryForListAutoTranform("getRoleList", null);
            count=platRoleDao.getCount("getRoleListCount", null);
        } else {
            // 默认页码
            if (pageSize==-1) {
                pageSize=10;
            }
            if (page==-1) {
                page=1;
            }
            Page<Map<String, Object>> pageList=platRoleDao.pageQueryAutoTranform("getRoleListCount", "getRoleList", null, page, pageSize);
            if (pageList!=null&&pageList.getDataCount()>0) {
                _ret=new ArrayList<Map<String, Object>>();
                _ret.addAll(pageList.getResult());
                count=pageList.getDataCount();
            }
        }
        if (_ret==null||_ret.isEmpty()) return null;

        // 组装数据
        List<Map<String, Object>> ret=new ArrayList<Map<String, Object>>(_ret.size());
        for (int i=0; i<_ret.size(); i++) {
            Map<String, Object> one=_ret.get(i);
            Map<String, Object> _one=new HashMap<String, Object>();
            _one.put("id", one.get("id"));
            _one.put("roleName", one.get("roleName"));
            _one.put("roleType", one.get("roleType"));
            _one.put("descn", one.get("descn"));
            ret.add(_one);
        }
        Map<String, Object> retM=new HashMap<String, Object>();
        retM.put("ResultList", ret);
        retM.put("AllCount", count);
        return retM;
    }

    /**
     * 设置角色功能
     * @param roleId 角色Id
     * @param funName 角色名
     * @param funClass 功能类型
     * @param funType 功能分类
     * @param objId 栏目Id
     * @param funFlag1 操作标识1
     * @param funFlag1 操作标识2
     * @return boolean
     */
    public boolean setRoleFun(String roleId, String funName, String funClass, String funType, String objId, String funFlag1, String funFlag2) {
        if (StringUtils.isNullOrEmptyOrSpace(roleId) || StringUtils.isNullOrEmptyOrSpace(funName)
                || StringUtils.isNullOrEmptyOrSpace(funClass) || StringUtils.isNullOrEmptyOrSpace(funType)) {
            return false;
        }
        Map<String, Object> param=new HashMap<String, Object>();
        param.put("roleId", roleId);
        param.put("funName", funName);
        param.put("funClass", funClass);
        param.put("funType", funType);
        if (!StringUtils.isNullOrEmptyOrSpace(objId)) {
            param.put("objId", objId);
        }
        if (!StringUtils.isNullOrEmptyOrSpace(funFlag1)) {
            int _funFlag1=Integer.valueOf(funFlag1);
            param.put("funFlag1", _funFlag1);
        } else {
            param.put("funFlag1", 1);
        }
        if (!StringUtils.isNullOrEmptyOrSpace(funFlag2)) {
            int _funFlag2=Integer.valueOf(funFlag2);
            param.put("funFlag2", _funFlag2);
        }
        try{
            roleFunctionDao.update("setRoleFun", param);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    /**
     * 获取角色功能
     * @param roleId 角色Id
     * @return 角色所有信息
     */
    public RoleFunctionPo getRoleFunlist(String roleId) {
        if (StringUtils.isNullOrEmptyOrSpace(roleId)) return null;
        
        Map<String, Object> param=new HashMap<String, Object>();
        param.put("roleId", roleId);
        try {
            List<Map<String, Object>> list=roleFunctionDao.queryForListAutoTranform("getRoleFun", param);
            if (list==null || list.size()<=0) return null;
            Map<String, Object> map=list.get(0);
            if (map==null || map.size()<=0) return null;
            RoleFunctionPo roleFunctionPo=new RoleFunctionPo();
            if (map.get("id")!=null && !map.get("id").toString().equals("") && !map.get("id").equals("null")) {
                roleFunctionPo.setId(map.get("id").toString());
            }
            if (map.get("roleId")!=null && !map.get("roleId").toString().equals("") && !map.get("roleId").equals("null")) {
                roleFunctionPo.setRoleId(map.get("roleId").toString());
            }
            if (map.get("funName")!=null && !map.get("funName").toString().equals("") && !map.get("funName").equals("null")) {
                roleFunctionPo.setFunName(map.get("funName").toString());
            }
            if (map.get("funClass")!=null && !map.get("funClass").toString().equals("") && !map.get("funClass").equals("null")) {
                roleFunctionPo.setFunClass(map.get("funClass").toString());
            }
            if (map.get("funType")!=null && !map.get("funType").toString().equals("") && !map.get("funType").equals("null")) {
                roleFunctionPo.setFunType(map.get("funType").toString());
            }
            if (map.get("objId")!=null && !map.get("objId").toString().equals("") && !map.get("objId").equals("null")) {
                roleFunctionPo.setObjId(map.get("objId").toString());
            }
            if (map.get("funFlag1")!=null && !map.get("funFlag1").toString().equals("") && !map.get("funFlag1").equals("null")) {
                roleFunctionPo.setFunFlag1(Integer.valueOf(map.get("funFlag1").toString()));
            }
            if (map.get("funFlag2")!=null && !map.get("funFlag2").toString().equals("") && !map.get("funFlag2").equals("null")) {
                roleFunctionPo.setFunFlag2(Integer.valueOf(map.get("funFlag2").toString()));
            }
            if (map.get("extInfo")!=null && !map.get("extInfo").toString().equals("") && !map.get("extInfo").equals("null")) {
                roleFunctionPo.setExtInfo(map.get("extInfo").toString());
            }
            if (map.get("cTime")!=null && !map.get("cTime").toString().equals("") && !map.get("cTime").equals("null")) {
                roleFunctionPo.setcTime(Timestamp.valueOf(map.get("cTime").toString()));
            }
            return roleFunctionPo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 设置用户角色
     * @param roleId 角色Id
     * @return boolean
     */
    public Map<String, Object> setUserRole(String userId, String roleId) {
        Map<String, Object> map=new HashMap<String, Object>();
        if (StringUtils.isNullOrEmptyOrSpace(roleId)) {
            map.put("ReturnType", "0000");
            map.put("Message", " 无法获取需要的参数");
            return map;
        }
        Map<String, Object> param=new HashMap<String, Object>();
        param.put("id", SequenceUUID.getPureUUID());
        param.put("userId", userId);
        param.put("roleId", roleId);
        int count=0;
        try {
            //查询是否存在此角色
            count=platRoleDao.queryForObjectAutoTranform("checkRoleById", param);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("ReturnType", "T");
            map.put("TClass", e.getClass().getName());
            map.put("Message", StringUtils.getAllMessage(e));
            return map;
        }
        if (count<=0) {//不存在此角色
            map.put("ReturnType", "1006");
            map.put("Message", "角色不存在");
            return map;
        } else count=0;
        try {
            //查询是否已经给用户设置过角色
            count=userRoleDao.queryForObjectAutoTranform("selectUserRoleCount", param);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("ReturnType", "T");
            map.put("TClass", e.getClass().getName());
            map.put("Message", StringUtils.getAllMessage(e));
            return map;
        }
        try {
            if (count<=0) {//需要先给用户添加角色
                userRoleDao.insert("insertUserRole", param);
            } else {//已经给用户设置过角色了  需要给用户修改角色
                userRoleDao.insert("updateUserRole", param);
            }
            map.put("ReturnType", "1001");
            map.put("Message", "设置成功");
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            map.put("ReturnType", "1005");
            map.put("Message", "设置失败");
            return map;
        }
    }

    /**
     * 设置角色
     * @param roleId 角色Id
     * @param roleName 角色名
     * @param desc 角色说明
     * @return boolean
     */
    public boolean updateRole(String roleId, String roleName, String desc) {
        if (StringUtils.isNullOrEmptyOrSpace(roleId)) return false;
        Map<String, Object> param=new HashMap<String, Object>();
        param.put("roleId", roleId);
        param.put("roleName", roleName);
        param.put("desc", desc);
        try {
            platRoleDao.update("updateRole", param);
            return  true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 修改用户权限
     * @param objId 权限Id 默认是栏目Id
     * @param funName 权限名  默认是栏目权限
     * @return Map<String, Object>
     */
    public Map<String, Object> updateUsersRole(String userId, String objId, String funName) {
        if (funName==null || funName.equals("") || funName.equals("栏目权限")) {//目前只支持栏目权限
            Map<String, Object> map=new HashMap<String, Object>();
            if (StringUtils.isNullOrEmptyOrSpace(userId) || StringUtils.isNullOrEmptyOrSpace(objId)) {
                map.put("ReturnType", "0000");
                map.put("Message", " 无法获取需要的参数");
                return map;
            }
            Map<String, Object> param=new HashMap<String, Object>();
            param.put("id", SequenceUUID.getPureUUID());
            param.put("userId", userId);
            param.put("objId", objId);
            int count=0;
            try {
                //查询是否已经为用户设置过权限
                count=userFunDao.queryForObjectAutoTranform("selectUserFun", param);
            } catch (Exception e) {
                e.printStackTrace();
                map.put("ReturnType", "T");
                map.put("TClass", e.getClass().getName());
                map.put("Message", StringUtils.getAllMessage(e));
                return map;
            }
            try {
                if (count<=0) {//没有未用户设置过权限
                    userFunDao.insert("insertUserFun", param);
                } else {//已经给用户设置过权限了  需要给用户修改未最新的设置的权限
                    userFunDao.update("updateUserFun", param);
                }
                map.put("ReturnType", "1001");
                map.put("Message", "设置成功");
                return map;
            } catch (Exception e) {
                e.printStackTrace();
                map.put("ReturnType", "1005");
                map.put("Message", "设置失败");
                return map;
            }
        } else {
            return null;
        }
    }

    /**
     * 获取用户的权限  目前只有栏目权限
     * @param userId 用户Id
     * @param funClass 权限类型
     * @param funName 操作名称  目前只有"栏目权限"
     * @return Map
     */
    public List<Map<String, Object>> getUserRole(String userId, String funClass, String funName) {
        if (StringUtils.isNullOrEmptyOrSpace(userId) || StringUtils.isNullOrEmptyOrSpace(funClass)) {
            return null;
        }
        if (funName==null || funName.equals("") || funName.equals("栏目权限")) {//目前只有栏目权限
            if (funClass!=null && funClass.equals("1")) {//==1 -> "Data" 数据权限
                Map<String, Object> param=new HashMap<String, Object>();
                param.put("userId", userId);
                //查询用户的角色
                Map<String, Object> ret=userRoleDao.queryForObjectAutoTranform("selectUserRole", param);
                if (ret==null || ret.size()<=0) return null;
                String roleId=ret.get("roleId")==null?null:ret.get("roleId").toString();
                if (roleId==null) return null;
                param=new HashMap<String, Object>();
                param.put("roleId", roleId);
                //查询角色的权限   此时用户的权限对应的就是角色的权限
                Map<String, Object> _ret=roleFunctionDao.queryForObjectAutoTranform("getRoleFun", param);
                if (_ret==null || _ret.size()<=0) return null;
                String objId=_ret.get("objId")==null?null:_ret.get("objId").toString();
                if (objId==null) return null;
                if (objId.contains("，")) objId=objId.replace("，", ",");
                String[] objIds=objId.split(",");
                Arrays.sort(objIds);
                Map<String, Object> map;
                List<Map<String, Object>> data=new ArrayList<Map<String, Object>>();
                _CacheChannel _cc=((CacheEle<_CacheChannel>)SystemCache.getCache(WtContentMngConstants.CACHE_CHANNEL)).getContent();
                TreeNode<Channel> root=_cc.channelTree;
                for (String id : objIds) {
                    if (!StringUtils.isNullOrEmptyOrSpace(id)) {
                        root=_cc.channelTreeMap.get(id);
                    }
                    if (root==null) return null;
                    map=new HashMap<String, Object>();
                    map.put("ChannelId", id);
                    String channelName=root.getTreePathName(null, 1);
                    map.put("ChannelName", channelName);
                    data.add(map);
                }
                return data;
            } else {//==2 -> 模块(界面)权限           ==3 -> 操作权限
                return null;
            }
        } else {//栏目权限之外的权限
            return null;
        }
    }
}
