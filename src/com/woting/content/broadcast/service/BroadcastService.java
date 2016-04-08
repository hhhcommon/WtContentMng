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
import com.woting.cm.core.dict.mem._CacheDictionary;
import com.woting.cm.core.dict.model.DictDetail;
import com.woting.cm.core.dict.model.DictModel;
import com.woting.cm.core.dict.model.DictRefRes;
import com.woting.cm.core.dict.persis.po.DictRefResPo;
import com.woting.content.broadcast.persistence.pojo.BroadcastPo;
import com.woting.content.broadcast.persistence.pojo.FrequncePo;
import com.woting.content.broadcast.persistence.pojo.LiveFlowPo;

public class BroadcastService {
    @Resource(name="defaultDAO")
    private MybatisDAO<BroadcastPo> broadcastDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<LiveFlowPo> bc_liveflowDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<FrequncePo> bc_frequnceDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<DictRefResPo> dictRefResDao;

    @PostConstruct
    public void initParam() {
        broadcastDao.setNamespace("BROADCAST");
        bc_liveflowDao.setNamespace("BC_LF");
        bc_frequnceDao.setNamespace("BC_F");
        dictRefResDao.setNamespace("A_DREFRES");
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
        com.woting.cm.core.dict.mem._CacheDictionary _cd = ((CacheEle<_CacheDictionary>)SystemCache.getCache(WtContentMngConstants.CACHE_DICT)).getContent();
        //字典--地区
        String tempIds=m.get("bcArea")+"";
        DictModel tempDictM=_cd.getDictModelById("2");
        TreeNode<DictDetail> tempNode=(TreeNode<DictDetail>)tempDictM.dictTree.findNode(tempIds);
        if (tempNode!=null) {
            DictRefResPo drrPo = new DictRefResPo();
            drrPo.setId(SequenceUUID.getUUIDSubSegment(4));
            drrPo.setRefName("电台所属地区");
            drrPo.setResTableName("wt_Broadcast");
            drrPo.setResId(bPo.getId());
            drrPo.setDictMid(tempDictM.getId());
            drrPo.setDictMName(tempDictM.getDmName());
            drrPo.setDictDid(tempIds);
            drrPo.setTitle(tempNode.getNodeName());
            drrPo.setBCode(tempNode.getTnEntity().getBCode());
            drrPo.setPathNames(tempNode.getTreePathName("-", 0));
            drrPo.setPathIds(tempNode.getTreePathId("-", 0));
            dictRefResDao.insert(drrPo);
        }
        //字典--分类
        tempIds=m.get("cType")+"";
        String ids[]=tempIds.split(",");
        tempDictM=_cd.getDictModelById("1");
        for (int i=0; i<ids.length; i++) {
            tempNode = (TreeNode<DictDetail>)tempDictM.dictTree.findNode(ids[i]);
            if (tempNode!=null) {
                DictRefResPo drrPo = new DictRefResPo();
                drrPo.setId(SequenceUUID.getUUIDSubSegment(4));
                drrPo.setRefName("电台分类");
                drrPo.setResTableName("wt_Broadcast");
                drrPo.setResId(bPo.getId());
                drrPo.setDictMid(tempDictM.getId());
                drrPo.setDictMName(tempDictM.getDmName());
                drrPo.setDictDid(ids[i]);
                drrPo.setTitle(tempNode.getNodeName());
                drrPo.setBCode(tempNode.getTnEntity().getBCode());
                drrPo.setPathNames(tempNode.getTreePathName("-", 0));
                drrPo.setPathIds(tempNode.getTreePathId("-", 0));
                dictRefResDao.insert(drrPo);
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
            lfp.setIsMain(0);
            if (Integer.parseInt(_s[3])==1&&!hasMain) {
                hasMain=true;
                lfp.setIsMain(1);
            }
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
        dictRefResDao.delete("multiDelBc", "'"+id+"'");//先删除
        com.woting.cm.core.dict.mem._CacheDictionary _cd = ((CacheEle<_CacheDictionary>)SystemCache.getCache(WtContentMngConstants.CACHE_DICT)).getContent();
        //字典--地区
        String tempIds=m.get("bcArea")+"";
        DictModel tempDictM=_cd.getDictModelById("2");
        TreeNode<DictDetail> tempNode=(TreeNode<DictDetail>)tempDictM.dictTree.findNode(tempIds);
        if (tempNode!=null) {
            DictRefResPo drrPo = new DictRefResPo();
            drrPo.setId(SequenceUUID.getUUIDSubSegment(4));
            drrPo.setRefName("电台所属地区");
            drrPo.setResTableName("wt_Broadcast");
            drrPo.setResId(bPo.getId());
            drrPo.setDictMid(tempDictM.getId());
            drrPo.setDictMName(tempDictM.getDmName());
            drrPo.setDictDid(tempIds);
            drrPo.setTitle(tempNode.getNodeName());
            drrPo.setBCode(tempNode.getTnEntity().getBCode());
            drrPo.setPathNames(tempNode.getTreePathName("-", 0));
            drrPo.setPathIds(tempNode.getTreePathId("-", 0));
            dictRefResDao.insert(drrPo);
        }
        //字典--分类
        tempIds=m.get("cType")+"";
        String ids[]=tempIds.split(",");
        tempDictM=_cd.getDictModelById("1");
        for (int i=0; i<ids.length; i++) {
            tempNode = (TreeNode<DictDetail>)tempDictM.dictTree.findNode(ids[i]);
            if (tempNode!=null) {
                DictRefResPo drrPo = new DictRefResPo();
                drrPo.setId(SequenceUUID.getUUIDSubSegment(4));
                drrPo.setRefName("电台分类");
                drrPo.setResTableName("wt_Broadcast");
                drrPo.setResId(bPo.getId());
                drrPo.setDictMid(tempDictM.getId());
                drrPo.setDictMName(tempDictM.getDmName());
                drrPo.setDictDid(ids[i]);
                drrPo.setTitle(tempNode.getNodeName());
                drrPo.setBCode(tempNode.getTnEntity().getBCode());
                drrPo.setPathNames(tempNode.getTreePathName("-", 0));
                drrPo.setPathIds(tempNode.getTreePathId("-", 0));
                dictRefResDao.insert(drrPo);
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
            lfp.setIsMain(0);
            if (Integer.parseInt(_s[3])==1&&!hasMain) {
                hasMain=true;
                lfp.setIsMain(1);
            }
            bc_liveflowDao.insert(lfp);
        }
    }

    public Page<Map<String, Object>> getViewList(Map<String, Object> m) {
        Map<String, Object> param = new HashMap<String, Object>();
        int pageIndex=Integer.parseInt(m.get("pageNumber")+"");
        int pageSize=Integer.parseInt(m.get("pageSize")+"");
        param.put("orderByClause", "a.CTime desc");
        Page<Map<String, Object>> retP=broadcastDao.pageQueryAutoTranform(null, "query4ViewTemp", param, pageIndex, pageSize);
        //List<Map<String, Object>> retL = broadcastDao.queryForListAutoTranform("query4ViewTemp", null);
        return retP;
    }

    public void del(String ids) {
        ids=ids.replaceAll(",", "','");
        ids="'"+ids+"'";
        broadcastDao.delete("multiDelBc", ids);
        bc_liveflowDao.delete("multiDelBc", ids);
        dictRefResDao.delete("multiDelBc", ids);
    }

    public Map<String, Object> getInfo(String bcId) {
        Map<String, Object> ret = new HashMap<String, Object>();
        //基本信息
        BroadcastPo bp = broadcastDao.getInfoObject("getInfoById", bcId);
        if (bp!=null) ret.put("bcBaseInfo", bp);
        else return null;
        //直播流
        Map<String, String> param=new HashMap<String, String>();
        param.put("bcId", bcId);
        List<LiveFlowPo> lfpL = bc_liveflowDao.queryForList(param);
        if (lfpL!=null&&lfpL.size()>0) ret.put("liveflows", lfpL);
        //分类
        param=new HashMap<String, String>();
        param.put("resType", "1");
        param.put("resId", bcId);
        param.put("orderByClause", "dictMid, bCode");
        List<DictRefResPo> rcrpL = dictRefResDao.queryForList(param);
        if (rcrpL!=null&&rcrpL.size()>0) ret.put("cataList", rcrpL);

        return ret;
    }

    public List<DictRefResPo> getCataRefList(String ids) {
        Map<String, String> param=new HashMap<String, String>();
        param.put("resType", "1");
        param.put("resIds", ids);
        param.put("orderByClause", "resId, dictMid, bCode");
        List<DictRefResPo> rcrpL = dictRefResDao.queryForList("getListByResIds", param);
        return rcrpL;
    }
}