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

public class SecurityRoleService {
    @Resource(name="defaultDAO")
    private MybatisDAO<PlatRolePo> platRoleDao;

    @PostConstruct
    public void initParam() {
        platRoleDao.setNamespace("PLAT_ROLE");
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
}
