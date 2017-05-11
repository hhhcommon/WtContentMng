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
		List<Map<String, Object>> kwre = makeReturnList(tagType, kwlist, userId);
		if (kwre!=null && kwre.size()>0) {
			return kwre;
		}
		return null;
	}

	public List<Map<String, Object>> getKeyWordListBySeqMedia(String seqmediaid, String tagType, String userId, String tagsize) {
		ChannelAssetPo cha = channelContentService.getChannelAssetByAssetIdAndPubId(seqmediaid, userId, "wt_SeqMediaAsset");
		List<KeyWordPo> kwlist=null;
		if (cha==null) {
		    kwlist = keyWordBaseService.getRandKeyWordByOwner(tagType, userId, null, tagsize);
		} else {
		    kwlist = keyWordBaseService.getRandKeyWordByOwner(tagType, userId, cha.getChannelId(), tagsize);
		}
//		List<KeyWordPo> ownkws = keyWordBaseService.getKeyWordsByAssetId(userId, "palt_User");
		List<Map<String, Object>> kwre = makeReturnList(tagType, kwlist, userId);
		if (kwre!=null && kwre.size()>0) {
			return kwre;
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
	
	public void removeKeyWordByAssetId(String assetId, String resTableName) {
		keyWordBaseService.deleteKeyWordRes(assetId, resTableName);
	}

	public List<Map<String, Object>> getKeyWordList(String tagType, String userid, String tagsize) {
		List<KeyWordPo> kws = keyWordBaseService.getRandKeyWordByOwner(tagType, userid, null, tagsize);
//		List<KeyWordPo> ownkws = keyWordBaseService.getKeyWordsByAssetId(userid, "palt_User");
		List<Map<String, Object>> kwre = makeReturnList(tagType, kws, userid);
		if (kwre!=null && kwre.size()>0) {
			return kwre;
		}
		return null;
	}
	
	private List<Map<String, Object>> makeReturnList(String tagType, List<KeyWordPo> kws, String userId) {
		if (tagType.equals("1")) {
			if (kws!=null && kws.size()>0) {
				List<Map<String, Object>> kwre = new ArrayList<>();
				for (KeyWordPo kw : kws) {
					Map<String, Object> m = new HashMap<>();
					m.put("TagName", kw.getKwName());
					m.put("nPy", kw.getnPy());
					m.put("TagId", kw.getId());
					m.put("Sort", kw.getSort());
					m.put("CTime", kw.getcTime());
					m.put("TagOrg", "公共标签");
					kwre.add(m);
				}
				if (kwre!=null && kwre.size()>0) {
					return kwre;
				}
			}
		}
		if (tagType.equals("2")) {
			if (kws!=null && kws.size()>0) {
				List<Map<String, Object>> kwre = new ArrayList<>();
				for (KeyWordPo kw : kws) {
					Map<String, Object> m = new HashMap<>();
					m.put("TagName", kw.getKwName());
					m.put("nPy", kw.getnPy());
					m.put("TagId", kw.getId());
					m.put("Sort", kw.getSort());
					m.put("CTime", kw.getcTime());
					m.put("TagOrg", "我的标签");
					kwre.add(m);
				}
				if (kwre!=null && kwre.size()>0) {
					return kwre;
				}
			}
		}
		if (tagType.equals("3")) {
			if (kws!=null && kws.size()>0) {
				List<Map<String, Object>> kwre = new ArrayList<>();
				List<KeyWordPo> ownkws = keyWordBaseService.getKeyWordsByAssetId(userId, "palt_User");
				for (KeyWordPo kw : kws) {
					Map<String, Object> m = new HashMap<>();
					m.put("TagName", kw.getKwName());
					m.put("nPy", kw.getnPy());
					m.put("TagId", kw.getId());
					m.put("Sort", kw.getSort());
					m.put("CTime", kw.getcTime());
					if (ownkws!=null && ownkws.size()>0) {
						for (KeyWordPo k : ownkws) {
							if (kw.getId().equals(k.getId())) {
								m.put("TagOrg", "我的标签");
							}
						}
					}
					if (!m.containsKey("TagOrg")) {
						m.put("TagOrg", "公共标签");
					}
					kwre.add(m);
				}
				if (kwre!=null && kwre.size()>0) {
					return kwre;
				}
			}
		}
		return null;
	}
	
	public List<Map<String, Object>> getKeyWordsByIds(String ids, String mediaType) {
		Map<String, Object> m = new HashMap<>();
		m.put("ids", ids);
		m.put("resTableName", mediaType);
		List<Map<String, Object>> ls = keyWordBaseService.getKeyWordsByIdsAndResTableName(m);
		if (ls!=null) {
			return ls;
		}
		return null;
	}
}
