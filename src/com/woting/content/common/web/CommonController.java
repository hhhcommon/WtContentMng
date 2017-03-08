package com.woting.content.common.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.core.model.tree.TreeNodeBean;
import com.spiritdata.framework.ui.tree.ZTree;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.framework.util.TreeUtils;
import com.woting.WtContentMngConstants;
import com.woting.cm.core.channel.mem._CacheChannel;
import com.woting.cm.core.channel.model.Channel;
import com.woting.cm.core.channel.persis.po.ChannelPo;
import com.woting.cm.core.channel.service.ChannelService;
import com.woting.cm.core.dict.mem._CacheDictionary;
import com.woting.cm.core.dict.model.DictDetail;
import com.woting.cm.core.dict.model.DictModel;
import com.woting.cm.core.subscribe.SubscribeThread;
import com.woting.cm.core.subscribe.service.SubscribeService;
import com.spiritdata.framework.util.RequestUtils;
import com.woting.content.manage.dict.service.DictContentService;

import org.jsoup.nodes.Document;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

@Controller
@RequestMapping(value="/common/")
public class CommonController {
	@Resource
	private ChannelService channelService;
	@Resource
	private DictContentService dictdService;
	@Resource
	private SubscribeService subscribeService;
	
	private _CacheDictionary _cd=null;
    private _CacheChannel _cc=null;

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void initParam() {
        _cd=(SystemCache.getCache(WtContentMngConstants.CACHE_DICT)==null?null:((CacheEle<_CacheDictionary>)SystemCache.getCache(WtContentMngConstants.CACHE_DICT)).getContent());
        _cc=(SystemCache.getCache(WtContentMngConstants.CACHE_CHANNEL)==null?null:((CacheEle<_CacheChannel>)SystemCache.getCache(WtContentMngConstants.CACHE_CHANNEL)).getContent());
    }

	/**
	 * 获取内容分类树
	 * @param request
	 * @return
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value="getCataTreeWithSelf.do")
    @ResponseBody
    public Map<String,Object> getCataTreeWithSelf(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
        Map<String, Object> m=RequestUtils.getDataFromRequest(request);
        String cataId=(String)m.get("cataId");
        if (!StringUtils.isNullOrEmptyOrSpace(cataId)&&!cataId.equals("null")) {
			_CacheDictionary _cd=( (CacheEle<_CacheDictionary>)SystemCache.getCache(WtContentMngConstants.CACHE_DICT)).getContent();
            try {
                DictModel dm=_cd.getDictModelById(cataId);
                ZTree<DictDetail> eu1=new ZTree<DictDetail>(dm.dictTree);
                map.put("jsonType", "1");
                map.put("data", eu1.toTreeMap());
            } catch (Exception e) {
                map.put("jsonType", "2");
                map.put("err", e.getMessage());
                e.printStackTrace();
            }
        } else {
            map.put("jsonType", "2");
            map.put("err", "无法得到正确的分类Id");
        }
        return map;
    }

    /**
     * 获取栏目列表
     * @param request
     * @return
     */
    @RequestMapping(value="getChannelTreeWithSelf.do")
    @ResponseBody
    public Map<String,Object> getChannelTreeWithSelf(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<String, Object>();
         _CacheChannel _cd=channelService.loadCache();
         try {
			ZTree<Channel> zc = new ZTree<>(_cd.channelTree);
			map.put("jsonType", "1");
            map.put("data", zc.toTreeMap());
		} catch (Exception e) {
			map.put("jsonType", "2");
            map.put("err", e.getMessage());
			e.printStackTrace();
		}
        return map;
    }
    
    @RequestMapping(value="getCategoryTags.do")
    @ResponseBody
    public Map<String,Object> getCategoryTags(HttpServletRequest request) {
    	Map<String,Object> map=new HashMap<String, Object>();
        Map<String, Object> m=RequestUtils.getDataFromRequest(request);
        String dictdid = m.get("CategoryId")+"";
        if(dictdid.equals("null")){
        	map.put("ReturnType", "1002");
        	map.put("Message", "缺少内容分类id");
        	return map;
        }
        List<Map<String, Object>> dictdlist = dictdService.getkeywordByDictMid("6", 8);
        if(dictdlist!=null&&dictdlist.size()>0){
        	map.put("ReturnType", "1001");
        	map.put("AllCount", dictdlist.size());
        	map.put("ResultList", dictdlist);
        }
        return map;
    }

    @RequestMapping(value="jsonp.do")
    @ResponseBody
    /**
     * 用jsonP的方式获取数据
     * @param request 其中必须有RemoteUrl参数
     * @return json结构
     */
    public Map<String, Object> jsonp(HttpServletRequest request) throws IOException {
        //获取参数
        Map<String, Object> m=RequestUtils.getDataFromRequest(request);
        Map<String, Object> map = new HashMap<>();
        //1-获取地址：
        String remoteUrl=m.get("RemoteUrl")+"";
        if (StringUtils.isNullOrEmptyOrSpace(remoteUrl)||remoteUrl.toLowerCase().equals("null")) {
        	map.put("ReturnType", "0000");
        	map.put("Message", "无法获得远程Url");
            return map;
        }
        m.remove("RemoteUrl");
        Connection conn=Jsoup.connect(remoteUrl);
        for (String key: m.keySet()) {
            if (m.get(key)!=null) conn.data(key, m.get(key)+"");
        }
        Document doc=conn.timeout(10000).ignoreContentType(true).get();
        String str=doc.select("body").html().toString();
        str=str.replaceAll("\"", "'");
        str=str.replaceAll("\n", "");
        str=str.replaceAll("&quot;", "\"");
        str=str.replaceAll("\r", "");
    	map.put("Data", str);
        return map;
    }
    
