package com.woting.security.role.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.core.model.Page;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.security.role.persis.pojo.PlatRolePo;
import com.woting.security.role.persis.pojo.RoleFunctionPo;
import com.woting.security.role.persis.pojo.UserRolePo;

public class SecurityRoleService {
    @Resource(name="defaultDAO")
    private MybatisDAO<PlatRolePo> platRoleDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<RoleFunctionPo> roleFunctionDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<UserRolePo> userRoleDao;

    @PostConstruct
    public void initParam() {
        platRoleDao.setNamespace("PLAT_ROLE");
        roleFunctionDao.setNamespace("PLAT_ROLE");
        userRoleDao.setNamespace("PLAT_ROLE");
    }

    /**
     * 添加角色
     * @param roleName 角色名
     * @param desc 角色说明
     */
    public boolean addRole(String roleName, String desc) {
        if (StringUtils.isNullOrEmptyOrSpace(roleName)) return false;
        Map<String, Object> param=new HashMap<String, Object>();
        param.put("id", SequenceUUID.getPureUUID());
        param.put("roleName", roleName);
        if (!StringUtils.isNullOrEmptyOrSpace(desc)) {
            param.put("descn", desc);
        }
        try {
            platRoleDao.insert("insert", param);
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
        Map<String, Object> param=new HashMap<String, Object>();
        param.put("roleId", roleId);
        try {
            platRoleDao.delete("deleteRole", param);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
            platRoleDao.update("setRoleFun", param);
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
            if (map.get("roleId")!=null && !map.get("roleId").toString().equals("")) {
                roleFunctionPo.setRoleId(map.get("roleId").toString());
            }
            if (map.get("funName")!=null && !map.get("funName").toString().equals("")) {
                roleFunctionPo.setFunName(map.get("funName").toString());
            }
            if (map.get("funClass")!=null && !map.get("funClass").toString().equals("")) {
                roleFunctionPo.setFunClass(map.get("funClass").toString());
            }
            if (map.get("funType")!=null && !map.get("funType").toString().equals("")) {
                roleFunctionPo.setFunType(map.get("funType").toString());
            }
            if (map.get("objId")!=null && !map.get("objId").toString().equals("")) {
                roleFunctionPo.setObjId(map.get("objId").toString());
            }
            if (map.get("funFlag1")!=null && !map.get("funFlag1").toString().equals("")) {
                roleFunctionPo.setFunFlag1(Integer.valueOf(map.get("funFlag1").toString()));
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
            count=userRoleDao.queryForObjectAutoTranform("selectUserRole", param);
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
}
