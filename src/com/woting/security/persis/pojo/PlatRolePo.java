package com.woting.security.persis.pojo;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

/**
 * 角色对象
 * @author Administrator
 */
public class PlatRolePo extends BaseObject {
    private static final long serialVersionUID = -6703224612778088106L;

    private String id;
    private String roleName;//角色名称
    private int roleType;//角色分类  0=程序角色；1=配置角色
    private String desc;//角色描述
    private Timestamp cTime;//创建时间

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getRoleName() {
        return roleName;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    public int getRoleType() {
        return roleType;
    }
    public void setRoleType(int roleType) {
        this.roleType = roleType;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public Timestamp getcTime() {
        return cTime;
    }
    public void setcTime(Timestamp cTime) {
        this.cTime = cTime;
    }
}
