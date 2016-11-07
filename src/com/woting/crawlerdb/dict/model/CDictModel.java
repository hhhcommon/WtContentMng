package com.woting.crawlerdb.dict.model;

import com.spiritdata.framework.core.model.tree.TreeNode;
import com.woting.cm.core.dict.model.DictMaster;

public class CDictModel extends DictMaster {
	private static final long serialVersionUID = -5362736537533905063L;
	
	public CDictModel() {
		super();
	}
	
	/**
    * 根据持久化数据（字典组），构造无字典树的字典模型
    * @param dMaster 字典组信息
    */
   public CDictModel(CDictMaster cdMaster) {
       super();
       this.setCDictModelByMaster(cdMaster);
   }
   
   /**
    * 根据持久化数据（字典组），构造无字典树的字典模型
    * @param dMaster 字典组信息
    */
   public void setCDictModelByMaster(CDictMaster cdMaster) {
       this.setId(cdMaster.getId());
       this.setDmName(cdMaster.getDmName());
       this.setOrder(cdMaster.getOrder());
       this.setIsValidate(cdMaster.getIsValidate());
       this.setMType(cdMaster.getMType());
       this.setMRef(cdMaster.getMRef());
       this.setDesc(cdMaster.getDesc());
       this.setOwner(cdMaster.getOwner());
       this.setCTime(cdMaster.getCTime());
   }
   
   /**
    * 获得字典组信息，为持久化处理
    */
   public CDictMaster getCDictMaster() {
       CDictMaster dd = new CDictMaster();
       dd.setId(this.getId());
       dd.setDmName(this.getDmName());
       dd.setOrder(this.getOrder());
       dd.setIsValidate(this.getIsValidate());
       dd.setMType(this.getMType());
       dd.setMRef(this.getMRef());
       dd.setDesc(this.getDesc());
       dd.setOwner(this.getOwner());
       dd.setCTime(this.getCTime());
       return dd;
   }
   
   /**
    * 字典的根，此根结点是以本字典模型对应的字典组信息为基础的
    */
   public TreeNode<CDictDetail> cdictTree;
}
