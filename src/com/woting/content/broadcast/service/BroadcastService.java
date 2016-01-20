package com.woting.content.broadcast.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.core.model.Page;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.util.SequenceUUID;
import com.woting.WtContentMngConstants;
import com.woting.content.broadcast.persistence.pojo.BroadcastPo;
import com.woting.content.broadcast.persistence.pojo.FrequncePo;
import com.woting.content.broadcast.persistence.pojo.LiveFlowPo;
import com.woting.content.pubref.persistence.pojo.ResCataRefPo;
import com.woting.dictionary.model.DictDetail;
import com.woting.dictionary.model.DictModel;
import com.woting.dictionary.model._CacheDictionary;

public class BroadcastService {
    @Resource(name="defaultDAO")
    private MybatisDAO<BroadcastPo> broadcastDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<LiveFlowPo> bc_liveflowDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<FrequncePo> bc_frequnceDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<ResCataRefPo> resCataRefDao;

    @PostConstruct
    public void initParam() {
        broadcastDao.setNamespace("BROADCAST");
        bc_liveflowDao.setNamespace("BC_LF");
        bc_frequnceDao.setNamespace("BC_F");
        resCataRefDao.setNamespace("REF_RESCATA");
    }

    /**
     * 新增内容
     * @param m
     */
    public void add(Map<String, Object> m) {
        BroadcastPo bPo = new BroadcastPo();
        bPo.setId(SequenceUUID.getUUIDSubSegment(4));
        bPo.setBcTitle(m.get("bcTitle")+"");
        bPo.setBcPubType(2);
        bPo.setBcPublisher(m.get("bcPublisher")+"");
        bPo.setBcUrl(m.get("bcUrl")+"");
        bPo.setDesc(m.get("descn")+"");
        broadcastDao.insert(bPo);

        //字典
        _CacheDictionary _cd = ((CacheEle<_CacheDictionary>)SystemCache.getCache(WtContentMngConstants.CACHE_DICT)).getContent();
        //字典--地区
        String tempIds=m.get("bcArea")+"";
        DictModel tempDictM=_cd.getDictModelById("2");
        TreeNode<DictDetail> tempNode=(TreeNode<DictDetail>)tempDictM.dictTree.findNode(tempIds);
        if (tempNode!=null) {
            ResCataRefPo rcp = new ResCataRefPo();
            rcp.setId(SequenceUUID.getUUIDSubSegment(4));
            rcp.setResType("1");
            rcp.setResId(bPo.getId());
            rcp.setDictMid(tempDictM.getId());
            rcp.setDictDid(tempIds);
            rcp.setTitle(tempNode.getNodeName());
            rcp.setbCode(tempNode.getTnEntity().getBCode());
            rcp.setPathNames(tempNode.getTreePathName("-", 0));
            rcp.setPathIds(tempNode.getTreePathId("-", 0));
            resCataRefDao.insert(rcp);
        }
        //字典--分类
        tempIds=m.get("cType")+"";
        String ids[]=tempIds.split(",");
        tempDictM=_cd.getDictModelById("1");
        for (int i=0; i<ids.length; i++) {
            tempNode = (TreeNode<DictDetail>)tempDictM.dictTree.findNode(ids[i]);
            if (tempNode!=null) {
                ResCataRefPo rcp = new ResCataRefPo();
                rcp.setId(SequenceUUID.getUUIDSubSegment(4));
                rcp.setResType("1");
                rcp.setResId(bPo.getId());
                rcp.setDictMid(tempDictM.getId());
                rcp.setDictDid(ids[i]);
                rcp.setTitle(tempNode.getNodeName());
                rcp.setbCode(tempNode.getTnEntity().getBCode());
                rcp.setPathNames(tempNode.getTreePathName("-", 0));
                rcp.setPathIds(tempNode.getTreePathId("-", 0));
                resCataRefDao.insert(rcp);
            }
        }

        //直播流
        String lfs=m.get("bcLiveFlows")+"";
        String[] fla = lfs.split(";;");
        boolean hasMain=false;
        for (int i=0; i<fla.length; i++) {
            String[] _s=fla[i].split("::");
            LiveFlowPo lfp = new LiveFlowPo();
            lfp.setId(SequenceUUID.getUUIDSubSegment(4));
            lfp.setBcId(bPo.getId());
            lfp.setBcSrcType(Integer.parseInt(_s[2]));
            lfp.setBcSource(_s[0]);
            lfp.setFlowURI(_s[1]);
            if (Integer.parseInt(_s[3])==1) hasMain=true;
            if (hasMain) lfp.setIsMain(0);
            else lfp.setIsMain(Integer.parseInt(_s[3]));
            bc_liveflowDao.insert(lfp);
        }
    }
    /**
     * 修改内容，子表都删除掉，再入库
     * @param m
     */
    public void update(Map<String, Object> m) {
        String id=m.get("id")+"";
        BroadcastPo bPo = new BroadcastPo();
        bPo.setId(id);
        bPo.setBcTitle(m.get("bcTitle")+"");
        bPo.setBcPubType(2);
        bPo.setBcPublisher(m.get("bcPublisher")+"");
        bPo.setBcUrl(m.get("bcUrl")+"");
        bPo.setDesc(m.get("descn")+"");
        broadcastDao.update(bPo);

        //字典
        resCataRefDao.delete("multiDelBc", "'"+id+"'");//先删除
        _CacheDictionary _cd = ((CacheEle<_CacheDictionary>)SystemCache.getCache(WtContentMngConstants.CACHE_DICT)).getContent();
        //字典--地区
        String tempIds=m.get("bcArea")+"";
        DictModel tempDictM=_cd.getDictModelById("2");
        TreeNode<DictDetail> tempNode=(TreeNode<DictDetail>)tempDictM.dictTree.findNode(tempIds);
        if (tempNode!=null) {
            ResCataRefPo rcp = new ResCataRefPo();
            rcp.setId(SequenceUUID.getUUIDSubSegment(4));
            rcp.setResType("1");
            rcp.setResId(bPo.getId());
            rcp.setDictMid(tempDictM.getId());
            rcp.setDictDid(tempIds);
            rcp.setTitle(tempNode.getNodeName());
            rcp.setbCode(tempNode.getTnEntity().getBCode());
            rcp.setPathNames(tempNode.getTreePathName("-", 0));
            rcp.setPathIds(tempNode.getTreePathId("-", 0));
            resCataRefDao.insert(rcp);
        }
        //字典--分类
        tempIds=m.get("cType")+"";
        String ids[]=tempIds.split(",");
        tempDictM=_cd.getDictModelById("1");
        for (int i=0; i<ids.length; i++) {
            tempNode = (TreeNode<DictDetail>)tempDictM.dictTree.findNode(ids[i]);
            if (tempNode!=null) {
                ResCataRefPo rcp = new ResCataRefPo();
                rcp.setId(SequenceUUID.getUUIDSubSegment(4));
                rcp.setResType("1");
                rcp.setResId(bPo.getId());
                rcp.setDictMid(tempDictM.getId());
                rcp.setDictDid(ids[i]);
                rcp.setTitle(tempNode.getNodeName());
                rcp.setbCode(tempNode.getTnEntity().getBCode());
                rcp.setPathNames(tempNode.getTreePathName("-", 0));
                rcp.setPathIds(tempNode.getTreePathId("-", 0));
                resCataRefDao.insert(rcp);
            }
        }

        //直播流
        bc_liveflowDao.delete("multiDelBc", "'"+id+"'");//先删除
        String lfs=m.get("bcLiveFlows")+"";
        String[] fla = lfs.split(";;");
        boolean hasMain=false;
        for (int i=0; i<fla.length; i++) {
            String[] _s=fla[i].split("::");
            LiveFlowPo lfp = new LiveFlowPo();
            lfp.setId(SequenceUUID.getUUIDSubSegment(4));
            lfp.setBcId(bPo.getId());
            lfp.setBcSrcType(Integer.parseInt(_s[2]));
            lfp.setBcSource(_s[0]);
            lfp.setFlowURI(_s[1]);
            if (Integer.parseInt(_s[3])==1) hasMain=true;
            if (hasMain) lfp.setIsMain(0);
            else lfp.setIsMain(Integer.parseInt(_s[3]));
            bc_liveflowDao.insert(lfp);
        }
    }

