package com.woting.security.persis.pojo;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

/**
 * 用户扩展表
 * @author Administrator
 */
public class PlatUserExtPo  extends BaseObject {
    private static final long serialVersionUID = -1214593585468646163L;

    private String userid;//用户Id
    private String iDCard;//用户认证时身份证号码
    private String frontImg;//身份证正面图片地址
    private String reverseImg;//身份证反面图片地址
    private String mixImg;//复合型图片地址
    private String anchorCardImg;//专业认证证件图片地址
    private Timestamp cTime;//创建时间
    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }
    public String getiDCard() {
        return iDCard;
    }
    public void setiDCard(String iDCard) {
        this.iDCard = iDCard;
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
    public Timestamp getcTime() {
        return cTime;
    }
    public void setcTime(Timestamp cTime) {
        this.cTime = cTime;
    }
}
