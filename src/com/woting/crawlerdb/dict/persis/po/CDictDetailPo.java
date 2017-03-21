package com.woting.crawlerdb.dict.persis.po;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

public class CDictDetailPo extends BaseObject {

	private static final long serialVersionUID = -8654589382196177289L;
	private String id;
	private String sourceId;
	private String mId;
	private String pId;
	private String publisher;
	private String ddName;
	private String nPy;
	private String aliasName;
	private String anPy;
	private String visitUrl;
	private String schemeId;
	private String schemeName;
	private int crawlerNum;
	private int isValidate;
	private Timestamp cTime;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public String getmId() {
		return mId;
	}
	public void setmId(String mId) {
		this.mId = mId;
	}
	public String getpId() {
		return pId;
	}
	public void setpId(String pId) {
		this.pId = pId;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getDdName() {
		return ddName;
	}
	public void setDdName(String ddName) {
		this.ddName = ddName;
	}
	public String getnPy() {
		return nPy;
	}
	public void setnPy(String nPy) {
		this.nPy = nPy;
	}
	public String getAliasName() {
		return aliasName;
	}
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
	public String getAnPy() {
		return anPy;
	}
	public void setAnPy(String anPy) {
		this.anPy = anPy;
	}
	public String getVisitUrl() {
		return visitUrl;
	}
	public void setVisitUrl(String visitUrl) {
		this.visitUrl = visitUrl;
	}
	public String getSchemeId() {
		return schemeId;
	}
	public void setSchemeId(String schemeId) {
		this.schemeId = schemeId;
	}
	public String getSchemeName() {
		return schemeName;
	}
	public void setSchemeName(String schemeName) {
		this.schemeName = schemeName;
	}
	public int getIsValidate() {
		return isValidate;
	}
	public void setIsValidate(int isValidate) {
		this.isValidate = isValidate;
	}
	public int getCrawlerNum() {
		return crawlerNum;
	}
	public void setCrawlerNum(int crawlerNum) {
		this.crawlerNum = crawlerNum;
	}
	public Timestamp getcTime() {
		return cTime;
	}
	public void setcTime(Timestamp cTime) {
		this.cTime = cTime;
	}
}
