package com.woting.cm.core.channel.persis.po;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

public class ChannelAssetProgressPo extends BaseObject {

	private static final long serialVersionUID = 7789697859937825969L;
	private String id;
	private String chaId;
	private String checkerId;
	private int applyFlowFlag;
	private int reFlowFlag;
	private String applyDescn;
	private String reDescn;
	private Timestamp cTime;
	private Timestamp modifyTime;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getChaId() {
		return chaId;
	}
	public void setChaId(String chaId) {
		this.chaId = chaId;
	}
	public String getCheckerId() {
		return checkerId;
	}
	public void setCheckerId(String checkerId) {
		this.checkerId = checkerId;
	}
	public int getApplyFlowFlag() {
		return applyFlowFlag;
	}
	public void setApplyFlowFlag(int applyFlowFlag) {
		this.applyFlowFlag = applyFlowFlag;
	}
	public int getReFlowFlag() {
		return reFlowFlag;
	}
	public void setReFlowFlag(int reFlowFlag) {
		this.reFlowFlag = reFlowFlag;
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
		return modifyTime;
	}
	public void setModifyTime(Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}
}
