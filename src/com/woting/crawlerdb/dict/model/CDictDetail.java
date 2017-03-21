package com.woting.crawlerdb.dict.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.spiritdata.framework.core.model.ModelSwapPo;
import com.spiritdata.framework.core.model.tree.TreeNodeBean;
import com.spiritdata.framework.exceptionC.Plat0006CException;
import com.spiritdata.framework.util.ChineseCharactersUtils;
import com.spiritdata.framework.util.StringUtils;
import com.woting.crawlerdb.dict.persis.po.CDictDetailPo;

public class CDictDetail extends TreeNodeBean implements Serializable, ModelSwapPo {
	
	private static final long serialVersionUID = 5258737480175211092L;
	private String Mid;
	private int isValidate;  //是否生效(1-生效,2-无效)
	private String NPy; //字典名称拼音
    private String aliasName; //字典项别名
    private String anPy; //字典项别名拼音
    private String BCode; //字典项业务编码
    private String publisher;
    private int DType; //字典项类型：1系统保留；2系统；3定义；4引用：其他字典项ID
    private String DRef; //字典项引用
    private String desc; //说明
    private Timestamp CTime; //记录创建时间
	
	public String getMid() {
		return Mid;
	}

	public void setMid(String mid) {
		Mid = mid;
	}

	public int getIsValidate() {
		return isValidate;
	}

	public void setIsValidate(int isValidate) {
		this.isValidate = isValidate;
	}

	/**
     * 改写基类的该方法，使其能够自动设置汉语拼音
     */
	public void setDdName(String ddName) {
		this.setNodeName(ddName);
        if (StringUtils.isNullOrEmptyOrSpace(this.NPy)) this.NPy=ChineseCharactersUtils.getFullSpellFirstUp(ddName);
	}

	public String getNPy() {
		if (StringUtils.isNullOrEmptyOrSpace(this.NPy)&&!StringUtils.isNullOrEmptyOrSpace(this.getNodeName())) {
            this.NPy=ChineseCharactersUtils.getFullSpellFirstUp(this.getNodeName());
        }
        return this.NPy;
	}

	public void setNPy(String nPy) {
		NPy = nPy;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
        if (StringUtils.isNullOrEmptyOrSpace(this.anPy)) this.anPy=ChineseCharactersUtils.getFullSpellFirstUp(aliasName);
	}

	public String getAnPy() {
		if (StringUtils.isNullOrEmptyOrSpace(this.NPy)&&!StringUtils.isNullOrEmptyOrSpace(this.getNodeName())) {
            this.NPy=ChineseCharactersUtils.getFullSpellFirstUp(this.getNodeName());
        }
        return this.anPy;
	}

	public void setAnPy(String anPy) {
		this.anPy = anPy;
	}

	public String getBCode() {
		return BCode;
	}

	public void setBCode(String bCode) {
		BCode = bCode;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public int getDType() {
		return DType;
	}

	public void setDType(int dType) {
		DType = dType;
	}

	public String getDRef() {
		return DRef;
	}

	public void setDRef(String dRef) {
		DRef = dRef;
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
		if (!(po instanceof CDictDetailPo)) throw new Plat0006CException("Po对象不是CDictDetailPo的实例，无法从此对象构建字典项对象！");
		CDictDetailPo _po = (CDictDetailPo) po;
		this.setId(_po.getId());
		this.Mid=_po.getmId();
		this.setParentId(_po.getpId());
		this.setOrderType(0);//从大到小
		this.isValidate=_po.getIsValidate();
		this.setDdName(_po.getDdName());
		this.setAliasName(_po.getAliasName());
		this.setPublisher(_po.getPublisher());
		this.CTime=_po.getcTime();
	}

	@Override
	public Object convert2Po() {
		// TODO Auto-generated method stub
		return null;
	}

}
