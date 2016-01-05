package com.woting.content.broadcast.persistence.pojo;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

public class LiveFlowPo extends BaseObject {
    private static final long serialVersionUID = -8315162892011249812L;

    private String id;//直播流Id
    private String bcId;//对应电台Id
    private String bcSource;//来源:蜻蜓，官网等
    private String flowURI;//来源:蜻蜓，官网等
    private Integer isMain;//是否是主直播流
    private Timestamp CTime; //记录创建时间

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getBcId() {
        return bcId;
    }
    public void setBcId(String bcId) {
        this.bcId = bcId;
    }
    public String getBcSource() {
        return bcSource;
    }
    public void setBcSource(String bcSource) {
        this.bcSource = bcSource;
    }
    public String getFlowURI() {
        return flowURI;
    }
    public void setFlowURI(String flowURI) {
        this.flowURI = flowURI;
    }
    public Integer getIsMain() {
        return isMain;
    }
    public void setIsMain(Integer isMain) {
        this.isMain = isMain;
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp cTime) {
        CTime = cTime;
    }
}