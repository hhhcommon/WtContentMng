package com.woting.passport.UGA.persis.pojo;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.UGA.UgaUser;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.StringUtils;
import com.woting.cm.core.dict.model.DictRefRes;
import com.woting.cm.core.dict.service.DictService;

public class UserPo extends UgaUser {
    private static final long serialVersionUID = 400373602903981461L;

    private String nickName; //昵称
    private String userNum; //用户号，用于公开的号码
    private String userSign; //用户签名
    private Timestamp birthday; //生日
    private String starSign; //星座
    private String mainPhoneNum; //用户主手机号码，用户可能有多个手机号码
    private int phoneNumIsPub; //是否允许手机号码搜索或公布手机号码:0不公开；1公开
    private String mailAddress; //用户邮箱
    private int userType; //用户分类：对应OwnerType，1xx::系统:100-我们自己的系统(cm/crawl/push等);101-其他系统(wt_Organize表中的Id);2xx::用户:200-后台系统用户;201-前端用户-wt_Member表中的用户Id
    private int userClass; //用户类型，现在还没有用，比如是一般用户还是管理原等
    private int userState;//用户状态，0-2,0代表未激活的用户，1代表已激用户，2代表失效用户,3根据邮箱找密码的用户
    private int RUserType;//注册用户分类：0一般用户；1认证用户；2专业用户
    private int AUserType;//管理用户分类：0没有任何管理权限；1一般管理员；2审核管理员
    private String portraitBig;//用户头像大
    private String portraitMini;//用户头像小
    private String homepage; //用户主页
    private String descn; //用户描述
    private Timestamp CTime; //记录创建时间
    private Timestamp lmTime; //最后修改时间:last modify time

