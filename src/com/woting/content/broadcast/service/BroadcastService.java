package com.woting.content.broadcast.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.util.SequenceUUID;
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

    @PostConstruct
    public void initParam() {
        broadcastDao.setNamespace("BROADCAST");
        bc_liveflowDao.setNamespace("BC_LF");
        bc_frequnceDao.setNamespace("BC_F");
    }

    /**
     * 新增内容
     * @param m
     */
    public void add(Map<String, Object> m) {
        BroadcastPo bPo = new BroadcastPo();
        bPo.setId(SequenceUUID.getUUIDSubSegment(4));
        bPo.setBcTitle(m.get("bcTitle")+"");
        bPo.setBcPublisher(m.get("bcPublisher")+"");
        bPo.setBcUrl(m.get("bcUrl")+"");
        bPo.setDesc(m.get("descn")+"");
        //分类，暂存
        bPo.setBcImg(m.get("cType")+"::"+m.get("bcArea"));
        broadcastDao.insert(bPo);
        //直播流
        String lfs=m.get("bcLiveFlows")+"";
        String[] fla = lfs.split(";;");
        for (int i=0; i<fla.length; i++) {
            String[] _s=fla[i].split("::");
            LiveFlowPo lfp = new LiveFlowPo();
            lfp.setId(SequenceUUID.getUUIDSubSegment(4));
            lfp.setBcId(bPo.getId());
            lfp.setBcSource(_s[0]);
            lfp.setFlowURI(_s[1]);
            lfp.setIsMain(1);
            bc_liveflowDao.insert(lfp);
        }
    }

    public List<Map<String, Object>> getViewList(Map<String, Object> m) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("orderByClause", "a.CTime desc");
        List<Map<String, Object>> retL = broadcastDao.queryForListAutoTranform("query4ViewTemp", null);
        return retL;
    }

    public void del(String ids) {
        ids=ids.replaceAll(",", "','");
        ids="'"+ids+"'";
        broadcastDao.delete("multiDelBc", ids);
        bc_liveflowDao.delete("multiDelBc", ids);
    }
}