    @RequestMapping(value="subscribe.do")
    @ResponseBody
    /**
     * 用jsonP的方式获取数据
     * @param request 其中必须有RemoteUrl参数
     * @return json结构
     */
    public Map<String, Object> makeSubscribe(HttpServletRequest request) throws IOException {
        //获取参数
        Map<String, Object> m=RequestUtils.getDataFromRequest(request);
        Map<String, Object> map = new HashMap<>();
        //1-获取地址：
        String maId=m.get("ContentId")+"";
        if (StringUtils.isNullOrEmptyOrSpace(maId)||maId.toLowerCase().equals("null")) {
        	map.put("ReturnType", "1011");
        	map.put("Message", "无法获得内容Id");
            return map;
        }
        new SubscribeThread(maId).start();
        map.put("ReturnType", "1001");
        return map;
    }
    
    /**
	 * 搜索内容请求
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getCatalogInfo.do")
	@ResponseBody
	public Map<String, Object> getPersonStatus(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> m = RequestUtils.getDataFromRequest(request);
		//1-得到模式Id
        String catalogType=(m.get("CatalogType")==null?null:m.get("CatalogType")+"");
        if (StringUtils.isNullOrEmptyOrSpace(catalogType)) catalogType="-1";
        //2-得到字典项Id或父栏目Id
        String catalogId=(m.get("CatalogId")==null?null:m.get("CatalogId")+"");
        if (StringUtils.isNullOrEmptyOrSpace(catalogId)) catalogId=null;
        //3-得到返回类型
        int resultType=2;
        try {resultType=Integer.parseInt(m.get("ResultType")+"");} catch(Exception e) {}
        //4-得到相对层次
        int relLevel=0;
        try {relLevel=Integer.parseInt(m.get("RelLevel")+"");} catch(Exception e) {}

        //根据分类获得根
        TreeNode<? extends TreeNodeBean> root=null;
        if (catalogType.equals("-1")) {
            root=_cc.channelTree;
        } else {
            DictModel dm=_cd.getDictModelById(catalogType);
            if (dm!=null&&dm.dictTree!=null) root=dm.dictTree;
        }
        //获得相应的结点，通过查找
        if (root!=null&&catalogId!=null) root=root.findNode(catalogId);
        //根据层级参数，对树进行截取
        if (root!=null&&relLevel>0) root=TreeUtils.cutLevelClone(root, relLevel);

        if (root!=null) {
            Map<String, Object> CatalogData=new HashMap<String, Object>();
            //返回类型
            if (resultType==1) {//树结构
                convert2Data(root, CatalogData, catalogType);
                map.put("CatalogData", CatalogData);
            } else {//列表结构
                if (relLevel<=0) {//所有结点列表
                    map.put("CatalogData", getDeepList(root, catalogType));
                } else { //某层级节点
                    map.put("CatalogData", getLevelNodeList(root, relLevel, catalogType));
                }
            }
            map.put("ReturnType", "1001");
        } else {
            map.put("Message", "无符合条件的"+(catalogType.equals("-1")?"栏目":"分类")+"信息");
            map.put("ReturnType", "1011");
        }
        return map;
	}
    
    private void convert2Data(TreeNode<? extends TreeNodeBean> t, Map<String, Object> retData, String catalogType) {
        if (retData!=null&&t!=null) {
            retData.put("CatalogType", catalogType);
            retData.put("CatalogId", t.getId());
            retData.put("CatalogName", t.getNodeName());
            if (!t.isLeaf()) {
                List<Map<String, Object>> subCata=new ArrayList<Map<String, Object>>();
                for (TreeNode<? extends TreeNodeBean> _t: t.getChildren()) {
                    Map<String, Object> m=new HashMap<String, Object>();
                    convert2Data(_t, m, catalogType);
                    subCata.add(m);
                }
                retData.put("SubCata", subCata);
            }
        }
    }
    
    private List<Map<String, Object>> getDeepList(TreeNode<? extends TreeNodeBean> t, String catalogType) {
        if (t==null) return null;
        List<Map<String, Object>> ret=new ArrayList<Map<String, Object>>();
        if (!t.isLeaf()) {
            for (TreeNode<? extends TreeNodeBean> _t: t.getChildren()) {
                Map<String, Object> m=new HashMap<String, Object>();
                m.put("CatalogType", catalogType);
                m.put("CatalogId", _t.getId());
                m.put("CatalogName", _t.getNodeName());
                ret.add(m);
                List<Map<String, Object>> _r=getDeepList(_t, catalogType);
                if (_r!=null) ret.addAll(_r);
            }
            return ret;
        } else return null;
    }
    private List<Map<String, Object>> getLevelNodeList(TreeNode<? extends TreeNodeBean> t, int level, String catalogType) {
        if (t==null) return null;
        List<Map<String, Object>> ret=new ArrayList<Map<String, Object>>();
        if (!t.isLeaf()) {
            for (TreeNode<? extends TreeNodeBean> _t: t.getChildren()) {
                if (level==1) {
                    Map<String, Object> m=new HashMap<String, Object>();
                    m.put("CatalogType", catalogType);
                    m.put("CatalogId", _t.getId());
                    m.put("CatalogName", _t.getNodeName());
                    ret.add(m);
                } else {
                    List<Map<String, Object>> _r=getLevelNodeList(_t, level-1, catalogType);
                    if (_r!=null) ret.addAll(_r);
                }
            }
            return ret;
        } else return null;
    }
}