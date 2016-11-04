package com.woting.content.manage.utils;

import java.util.HashMap;
import java.util.Map;

public class ChannelUtils {

	public static Map<String, Object> buildChanelMap(Map<String, Object> nodemap) {
		Map<String, Object> m = new HashMap<>();
		m.put("nodeName", nodemap.get("nodeName")+"");
		m.put("id", nodemap.get("id")+"");
		m.put("parentId", nodemap.get("parentId"));
		m.put("isParent", nodemap.get("isParent"));
		return m;
	}
}
