package com.woting.content.manage.dict.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.ui.tree.EasyUiTree;
import com.spiritdata.framework.util.SequenceUUID;
import com.woting.WtContentMngConstants;
import com.woting.cm.core.dict.mem._CacheDictionary;
import com.woting.cm.core.dict.model.DictDetail;
import com.woting.cm.core.dict.model.DictModel;
import com.woting.cm.core.dict.model.DictRefRes;
import com.woting.cm.core.media.service.MediaService;

public class DictContentService {
	@Resource
	private MediaService mediaService;
	
	public void addCataLogs(String mid, String did, String mediatype, String assetid) {
		// 保存专辑分类信息到wt_ResDict_Ref
		try {
			_CacheDictionary _cd = ((CacheEle<_CacheDictionary>) SystemCache.getCache(WtContentMngConstants.CACHE_DICT))
					.getContent();
			DictModel dm = _cd.getDictModelById(mid);
			EasyUiTree<DictDetail> eu1 = new EasyUiTree<DictDetail>(dm.dictTree);
			Map<String, Object> m = eu1.toTreeMap();
			String refname = "";
			String restablename = "";
			if (mediatype.equals("wt_MediaAsset")) {
				refname = "单体-内容分类";
				restablename = "wt_MediaAsset";
			} else {
				if (mediatype.equals("wt_SeqMediaAsset")) {
					refname = "专辑-内容分类";
					restablename = "wt_SeqMediaAsset";
				} else {
					if(mediatype.equals("wt_Broadcast")) {
						refname = "电台-内容分类";
						restablename = "wt_Broadcast";
					}
				}
			}
			List<Map<String, Object>> chillist = (List<Map<String, Object>>) m.get("children");
			for (Map<String, Object> map2 : chillist) {
				if (map2.get("id").equals(did)) {
					DictRefRes dicres = new DictRefRes();
					dicres.setId(SequenceUUID.getPureUUID());
					dicres.setRefName(refname);
					dicres.setResTableName(restablename);
					dicres.setResId(assetid);
					dicres.setDm(dm);
					DictDetail dicd = new DictDetail();
					dicd.setId(map2.get("id") + "");
					dicd.setMId(mid);
					Map<String, Object> l = (Map<String, Object>) map2.get("attributes");
					dicd.setBCode(l.get("bCode") + "");
					dicd.setDdName(l.get("nodeName") + "");
					dicres.setDd(dicd);
					dicres.setCTime(new Timestamp(System.currentTimeMillis()));
					mediaService.saveDictRef(dicres);
				}
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
}
