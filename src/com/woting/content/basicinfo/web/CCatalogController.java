package com.woting.content.basicinfo.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.ui.tree.EasyUiTree;
import com.spiritdata.framework.ui.tree.UiTree;
import com.spiritdata.framework.ui.tree.ZTree;
import com.woting.WtContentMngConstants;
import com.woting.crawlerdb.dict.mem._CacheCDictionary;
import com.woting.crawlerdb.dict.model.CDictDetail;
import com.woting.crawlerdb.dict.model.CDictModel;

/**
 * 抓取分类(字典)信息前台控制
 * @author wangbingqiong
 */
@Controller
@RequestMapping(value="/baseinfo/")
public class CCatalogController {
	
	/**
     * 为前台显示获取栏目
     */
    @SuppressWarnings("unchecked")
	@RequestMapping(value="getCCatalogTree4View.do")
    @ResponseBody
    public Map<String,Object> getCCatalogTree4View(HttpServletRequest request) {
    	 Map<String,Object> map=new HashMap<String, Object>();
         try {
        	 
             String treeType="ZTREE";
             int sizeLimit=0;

             _CacheCDictionary _cd=((CacheEle<_CacheCDictionary>)SystemCache.getCache(WtContentMngConstants.CACHE_CDICT)).getContent();
             try {
                 CDictModel dm=_cd.getCDictModelById("3");
                 TreeNode<CDictDetail> root=dm.cdictTree;
                 if (root==null) {
                     map.put("ReturnType", "1002");
                     map.put("Message", "未找到分类");
                     return map;
                 }
                 UiTree<CDictDetail> uiTree=null;
                 if (root.getAllCount()<sizeLimit||sizeLimit==0) {
                     if (treeType.equals("ZTREE")) uiTree=new ZTree<CDictDetail>(root);
                     else
                     if (treeType.equals("EASYUITREE")) uiTree=new EasyUiTree<CDictDetail>(root);
                 } else {
                     if (treeType.equals("ZTREE")) uiTree=new ZTree<CDictDetail>(root, sizeLimit);
                     else
                     if (treeType.equals("EASYUITREE")) uiTree=new EasyUiTree<CDictDetail>(root, sizeLimit);
                 }
                 if (uiTree!=null) {
                     map.put("ReturnType", "1001");
                     map.put("Data", uiTree.toTreeMap());
                 } else {
                     map.put("ReturnType", "1011");
                 }
             } catch (CloneNotSupportedException e) {
                 map.put("jsonType", "2");
                 map.put("err", e.getMessage());
                 e.printStackTrace();
             }
             return map;
         } catch(Exception e) {
             e.printStackTrace();
             map.put("ReturnType", "T");
             map.put("TClass", e.getClass().getName());
             map.put("Message", e.getMessage());
             return map;
         }
     }
}