    public String getUserNum() {
        return userNum;
    }
    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }
    public String getMainPhoneNum() {
        return mainPhoneNum;
    }
    public void setMainPhoneNum(String mainPhoneNum) {
        this.mainPhoneNum = mainPhoneNum;
    }
    public String getMailAddress() {
        return mailAddress;
    }
    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }
    public int getUserType() {
        return userType;
    }
    public void setUserType(int userType) {
        this.userType = userType;
    }
    public int getUserClass() {
        return userClass;
    }
    public void setUserClass(int userClass) {
        this.userClass = userClass;
    }
    public int getUserState() {
        return userState;
    }
    public void setUserState(int userState) {
        this.userState = userState;
    }
    public int getRUserType() {
        return RUserType;
    }
    public void setRUserType(int rUserType) {
        RUserType=rUserType;
    }
    public int getAUserType() {
        return AUserType;
    }
    public void setAUserType(int aUserType) {
        AUserType=aUserType;
    }
    public String getPortraitBig() {
        return portraitBig;
    }
    public void setPortraitBig(String portraitBig) {
        this.portraitBig = portraitBig;
    }
    public String getPortraitMini() {
        return portraitMini;
    }
    public void setPortraitMini(String portraitMini) {
        this.portraitMini = portraitMini;
    }
    public String getDescn() {
        return descn;
    }
    public void setDescn(String descn) {
        this.descn = descn;
    }
    public String getHomepage() {
        return homepage;
    }
    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp cTime) {
        CTime = cTime;
    }
    public Timestamp getLmTime() {
        return lmTime;
    }
    public void setLmTime(Timestamp lmTime) {
        this.lmTime = lmTime;
    }
    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getUserSign() {
        return userSign;
    }
    public void setUserSign(String userSign) {
        this.userSign = userSign;
    }
    public Timestamp getBirthday() {
        return birthday;
    }
    public void setBirthday(Timestamp birthday) {
        this.birthday = birthday;
    }
    public String getStarSign() {
        return starSign;
    }
    public void setStarSign(String starSign) {
        this.starSign = starSign;
    }
    public void setPhoneNumIsPub(boolean isPub) {
        this.phoneNumIsPub=isPub?1:0;
    }
    public void setPhoneNumIsPub(int phoneNumIsPub) {
        this.phoneNumIsPub = phoneNumIsPub;
    }
    public boolean isPubPhoneNum() {
        return phoneNumIsPub==1;
    }
    public int getPhoneNumIsPub() {
        return phoneNumIsPub;
    }

    public Map<String, Object> toHashMap4Mobile() {
        Map<String, Object> retM = new HashMap<String, Object>();
        if (!StringUtils.isNullOrEmptyOrSpace(this.userId)) retM.put("UserId", this.userId);
        if (!StringUtils.isNullOrEmptyOrSpace(this.userName)) retM.put("RealName", this.userName);
        if (!StringUtils.isNullOrEmptyOrSpace(this.userNum)) retM.put("UserNum", this.userNum);
        if (!StringUtils.isNullOrEmptyOrSpace(this.userSign)) retM.put("UserSign", this.userSign);
        if (!StringUtils.isNullOrEmptyOrSpace(this.loginName)) retM.put("UserName", this.loginName);
        if (!StringUtils.isNullOrEmptyOrSpace(this.nickName)) retM.put("NickName", this.nickName);
        if (!StringUtils.isNullOrEmptyOrSpace(this.mainPhoneNum)&&this.isPubPhoneNum()) retM.put("PhoneNum", this.mainPhoneNum);
        if (!StringUtils.isNullOrEmptyOrSpace(this.descn)) retM.put("Descn", this.descn);
        if (!StringUtils.isNullOrEmptyOrSpace(this.portraitBig)) retM.put("Portrait", this.portraitBig);
//        if (!StringUtils.isNullOrEmptyOrSpace(this.portraitMini)) retM.put("PortraitMini", this.portraitMini);
        DictService dictService=null;
        ServletContext sc=(SystemCache.getCache(FConstants.SERVLET_CONTEXT)==null?null:(ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent());
        if (WebApplicationContextUtils.getWebApplicationContext(sc)!=null) {
            dictService=(DictService) WebApplicationContextUtils.getWebApplicationContext(sc).getBean("dictService");
        }
        if (dictService!=null) {
            List<DictRefRes> dictRefList=dictService.getDictRefs("plat_User", this.userId);
            if (dictRefList!=null&&!dictRefList.isEmpty()) {
                for (DictRefRes drr: dictRefList) {
                    if (drr.getDm().getId().equals("8")) {//性别
                        retM.put("Sex", drr.getDd().getNodeName());
                    } else
                    if (drr.getDm().getId().equals("2")&&drr.getRefName().equals("地区")) {
                        retM.put("Region", drr.getDd().getTreePathName());
                    }
                }
            }
        }
        return retM;
    }

    /**
     * 转换为详细信息
     * @return 详细信息内容
     */
    public Map<String, Object> getDetailInfo() {
        Map<String, Object> retM = new HashMap<String, Object>();
        if (!StringUtils.isNullOrEmptyOrSpace(this.userId)) retM.put("UserId", this.userId);
        if (!StringUtils.isNullOrEmptyOrSpace(this.userName)) retM.put("RealName", this.userName);
        if (!StringUtils.isNullOrEmptyOrSpace(this.userNum)) retM.put("UserNum", this.userNum);
        if (!StringUtils.isNullOrEmptyOrSpace(this.userSign)) retM.put("UserSign", this.userSign);
        if (!StringUtils.isNullOrEmptyOrSpace(this.loginName)) retM.put("UserName", this.loginName);
        if (!StringUtils.isNullOrEmptyOrSpace(this.nickName)) retM.put("NickName", this.nickName);
        DictService dictService=null;
        ServletContext sc=(SystemCache.getCache(FConstants.SERVLET_CONTEXT)==null?null:(ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent());
        if (WebApplicationContextUtils.getWebApplicationContext(sc)!=null) {
            dictService=(DictService) WebApplicationContextUtils.getWebApplicationContext(sc).getBean("dictService");
        }
        if (dictService!=null) {
            List<DictRefRes> dictRefList=dictService.getDictRefs("plat_User", this.userId);
            if (dictRefList!=null&&!dictRefList.isEmpty()) {
                for (DictRefRes drr: dictRefList) {
                    if (drr.getDm().getId().equals("8")) {//性别
                        retM.put("Sex", drr.getDd().getNodeName());
                    } else
                    if (drr.getDm().getId().equals("2")&&drr.getRefName().equals("地区")) {
                        retM.put("Region", drr.getDd().getTreePathName());
                    }
                }
            }
        }
        if (this.birthday!=null) {
            retM.put("Birthday", this.birthday.getTime());
            retM.put("Age", getAge());
        }
        retM.put("RUserType", this.RUserType);
        retM.put("AUserType", this.AUserType);
        if (!StringUtils.isNullOrEmptyOrSpace(this.starSign)) retM.put("StarSign", this.starSign);
        if (!StringUtils.isNullOrEmptyOrSpace(this.mainPhoneNum)) retM.put("PhoneNum", this.mainPhoneNum);
        if (!StringUtils.isNullOrEmptyOrSpace(this.mailAddress)) retM.put("Email", this.mailAddress);
        retM.put("PhoneNumIsPub", this.phoneNumIsPub);
        if (!StringUtils.isNullOrEmptyOrSpace(this.portraitBig)) retM.put("Portrait", this.portraitBig);
//        if (!StringUtils.isNullOrEmptyOrSpace(this.portraitMini)) retM.put("PortraitMini", this.portraitMini);
        if (!StringUtils.isNullOrEmptyOrSpace(this.descn)) retM.put("Descn", this.descn);
        return retM;
    }

    private int getAge() {
        int age=0;
        Calendar born=Calendar.getInstance();
        Calendar now=Calendar.getInstance();
        now.setTime(new Date());
        born.setTimeInMillis(this.birthday.getTime());
        if (born.after(now)) throw new IllegalArgumentException("生日不能大于今日");
        age=now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
        if (now.get(Calendar.DAY_OF_YEAR) < born.get(Calendar.DAY_OF_YEAR)) age -= 1;
        return age;
    } 
}