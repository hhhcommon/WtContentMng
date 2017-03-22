package com.woting.crawlerdb.dict.mem;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.spiritdata.framework.core.model.tree.TreeNode;
import com.woting.crawlerdb.dict.model.CDictDetail;
import com.woting.crawlerdb.dict.model.CDictMaster;
import com.woting.crawlerdb.dict.model.CDictModel;

public class _CacheCDictionary {
	public Map<String, CDictModel> cdictModelMap;
	public List<CDictMaster> cdmlist = null;
	public List<CDictDetail> cddlist = null;
	
	/**
     * 构造所有者处理单元
     */
    public _CacheCDictionary() {
        cdictModelMap=new ConcurrentHashMap<String, CDictModel>();
    }
    
    /**
     * 根据Id得到字典模式
     * @param dictMId 字典组Id
     * @return 元数据信息
     */
    public CDictModel getCDictModelById(String dictMid) {
        if (cdictModelMap==null) return null;
        return cdictModelMap.get(dictMid);
    }
    
    /**
     * 根据Id得到字典模式
     * @param dictMId 字典组Id
     * @return 元数据信息
     */
    @SuppressWarnings("unchecked")
	public TreeNode getCDictDetail(String cdictMid, String cdictDid) {
        if (cdictModelMap==null) return null;
        CDictModel dm=cdictModelMap.get(cdictMid);
        if (dm==null) return null;
        TreeNode<CDictDetail> ddTn=(TreeNode<CDictDetail>)dm.cdictTree.findNode(cdictDid);
        if (ddTn==null) return null;
        return ddTn;
    }
}
