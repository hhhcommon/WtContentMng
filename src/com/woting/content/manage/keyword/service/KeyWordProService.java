package com.woting.content.manage.keyword.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import com.woting.cm.core.channel.persis.po.ChannelAssetPo;
import com.woting.cm.core.keyword.persis.po.KeyWordPo;
import com.woting.cm.core.keyword.service.KeyWordBaseService;
import com.woting.content.manage.channel.service.ChannelContentService;

@Service
public class KeyWordProService {
	@Resource
	private KeyWordBaseService keyWordBaseService;
	@Resource
	private ChannelContentService channelContentService;
	
	public List<Map<String, Object>> getKeyWordList(String tagType, String userId, String typeId, String tagsize) {
		List<KeyWordPo> kwlist = keyWordBaseService.getRandKeyWordByOwner(tagType, userId, typeId, tagsize);
		List<Map<String, Object>> kwre = new ArrayList<>();
		if (kwlist!=null && kwlist.size()>0) {
			for (KeyWordPo kw : kwlist) {
				Map<String, Object> m = new HashMap<>();
				m.put("TagName", kw.getKwName());
				m.put("nPy", kw.getnPy());
				m.put("TagId", kw.getId());
				m.put("Sort", kw.getSort());
				m.put("CTime", kw.getcTime());
				if (tagType.equals("1")) {
					m.put("TagOrg", "公共标签");
				} else {
					if (tagType.equals("2")) {
						m.put("TagOrg", "我的标签");
					}
				}
				kwre.add(m);
			}
			if (kwre!=null && kwre.size()>0) {
				return kwre;
			}
		}
		return null;
	}
	
	public List<Map<String, Object>> getKeyWordListBySeqMedia(String seqmediaid, String tagType, String userId, String tagsize) {
		ChannelAssetPo cha = channelContentService.getChannelAssetByAssetIdAndPubId(seqmediaid, userId, "wt_SeqMediaAsset");
		List<KeyWordPo> kwlist = keyWordBaseService.getRandKeyWordByOwner(tagType, userId, cha.getChannelId(), tagsize);
		List<Map<String, Object>> kwre = new ArrayList<>();
		if (kwlist!=null && kwlist.size()>0) {
			for (KeyWordPo kw : kwlist) {
				Map<String, Object> m = new HashMap<>();
				m.put("TagName", kw.getKwName());
				m.put("nPy", kw.getnPy());
				m.put("TagId", kw.getId());
				m.put("Sort", kw.getSort());
				m.put("CTime", kw.getcTime());
				kwre.add(m);
			}
			if (kwre!=null && kwre.size()>0) {
				return kwre;
			}
		}
		return null;
	}
	
	public List<Map<String, Object>> getKeyWordListByAssetId(String assetId, String resTableName) {
		List<KeyWordPo> kws = keyWordBaseService.getKeyWordsByAssetId(assetId, resTableName);
		if (kws!=null && kws.size()>0) {
			List<Map<String, Object>> kwlist = new ArrayList<>();
			for (KeyWordPo kw : kws) {
				Map<String, Object> m = new HashMap<>();
				m.put("TagId", kw.getId());
				m.put("TagName", kw.getKwName());
				m.put("TagSort", kw.getSort());
				m.put("NamePy", kw.getnPy());
				m.put("CTime", kw.getcTime());
				kwlist.add(m);
			}
			return kwlist;
		}
		return null;
	}
}
