package com.woting.crawlerdb.dict.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.spiritdata.framework.core.model.ModelSwapPo;
import com.spiritdata.framework.exceptionC.Plat0006CException;
import com.spiritdata.framework.util.ChineseCharactersUtils;
import com.spiritdata.framework.util.StringUtils;
import com.woting.cm.core.common.model.Owner;
import com.woting.crawlerdb.dict.persis.po.CDictMasterPo;

public class CDictMaster implements Serializable, ModelSwapPo {
	private static final long serialVersionUID = 6943099458201483420L;

	 private String id; //字典组id
	 private Owner owner;
	 private String dmName; //字典组名称
	 private String NPy; //字典组名称拼音
	 private int order; //排序号，越大越靠前
	 private int isValidate; //字典组是否可用 1可用，2不可用
	 private int MType; //字典组类型：1系统保留；2系统；3定义；
	 private String MRef; //字典组引用，当mType=3
	 private String desc; //说明
	 private Timestamp CTime; //记录创建时间
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public String getDmName() {
		return dmName;
	}

	public void setDmName(String dmName) {
		this.dmName = dmName;
        if (StringUtils.isNullOrEmptyOrSpace(this.NPy)) this.NPy=ChineseCharactersUtils.getFullSpellFirstUp(this.dmName);
	}

	public String getNPy() {
		return NPy;
	}

	public void setNPy(String nPy) {
		NPy = nPy;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getIsValidate() {
		return isValidate;
	}

	public void setIsValidate(int isValidate) {
		this.isValidate = isValidate;
	}

	public int getMType() {
		return MType;
	}

	public void setMType(int mType) {
		MType = mType;
	}

	public String getMRef() {
		return MRef;
	}

	public void setMRef(String mRef) {
		MRef = mRef;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Timestamp getCTime() {
		return CTime;
	}

	public void setCTime(Timestamp cTime) {
		CTime = cTime;
	}

	@Override
	public void buildFromPo(Object po) {
		if (po==null) throw new Plat0006CException("Po对象为空，无法从空对象得到概念/逻辑对象！");
        if (!(po instanceof CDictMasterPo)) throw new Plat0006CException("Po对象不是CDictMasterPo的实例，无法从此对象构建字典组对象！");
        CDictMasterPo _po = (CDictMasterPo)po;
        this.id =_po.getId();
        this.setDmName(_po.getDmName());
        this.CTime = _po.getCTime();
	}

	@Override
	public Object convert2Po() {
		
		return null;
	}

}
