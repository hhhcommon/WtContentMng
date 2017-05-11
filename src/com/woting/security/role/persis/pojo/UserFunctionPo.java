package com.woting.security.role.persis.pojo;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

/**
 * 用户、角色与功能关系数据
 * @author Administrator
 */
public class UserFunctionPo extends BaseObject {
    private static final long serialVersionUID = -3610757093284156025L;

    private String id;
    private String userId;//用户Id
    private int roleType;//角色分类：0=程序角色；1=配置角色
    private String roleId;
    private String funName;//此字段有一定的规则，不可随意填写,目前只有“栏目权限”
    private String funClass;//功能类型目前有：Data=数据权限;Module=模块(界面)权限;Oper=操作权限',
    private String funType;//可以有很多要和roleClass配置使用，目前有:Channel-Add=栏目中添加内容。若是Module，这里是Module表中的结点Id'
    private int objId;
    private int funFlag1;//操作标识1：对于“栏目权限”1是包括2是不包括
    private int funFlag2;//另一个标识
    private String extInfo;
    private Timestamp cTime;//邀请成功的时间

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
    public int getRoleType() {
        return roleType;
    }
    public void setRoleType(int roleType) {
        this.roleType = roleType;
    }
    public String getRoleId() {
        return roleId;
    }
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
    public String getFunName() {
        return funName;
    }
    public void setFunName(String funName) {
        this.funName = funName;
    }
    public String getFunClass() {
        return funClass;
    }
    public void setFunClass(String funClass) {
        this.funClass = funClass;
    }
    public String getFunType() {
        return funType;
    }
    public void setFunType(String funType) {
        this.funType = funType;
    }
    public int getObjId() {
        return objId;
    }
    public void setObjId(int objId) {
        this.objId = objId;
    }
    public int getFunFlag1() {
        return funFlag1;
    }
    public void setFunFlag1(int funFlag1) {
        this.funFlag1 = funFlag1;
    }
    public int getFunFlag2() {
        return funFlag2;
    }
    public void setFunFlag2(int funFlag2) {
        this.funFlag2 = funFlag2;
    }
    public String getExtInfo() {
        return extInfo;
    }
    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }
    public Timestamp getcTime() {
        return cTime;
    }
    public void setcTime(Timestamp cTime) {
        this.cTime = cTime;
    }
}
