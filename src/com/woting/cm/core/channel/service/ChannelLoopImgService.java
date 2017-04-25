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
    public Map<String, Object> getLoopImgList(String mediaType, String channelId, int pageSize, int pageIndex) {
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
        int count=0;
        if (pageIndex==0) {
        	_ret=channelAssetDao.queryForListAutoTranform("getLoopImgList", param);
        	count=channelAssetDao.getCount("getLoopImgListCount");
        } else {
            Page<Map<String, Object>> page=channelAssetDao.pageQueryAutoTranform("getLoopImgListCount", "getLoopImgList", param, pageIndex, pageSize);
            if (page!=null&&page.getDataCount()>0) {
                _ret=new ArrayList<Map<String, Object>>();
                _ret.addAll(page.getResult());
                count=page.getDataCount();
            }
        }
        if (_ret==null||_ret.isEmpty()) return null;

        List<Map<String, Object>> ret=new ArrayList<Map<String, Object>>(_ret.size());
        for (int i=0; i<_ret.size(); i++) {
            Map<String, Object> one=_ret.get(i);
            Map<String, Object> _one=new HashMap<String, Object>();
            _one.put("ChanneAssetId", one.get("id"));
            _one.put("ChanneId", channelId);
            _one.put("MediaType", MediaType.buildByTabName(one.get("assetType")+"").getTypeName());
            _one.put("ContentId", one.get("assetId"));
            _one.put("LoopSort", one.get("loopSort"));
            if (one.get("title")!=null&&!StringUtils.isNullOrEmptyOrSpace(one.get("title")+"")) _one.put("ContentName", one.get("title")+"");
            if (one.get("imgUrl")!=null&&!StringUtils.isNullOrEmptyOrSpace(one.get("imgUrl")+"")) _one.put("ContentLoopImg", one.get("imgUrl")+"");
            ret.add(_one);
        }
        Map<String, Object> retM=new HashMap<String, Object>();
        retM.put("ResultList", ret);
        retM.put("AllCount", count);
        return retM;
    }
    
    /**
     * 某一栏目下轮播图排序
     * @param mediaType 过滤条件，按类型过滤
     * @param channelId 栏目Id
     * @param contentId 内容Id
     * @param loopSort =-1:上移; =-2:下移
     */
    @SuppressWarnings("unchecked")
	public boolean updateLoopSort(String mediaType, String channelId, String contentId, int loopSort) {
    	if (StringUtils.isNullOrEmptyOrSpace(channelId) || StringUtils.isNullOrEmptyOrSpace(contentId) || loopSort==0) return false;

    	Map<String, Object> map = getLoopImgList(mediaType, channelId, 0, 0);
    	List<Map<String, Object>> loopImageList=(List<Map<String, Object>>) map.get("ResultList");
    	if (loopImageList==null || loopImageList.size()<=1) return false;
    	
    	Map<String, Object> param=new HashMap<String, Object>();
    	int index=-1;
    	for(int i=0; i<loopImageList.size(); i++) {
    		if (loopImageList.get(i).get("ContentId").equals(contentId)) {// 查找所要操作的内容所在的位置
    			index=i+1;
    			break;
    		}
    	}
    	param.put("loopSort", loopSort);
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
        // 排序
    	if (loopSort==-1) {// 上移
    		if (index <= 0) return false; 
    		if (loopImageList.get(index-1).get("ContentId")==null) return false;
    		String _contentId = loopImageList.get(index-1).get("ContentId").toString();
    		param.put("assetId", contentId);
    		param.put("_assetId", _contentId);
    		param.put("loopSort", loopSort);
    		param.put("_loopSort", loopSort+1);
    	} else if (loopSort==-2) {// 下移
    		if (index >= loopImageList.size()-1) return false; 
    		if (loopImageList.get(index-1).get("ContentId")==null) return false;
    		String _contentId = loopImageList.get(index+1).get("ContentId").toString();
    		param.put("assetId", contentId);
    		param.put("_assetId", _contentId);
    		param.put("loopSort", loopSort);
    		param.put("_loopSort", loopSort-1);
    	} else if (loopSort>0) {// 根据参数设置所在位置
    		if (index == loopSort) return false;// 有错误
    		String _contentId = loopImageList.get(loopSort).get("ContentId").toString();
    		param.put("assetId", contentId);
    		param.put("_assetId", _contentId);
    		param.put("loopSort", loopSort);
    		param.put("_loopSort", index);
    	}
    	try {
			channelAssetDao.update("updateLoopSort", param);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
    
    /**
     * 删除某栏目下的轮播图  实际上并没有删除数据  只是将loopSort设置为0
     * @param mediaType 过滤条件，按类型过滤
     * @param channelId 栏目Id
     * @param contentId 内容Id
     */
    public boolean deleteLoopImg(String mediaType, String channelId, String contentId) {
    	if (StringUtils.isNullOrEmptyOrSpace(channelId) || StringUtils.isNullOrEmptyOrSpace(contentId)) return false;

    	Map<String, Object> param=new HashMap<String, Object>();
    	param.put("channelId", channelId);
    	param.put("assetId", contentId);
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
    	try {
			channelAssetDao.update("deleteLoopImg", param);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
    
    /**
     * 某一栏目下添加轮播图
     * @param mediaType 过滤条件，按类型过滤
     * @param channelId 栏目Id
     * @param contentId 内容Id
     * @param imgeUrl 轮播图地址
     * @param loopSort 轮播图排序号
     */
    @SuppressWarnings("unchecked")
	public boolean addLoopImg(String mediaType, String channelId, String contentId, String imageUrl, int sort) {
    	if (StringUtils.isNullOrEmptyOrSpace(channelId) || StringUtils.isNullOrEmptyOrSpace(contentId) || StringUtils.isNullOrEmptyOrSpace(imageUrl)) return false;

    	Map<String, Object> param=new HashMap<String, Object>();
    	param.put("channelId", channelId);
    	List<Map<String, Object>> _ret=channelAssetDao.queryForListAutoTranform("getChannelLoopImgCount", param);
    	int loopSort=0;
    	if (_ret!=null&&_ret.size()>=0) {
    		Map<String, Object> map=(Map<String, Object>) _ret.get(0).get(0);
    		loopSort=(int) map.get("loopSort");
    		if (sort>0) loopSort=sort;
    	}
        
    	Map<String, Object> newData=new HashMap<String, Object>();
    	newData.put("channelId", channelId);
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
            if (!assetTypes.isEmpty()) newData.put("assetTypes", assetTypes);
        }
        newData.put("assetId", contentId);
    	newData.put("publisherId", "2");
    	newData.put("checkerId", "2");
    	newData.put("isValidate", 1);
    	newData.put("loopSort", loopSort);
    	newData.put("sort", 0);
    	newData.put("loopImg", imageUrl);
    	newData.put("flowFlag", 2);
    	newData.put("checkRuleIds", "etl");
    	newData.put("pubTime", System.currentTimeMillis());
    	newData.put("cTime", System.currentTimeMillis());
    	
    	try {
			channelAssetDao.insert("addLoopImgInChannel", newData);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
}