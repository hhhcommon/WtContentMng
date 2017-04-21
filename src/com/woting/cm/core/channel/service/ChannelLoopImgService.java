package com.woting.cm.core.channel.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.core.model.Page;
import com.spiritdata.framework.util.StringUtils;
import com.woting.cm.core.channel.persis.po.ChannelAssetPo;
import com.woting.cm.core.media.MediaType;

public class ChannelLoopImgService {
    @Resource(name="defaultDAO")
    private MybatisDAO<ChannelAssetPo> channelAssetDao;

    @PostConstruct
    public void initParam() {
        channelAssetDao.setNamespace("A_CHANNELASSET");
    }

    /**
     * 获取某栏目下的轮播图列表
     * @param mediaType 过滤条件，按类型过滤
     * @param channelId 栏目Id
     * @param pageSize 每页有几条记录
     * @param pageIndex 页码，若为0,则得到所有内容
     * @return 轮播图列表，若无列表返回null
     */
    public List<Map<String, Object>> getLoopImgList(String mediaType, String channelId, int pageSize, int pageIndex) {
        if (StringUtils.isNullOrEmptyOrSpace(channelId)) return null;

        List<Map<String, Object>> _ret=null;
        Map<String, Object> param=new HashMap<String, Object>();
        param.put("channelId", channelId);
        //处理类型
        if (!StringUtils.isNullOrEmptyOrSpace(mediaType)) {
            String[] ms=mediaType.split(",");
            List<String> assetTypes=new ArrayList<String>();//多个媒体类型表的列表
            for (String oneMediaType:ms) {
                MediaType oneMT=MediaType.buildByTypeName(oneMediaType);
                if (oneMT!=MediaType.ERR) {
                    assetTypes.add(oneMT.getTabName());
                }
            }
            if (!assetTypes.isEmpty()) param.put("assetTypes", assetTypes);
        }
        if (pageIndex==0) _ret=channelAssetDao.queryForListAutoTranform("getLoopImgList", param);
        else {
            Page<Map<String, Object>> page=channelAssetDao.pageQueryAutoTranform(null, "getLoopImgList", param, pageIndex, pageSize);
            if (page!=null&&page.getDataCount()>0) {
                _ret=new ArrayList<Map<String, Object>>();
                _ret.addAll(page.getResult());
            }
        }
        if (_ret==null||_ret.isEmpty()) return null;

        List<Map<String, Object>> ret=new ArrayList<Map<String, Object>>(_ret.size());
        for (int i=0; i<_ret.size(); i++) {
            Map<String, Object> one=_ret.get(i);
            Map<String, Object> _one=new HashMap<String, Object>();
            _one.put("ChanneAssetId", one.get("id"));
            _one.put("ChanneId", one.get("channelId"));
            _one.put("MediaType", MediaType.buildByTabName(one.get("assetType")+"").getTypeName());
            _one.put("ContentId", one.get("assetId"));
            _one.put("LoopSort", one.get("loopSort"));
            if (one.get("title")!=null&&!StringUtils.isNullOrEmptyOrSpace(one.get("title")+"")) _one.put("ContentName", one.get("title")+"");
            if (one.get("imgUrl")!=null&&!StringUtils.isNullOrEmptyOrSpace(one.get("imgUrl")+"")) _one.put("ContentLoopImg", one.get("imgUrl")+"");
            ret.add(_one);
        }
        return ret;
    }
}