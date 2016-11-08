package com.woting.cm.core.complexref.persis.po;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

public class ComplexRefPo extends BaseObject {

	private static final long serialVersionUID = -3396728314059425915L;
	
	private String id;
	private String assetTableName;
	private String assetId;
	private String resTableName;
	private String resId;
	private String dictMId;
	private String dictDId;
	private Timestamp cTime;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAssetTableName() {
		return assetTableName;
	}
	public void setAssetTableName(String assetTableName) {
		this.assetTableName = assetTableName;
	}
	public String getAssetId() {
		return assetId;
	}
	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}
	public String getResTableName() {
		return resTableName;
	}
	public void setResTableName(String resTableName) {
		this.resTableName = resTableName;
	}
	public String getResId() {
		return resId;
	}
	public void setResId(String resId) {
		this.resId = resId;
	}
	public String getDictMId() {
		return dictMId;
	}
	public void setDictMId(String dictMId) {
		this.dictMId = dictMId;
	}
	public String getDictDId() {
		return dictDId;
	}
	public void setDictDId(String dictDId) {
		this.dictDId = dictDId;
	}
	public Timestamp getcTime() {
		return cTime;
	}
	public void setcTime(Timestamp cTime) {
		this.cTime = cTime;
	}
}
