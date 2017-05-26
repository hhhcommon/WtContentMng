package com.woting.security.approve.persis.pojo;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

public class ApproveInfoPo extends BaseObject {
    private static final long serialVersionUID = -4820912311238643535L;

    private String userId;//用户Id
    private String checkerId;//审核者Id
    private String applyRoleId;//用户申请认证Id
    private int reStatu;//认证流程  0=待处理；1=通过；2=未通过；
    private String reDescn;//审核回复意见
    private Timestamp ModifyTime;//审核操作时间
    
    private String iDCard;//用户认证时身份证号码
    private String reallyName;//用户真实姓名
    private String frontImg;//身份证正面图片地址
    private String reverseImg;//身份证反面图片地址
    private String mixImg;//复合型图片地址
    private String anchorCardImg;//专业认证证件图片地址
    public String getUserId() {
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
    public int getReStatu() {
        return reStatu;
    }
    public void setReStatu(int reStatu) {
        this.reStatu = reStatu;
    }
    public String getReDescn() {
        return reDescn;
    }
    public void setReDescn(String reDescn) {
        this.reDescn = reDescn;
    }
    public Timestamp getModifyTime() {
        return ModifyTime;
    }
    public void setModifyTime(Timestamp modifyTime) {
        ModifyTime = modifyTime;
    }
    public String getiDCard() {
        return iDCard;
    }
    public void setiDCard(String iDCard) {
        this.iDCard = iDCard;
    }
    public String getReallyName() {
        return reallyName;
    }
    public void setReallyName(String reallyName) {
        this.reallyName = reallyName;
    }
    public String getFrontImg() {
        return frontImg;
    }
    public void setFrontImg(String frontImg) {
        this.frontImg = frontImg;
    }
    public String getReverseImg() {
        return reverseImg;
    }
    public void setReverseImg(String reverseImg) {
        this.reverseImg = reverseImg;
    }
    public String getMixImg() {
        return mixImg;
    }
    public void setMixImg(String mixImg) {
        this.mixImg = mixImg;
    }
    public String getAnchorCardImg() {
        return anchorCardImg;
    }
    public void setAnchorCardImg(String anchorCardImg) {
        this.anchorCardImg = anchorCardImg;
    }
}
