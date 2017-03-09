package com.woting.cm.core.dict.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;
import com.spiritdata.framework.core.model.ModelSwapPo;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.core.model.tree.TreeNodeBean;
import com.spiritdata.framework.exceptionC.Plat0006CException;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.cm.core.dict.persis.po.DictRefResPo;

public class DictRefRes extends BaseObject implements Serializable, ModelSwapPo {
    private static final long serialVersionUID=5201517946401777207L;

    private String id; //uuid(主键)
    private String refName; //关系名称：resTableName+dictMId=唯一关系名称，既相当于某类资源的一个字段
    private String resTableName; //资源类型Id：1电台；2单体媒体资源；3专辑资源
    private String resId; //资源Id
    private DictModel dm;
    private TreeNode<TreeNodeBean> dd;
    private Timestamp CTime; //创建时间

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id=id;
    }
    public String getRefName() {
        return refName;
    }
    public void setRefName(String refName) {
        this.refName=refName;
    }
    public String getResTableName() {
        return resTableName;
    }
    public void setResTableName(String resTableName) {
        this.resTableName=resTableName;
    }
    public String getResId() {
        return resId;
    }
    public void setResId(String resId) {
        this.resId=resId;
    }
    public DictMaster getDm() {
        return dm;
    }
    public void setDm(DictModel dm) {
        if (dd!=null&&dd.getTnEntity() instanceof DictDetail) {
            if (!((DictDetail)dd.getTnEntity()).getMId().equals(dm.getId())) return;
        }
        this.dm=dm;
    }
    public TreeNode<TreeNodeBean> getDd() {
        return dd;
    }
    public void setDd(TreeNode<TreeNodeBean> dd) {
        if (dd.getTnEntity() instanceof DictDetail) {
            if (dm!=null&&!dm.getId().equals(((DictDetail)dd.getTnEntity()).getMId())) return;
        }
        this.dd=dd;
    }
    public void setDd(TreeNodeBean dd) {
        if (dd instanceof DictDetail) {
            if (dm!=null&&!dm.getId().equals(((DictDetail)dd).getMId())) return;
        }
        TreeNode<TreeNodeBean> tn=new TreeNode<TreeNodeBean>();
        tn.setTnEntity(dd);
        this.dd=tn;
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp cTime) {
        CTime=cTime;
    }

    @Override
    public DictRefResPo convert2Po() {
        DictRefResPo ret=new DictRefResPo();

        //id处理，没有id，自动生成一个
        if (StringUtils.isNullOrEmptyOrSpace(this.id)) ret.setId(SequenceUUID.getPureUUID());
        else ret.setId(this.id);
        ret.setRefName(refName);
        ret.setResTableName(resTableName);
        ret.setResId(resId);
        ret.setDictMid(dm.getId());
        ret.setDictDid(dd.getId());

        return ret;
    }

    @Override
    public void buildFromPo(Object po) {
        if (po==null) throw new Plat0006CException("Po对象为空，无法从空对象得到概念/逻辑对象！");
        if (!(po instanceof DictRefResPo)) throw new Plat0006CException("Po对象不是DictRefResPo的实例，无法从此对象构建字典资源关联对象！");
        DictRefResPo _po=(DictRefResPo)po;

        id=_po.getId();
        refName=_po.getRefName();
        resTableName=_po.getResTableName();
        resId=_po.getResId();
        CTime=_po.getCTime();
        //dm和dd无法在这里获取，这里只是做一个记录
        DictModel dm=new DictModel();
        dm.setId(_po.getDictMid());
        this.dm=dm;
    }

    @Override
    public int hashCode() {
        return this.getId()==null?0:this.getId().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o==null) return false;
        if (!(o instanceof DictRefRes)) return false;
        DictRefRes _other=(DictRefRes)o;
        try {
            if (this.getResId()==null&&_other.getResId()==null&&this.getResTableName()==null&&_other.getResTableName()==null
              &&this.getRefName()==null&&_other.getRefName()==null&&this.getDm()==null&&_other.getDm()==null
              &&this.getDd()==null&&_other.getDd()==null) return true;
            if (this.getResId()!=null&&this.getResId().equals(_other.getResId())
              &&this.getResTableName()!=null&&this.getResTableName().equals(_other.getResTableName())
              &&this.getRefName()!=null&&this.getRefName().equals(_other.getRefName())
              &&this.getDm()!=null&&this.getDm().getId().equals(_other.getDm()==null?null:_other.getDm().getId())
              &&this.getDd()!=null&&this.getDd().getId().equals(_other.getDd()==null?null:_other.getDd().getId())
              ) return true;
        } catch(Exception e) {}
        return false;
    }
}