package com.woting.security.role.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
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
     * 添加角色
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
}
