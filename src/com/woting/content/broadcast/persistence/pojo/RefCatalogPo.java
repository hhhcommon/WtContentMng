package com.woting.content.broadcast.persistence.pojo;

import com.spiritdata.framework.core.model.BaseObject;

public class RefCatalogPo extends BaseObject {
    private static final long serialVersionUID = -4002534086518812917L;
    private String id;//对应关系Id
    private String bcId;//电台Id
    private String dictMid; //字典组Id
    private String dictDid; //字典项Id
    private String bCode; //字典项业务编码
    private String cName; //字典项名称
    private String cAllName; //字典项全名称
    private String cAllIds; //字典项路径Id

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
    public String getcName() {
        return cName;
    }
    public void setcName(String cName) {
        this.cName = cName;
    }
    public String getcAllName() {
        return cAllName;
    }
    public void setcAllName(String cAllName) {
        this.cAllName = cAllName;
    }
    public String getcAllIds() {
        return cAllIds;
    }
    public void setcAllIds(String cAllIds) {
        this.cAllIds = cAllIds;
    }
    }