package com.woting.cm.core.person.persis.po;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

public class PersonLimitPo extends BaseObject {
	private static final long serialVersionUID = 6641231245236133627L;
	private String id;
	private String dictDid;
	private String personId;
	private String limitDescn;
	private Timestamp cTime;
	private Timestamp lmTime;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDictDid() {
		return dictDid;
	}
	public void setDictDid(String dictDid) {
		this.dictDid = dictDid;
	}
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public String getLimitDescn() {
		return limitDescn;
	}
	public void setLimitDescn(String limitDescn) {
		this.limitDescn = limitDescn;
	}
	public Timestamp getcTime() {
		return cTime;
	}
	public void setcTime(Timestamp cTime) {
		this.cTime = cTime;
	}
	public Timestamp getLmTime() {
		return lmTime;
	}
	public void setLmTime(Timestamp lmTime) {
		this.lmTime = lmTime;
	}
}
