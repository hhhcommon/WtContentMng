package com.woting.content.pubref.persistence.pojo;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

public class ResCataRefPo extends BaseObject {
    private static final long serialVersionUID = -4002534086518812917L;
    private String id;//对应关系Id
    private String resType;//资源类型Id：1电台；2单体媒体资源；3系列媒体资源
    private String resId;//电台Id
    private String dictMid; //字典组Id
    private String dictDid; //字典项Id
    private String bCode; //字典项业务编码
    private String title; //字典项名称
    private String pathNames; //字典项全名称
    private String pathIds; //字典项路径Id
    private Timestamp CTime; //记录创建时间

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getResType() {
        return resType;
    }
    public void setResType(String resType) {
        this.resType = resType;
    }
    public String getResId() {
        return resId;
    }
    public void setResId(String resId) {
        this.resId = resId;
    }
    public String getDictMid() {
        return dictMid;
    }
    public void setDictMid(String dictMid) {
        this.dictMid = dictMid;
    }
    public String getDictDid() {
        return dictDid;
    }
    public void setDictDid(String dictDid) {
        this.dictDid = dictDid;
    }
    public String getbCode() {
        return bCode;
    }
    public void setbCode(String bCode) {
        this.bCode = bCode;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getPathNames() {
        return pathNames;
    }
    public void setPathNames(String pathNames) {
        this.pathNames = pathNames;
    }
    public String getPathIds() {
        return pathIds;
    }
    public void setPathIds(String pathIds) {
        this.pathIds = pathIds;
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp cTime) {
        CTime = cTime;
    }
}