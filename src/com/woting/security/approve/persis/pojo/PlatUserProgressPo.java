package com.woting.security.approve.persis.pojo;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

/**
 * 用户发起主播认证流程
 * @author Administrator
 */
public class PlatUserProgressPo extends BaseObject {
    private static final long serialVersionUID = -9192919644399640035L;

    private String Id;//UUID
    private String userid;//用户Id
    private String checkerId;//审核者Id
    private String applyRoleId;//用户申请认证Id
    private int reStatus;//认证流程  0=待处理；1=通过；2=未通过；
    private String applyDescn;//主播认证请求意见
    private String reDescn;//审核回复意见
    private Timestamp cTime;//创建时间
    private Timestamp ModifyTime;//审核操作时间
    public String getId() {
        return Id;
    }
    public void setId(String id) {
        Id = id;
    }
    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
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
    public int getReStatus() {
        return reStatus;
    }
    public void setReStatus(int reStatus) {
        this.reStatus = reStatus;
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
        return ModifyTime;
    }
    public void setModifyTime(Timestamp modifyTime) {
        ModifyTime = modifyTime;
    }
}
