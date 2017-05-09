package com.woting.security.role.persis.pojo;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

/**
 * 用户角色
 * @author Administrator
 */
public class UserRolePo extends BaseObject {
    private static final long serialVersionUID = -538136172960427676L;

    private String id;//id 主键
    private String userId;//用户Id
    private String roleId;//角色Id
    private Timestamp cTime;//操作时间
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getRoleId() {
        return roleId;
    }
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
    public Timestamp getcTime() {
        return cTime;
    }
    public void setcTime(Timestamp cTime) {
        this.cTime = cTime;
    }
}
