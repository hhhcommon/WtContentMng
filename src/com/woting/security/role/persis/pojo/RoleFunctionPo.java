package com.woting.security.role.persis.pojo;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

/**
 * 角色操作对象
 * @author Administrator
 */
public class RoleFunctionPo extends BaseObject {
    private static final long serialVersionUID = -3849750445756566780L;

    private String id;
    private String roleId;//角色表Id
    private String funName;//此字段有一定的规则，不可随意填写  目前只有“栏目权限”
    private String funClass;//功能类型  目前有：“Data”:数据权限       “Module”:模块(界面)权限         “Oper”:操作权限
    private String funType;//功能分类  若是数据权限，此字段才有意义，目前有:Channel-Add=栏目中添加内容
    private String objId;//对象Id 若是数据权限，并且funType=Channel-Add，目前是栏目的Id;若是Module，这里是Module表中的结点Id
    private int funFlag1;//操作标识
    private int funFlag2;//操作标识
    private String extInfo;
    private Timestamp cTime;//创建时间

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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
    public String getObjId() {
        return objId;
    }
    public void setObjId(String objId) {
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
