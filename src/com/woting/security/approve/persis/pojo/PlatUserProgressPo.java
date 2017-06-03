package com.woting.security.approve.persis.pojo;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

/**
 * 用户发起主播认证流程
 * @author Administrator
 */
public class PlatUserProgressPo extends BaseObject {
    private static final long serialVersionUID = -9192919644399640035L;

    private String id;//UUID
    private String userId;//用户Id
    private String checkerId;//审核者Id
    private String applyRoleId;//用户申请认证Id
    private int reState;//认证流程  0=待处理；1=通过；2=未通过；
    private String applyDescn;//主播认证请求意见
    private String reDescn;//审核回复意见
    private Timestamp cTime;//创建时间
    private Timestamp modifyTime;//审核操作时间
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUserid() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getCheckerId() {
        return checkerId;
    }
    public void setCheckerId(String checkerId) {
        this.checkerId = checkerId;
    }
    public String getApplyRoleId() {
        return applyRoleId;
    }
    public void setApplyRoleId(String applyRoleId) {
        this.applyRoleId = applyRoleId;
    }
    public int getReState() {
        return reState;
    }
    public void setReState(int reState) {
        this.reState = reState;
    }
    public String getApplyDescn() {
        return applyDescn;
    }
    public void setApplyDescn(String applyDescn) {
        this.applyDescn = applyDescn;
    }
    public String getReDescn() {
        return reDescn;
    }
    public void setReDescn(String reDescn) {
        this.reDescn = reDescn;
    }
    public Timestamp getcTime() {
        return cTime;
    }
    public void setcTime(Timestamp cTime) {
        this.cTime = cTime;
    }
    public Timestamp getModifyTime() {
        return modifyTime;
    }
    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
    }
}