    public Page<Map<String, Object>> getViewList(Map<String, Object> m) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("orderByClause", "a.CTime desc");
        Page<Map<String, Object>> retP=broadcastDao.pageQueryAutoTranform(null, "query4ViewTemp", param, 0, 10);
        //List<Map<String, Object>> retL = broadcastDao.queryForListAutoTranform("query4ViewTemp", null);
        return retP;
    }

    public void del(String ids) {
        ids=ids.replaceAll(",", "','");
        ids="'"+ids+"'";
        broadcastDao.delete("multiDelBc", ids);
        bc_liveflowDao.delete("multiDelBc", ids);
        resCataRefDao.delete("multiDelBc", ids);
    }

    public Map<String, Object> getInfo(String bcId) {
        Map<String, Object> ret = new HashMap<String, Object>();
        //基本信息
        BroadcastPo bp = broadcastDao.getInfoObject("getInfoById", bcId);
        if (bp!=null) {
            ret.put("bcBaseInfo", bp);
        } else return null;
        //直播流
        Map<String, String> param=new HashMap<String, String>();
        param.put("bcId", bcId);
        List<LiveFlowPo> lfpL = bc_liveflowDao.queryForList(param);
        if (lfpL!=null&&lfpL.size()>0) {
            ret.put("liveflows", lfpL);
        }
        //分类
        param=new HashMap<String, String>();
        param.put("resType", "1");
        param.put("resId", bcId);
        param.put("orderByClause", "dictMid, bCode");
        List<ResCataRefPo> rcrpL = resCataRefDao.queryForList(param);
        if (rcrpL!=null&&rcrpL.size()>0) {
            ret.put("cataList", rcrpL);
        }
        return ret;
    }

    public List<ResCataRefPo> getCataRefList(String ids) {
        Map<String, String> param=new HashMap<String, String>();
        param.put("resType", "1");
        param.put("resIds", ids);
        param.put("orderByClause", "resId, dictMid, bCode");
        List<ResCataRefPo> rcrpL = resCataRefDao.queryForList("getListByResIds",param);
        return rcrpL;
    }
}